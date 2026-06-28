package dev.tuningneo.industrialarmies.entity;

import dev.tuningneo.industrialarmies.army.CombatStance;
import dev.tuningneo.industrialarmies.army.SoldierOrder;
import dev.tuningneo.industrialarmies.army.SoldierRank;
import dev.tuningneo.industrialarmies.entity.ai.FollowCommanderGoal;
import dev.tuningneo.industrialarmies.entity.ai.HoldPositionGoal;
import dev.tuningneo.industrialarmies.network.OpenSoldierSettingsPayload;
import dev.tuningneo.industrialarmies.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public final class CompanySoldierEntity extends TamableAnimal {
    private static final EntityDataAccessor<Integer> ORDER =
            SynchedEntityData.defineId(CompanySoldierEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> RANK =
            SynchedEntityData.defineId(CompanySoldierEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<UUID>> COMPANY_ID =
            SynchedEntityData.defineId(CompanySoldierEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> COMBAT_STANCE =
            SynchedEntityData.defineId(CompanySoldierEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> FOLLOW_DISTANCE =
            SynchedEntityData.defineId(CompanySoldierEntity.class, EntityDataSerializers.INT);

    @Nullable
    private BlockPos holdPosition;

    public CompanySoldierEntity(EntityType<? extends CompanySoldierEntity> type, Level level) {
        super(type, level);
        setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 24.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ORDER, SoldierOrder.FOLLOW.ordinal());
        builder.define(RANK, SoldierRank.RECRUIT.ordinal());
        builder.define(COMPANY_ID, Optional.empty());
        builder.define(COMBAT_STANCE, CombatStance.DEFENSIVE.ordinal());
        builder.define(FOLLOW_DISTANCE, 8);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.15D, true));
        goalSelector.addGoal(3, new HoldPositionGoal(this, 1.05D));
        goalSelector.addGoal(4, new FollowCommanderGoal(this, 1.1D));
        goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.8D) {
            @Override
            public boolean canUse() {
                return getOrder() == SoldierOrder.FOLLOW && super.canUse();
            }
        });
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        targetSelector.addGoal(1, new HurtByTargetGoal(this) {
            @Override
            public boolean canUse() {
                return getCombatStance() != CombatStance.PASSIVE && super.canUse();
            }
        });
        targetSelector.addGoal(2, new OwnerHurtByTargetGoal(this) {
            @Override
            public boolean canUse() {
                return getCombatStance() != CombatStance.PASSIVE && super.canUse();
            }
        });
        targetSelector.addGoal(3, new OwnerHurtTargetGoal(this) {
            @Override
            public boolean canUse() {
                return getCombatStance() != CombatStance.PASSIVE && super.canUse();
            }
        });
        targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Monster.class, true) {
            @Override
            public boolean canUse() {
                return getCombatStance() == CombatStance.AGGRESSIVE && super.canUse();
            }
        });
    }

    @Override
    public boolean isAlliedTo(Entity other) {
        if (super.isAlliedTo(other) || other == getOwner()) {
            return true;
        }
        if (other instanceof CompanySoldierEntity soldier) {
            return getCompanyId().isPresent() && getCompanyId().equals(soldier.getCompanyId());
        }
        return false;
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return getCombatStance() != CombatStance.PASSIVE && !isAlliedTo(target) && super.canAttack(target);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        Entity attacker = source.getEntity();
        if (attacker != null && isAlliedTo(attacker)) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (isOwnedBy(player) && player.getItemInHand(hand).is(ModItems.COMMAND_BATON.get())) {
            if (!level().isClientSide) {
                if (player.isShiftKeyDown()) {
                    PacketDistributor.sendToPlayer((net.minecraft.server.level.ServerPlayer) player,
                            new OpenSoldierSettingsPayload(getId()));
                    return InteractionResult.SUCCESS;
                }
                if (getOrder() == SoldierOrder.FOLLOW) {
                    setHoldPosition(blockPosition());
                    setOrder(SoldierOrder.HOLD);
                } else {
                    setOrder(SoldierOrder.FOLLOW);
                    setHoldPosition(null);
                }
                player.displayClientMessage(Component.translatable(
                        "message.industrial_armies.order." + getOrder().name().toLowerCase()), true);
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    public SoldierOrder getOrder() {
        return SoldierOrder.byId(entityData.get(ORDER));
    }

    public void setOrder(SoldierOrder order) {
        entityData.set(ORDER, order.ordinal());
    }

    public SoldierRank getSoldierRank() {
        return SoldierRank.byId(entityData.get(RANK));
    }

    public void setSoldierRank(SoldierRank rank) {
        entityData.set(RANK, rank.ordinal());
    }

    public Optional<UUID> getCompanyId() {
        return entityData.get(COMPANY_ID);
    }

    public void setCompanyId(UUID companyId) {
        entityData.set(COMPANY_ID, Optional.of(companyId));
    }

    public CombatStance getCombatStance() {
        return CombatStance.byId(entityData.get(COMBAT_STANCE));
    }

    public void setCombatStance(CombatStance stance) {
        entityData.set(COMBAT_STANCE, stance.ordinal());
        if (stance == CombatStance.PASSIVE) {
            setTarget(null);
        }
    }

    public int getFollowDistance() {
        return entityData.get(FOLLOW_DISTANCE);
    }

    public void setFollowDistance(int distance) {
        int normalized = distance <= 4 ? 4 : distance <= 8 ? 8 : 12;
        entityData.set(FOLLOW_DISTANCE, normalized);
    }

    @Nullable
    public BlockPos getHoldPosition() {
        return holdPosition;
    }

    public void setHoldPosition(@Nullable BlockPos holdPosition) {
        this.holdPosition = holdPosition == null ? null : holdPosition.immutable();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Order", getOrder().ordinal());
        tag.putInt("Rank", getSoldierRank().ordinal());
        tag.putInt("CombatStance", getCombatStance().ordinal());
        tag.putInt("FollowDistance", getFollowDistance());
        getCompanyId().ifPresent(uuid -> tag.putUUID("Company", uuid));
        if (holdPosition != null) {
            tag.putInt("HoldX", holdPosition.getX());
            tag.putInt("HoldY", holdPosition.getY());
            tag.putInt("HoldZ", holdPosition.getZ());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setOrder(SoldierOrder.byId(tag.getInt("Order")));
        setSoldierRank(SoldierRank.byId(tag.getInt("Rank")));
        setCombatStance(tag.contains("CombatStance")
                ? CombatStance.byId(tag.getInt("CombatStance"))
                : CombatStance.DEFENSIVE);
        if (tag.contains("FollowDistance")) {
            setFollowDistance(tag.getInt("FollowDistance"));
        }
        if (tag.hasUUID("Company")) {
            setCompanyId(tag.getUUID("Company"));
        }
        if (tag.contains("HoldX") && tag.contains("HoldY") && tag.contains("HoldZ")) {
            setHoldPosition(new BlockPos(tag.getInt("HoldX"), tag.getInt("HoldY"), tag.getInt("HoldZ")));
        }
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null;
    }
}

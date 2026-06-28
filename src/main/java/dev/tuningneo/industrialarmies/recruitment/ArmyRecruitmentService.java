package dev.tuningneo.industrialarmies.recruitment;

import dev.tuningneo.industrialarmies.IndustrialArmies;
import dev.tuningneo.industrialarmies.army.SoldierOrder;
import dev.tuningneo.industrialarmies.entity.CompanySoldierEntity;
import dev.tuningneo.industrialarmies.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public final class ArmyRecruitmentService {
    public static final int BASE_EMERALD_COST = 16;
    public static final int LOCAL_SOLDIER_CAP = 32;
    public static final double ROSTER_CHECK_RADIUS = 128.0D;

    private ArmyRecruitmentService() {
    }

    public static RecruitmentResult recruitAtMusterBell(ServerPlayer player, ServerLevel level, BlockPos spawnPos) {
        long ownedSoldiers = level.getEntitiesOfClass(
                        CompanySoldierEntity.class,
                        new AABB(spawnPos).inflate(ROSTER_CHECK_RADIUS),
                        soldier -> soldier.isOwnedBy(player))
                .size();
        if (ownedSoldiers >= LOCAL_SOLDIER_CAP) {
            return RecruitmentResult.failure("message.industrial_armies.recruitment.local_cap");
        }

        if (!player.isCreative() && countItem(player, Items.EMERALD) < BASE_EMERALD_COST) {
            return RecruitmentResult.failure("message.industrial_armies.recruitment.missing_payment");
        }

        CompanySoldierEntity soldier = new CompanySoldierEntity(ModEntities.COMPANY_SOLDIER.get(), level);
        soldier.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D,
                player.getYRot(), 0.0F);
        soldier.setTame(true, true);
        soldier.setOwnerUUID(player.getUUID());
        soldier.setCompanyId(companyIdFor(player.getUUID()));
        soldier.setOrder(SoldierOrder.FOLLOW);
        soldier.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
        soldier.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.LEATHER_HELMET));
        soldier.setCustomName(Component.translatable("entity.industrial_armies.company_soldier"));

        if (!level.noCollision(soldier) || !level.addFreshEntity(soldier)) {
            return RecruitmentResult.failure("message.industrial_armies.recruitment.no_space");
        }

        if (!player.isCreative()) {
            removeItem(player, Items.EMERALD, BASE_EMERALD_COST);
        }
        level.playSound(null, spawnPos, SoundEvents.VILLAGER_YES, SoundSource.PLAYERS, 1.0F, 0.9F);
        return RecruitmentResult.success(soldier);
    }

    public static UUID companyIdFor(UUID ownerId) {
        return UUID.nameUUIDFromBytes((IndustrialArmies.MOD_ID + ":company:" + ownerId)
                .getBytes(StandardCharsets.UTF_8));
    }

    private static int countItem(ServerPlayer player, net.minecraft.world.item.Item item) {
        int count = 0;
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (stack.is(item)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private static void removeItem(ServerPlayer player, net.minecraft.world.item.Item item, int amount) {
        int remaining = amount;
        for (int slot = 0; slot < player.getInventory().getContainerSize() && remaining > 0; slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (!stack.is(item)) {
                continue;
            }
            int removed = Math.min(remaining, stack.getCount());
            stack.shrink(removed);
            remaining -= removed;
        }
        player.getInventory().setChanged();
    }

    public record RecruitmentResult(boolean success, Component message, CompanySoldierEntity soldier) {
        public static RecruitmentResult success(CompanySoldierEntity soldier) {
            return new RecruitmentResult(true,
                    Component.translatable("message.industrial_armies.recruitment.success", BASE_EMERALD_COST),
                    soldier);
        }

        public static RecruitmentResult failure(String translationKey) {
            return new RecruitmentResult(false, Component.translatable(translationKey), null);
        }
    }
}

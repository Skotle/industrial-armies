package dev.tuningneo.industrialarmies.item;

import dev.tuningneo.industrialarmies.army.SoldierOrder;
import dev.tuningneo.industrialarmies.entity.CompanySoldierEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;

public final class CommandBatonItem extends Item {
    private static final double COMMAND_RADIUS = 64.0D;

    public CommandBatonItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.industrial_armies.command_baton.single"));
        tooltip.add(Component.translatable("tooltip.industrial_armies.command_baton.settings"));
        tooltip.add(Component.translatable("tooltip.industrial_armies.command_baton.group"));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!(context.getPlayer() instanceof ServerPlayer player)
                || !(context.getLevel() instanceof ServerLevel level)) {
            return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
        }
        BlockPos target = context.getClickedPos().relative(context.getClickedFace());
        List<CompanySoldierEntity> soldiers = ownedSoldiers(level, player);
        soldiers.forEach(soldier -> {
            soldier.setOrder(SoldierOrder.HOLD);
            soldier.setHoldPosition(target);
            soldier.getNavigation().moveTo(target.getX() + 0.5D, target.getY(), target.getZ() + 0.5D, 1.1D);
        });
        player.displayClientMessage(Component.translatable(
                "message.industrial_armies.command.hold_group", soldiers.size()), true);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
            List<CompanySoldierEntity> soldiers = ownedSoldiers(serverLevel, serverPlayer);
            soldiers.forEach(soldier -> {
                soldier.setOrder(SoldierOrder.FOLLOW);
                soldier.setHoldPosition(null);
            });
            serverPlayer.displayClientMessage(Component.translatable(
                    "message.industrial_armies.command.follow_group", soldiers.size()), true);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    private static List<CompanySoldierEntity> ownedSoldiers(ServerLevel level, ServerPlayer player) {
        return level.getEntitiesOfClass(
                CompanySoldierEntity.class,
                player.getBoundingBox().inflate(COMMAND_RADIUS),
                soldier -> soldier.isOwnedBy(player));
    }
}

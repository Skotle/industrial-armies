package dev.tuningneo.industrialarmies.item;

import dev.tuningneo.industrialarmies.recruitment.ArmyRecruitmentService;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public final class EnlistmentContractItem extends Item {
    public EnlistmentContractItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.industrial_armies.enlistment_contract"));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!context.getLevel().getBlockState(context.getClickedPos()).is(Blocks.BELL)) {
            return InteractionResult.PASS;
        }
        if (!(context.getPlayer() instanceof ServerPlayer player)
                || !(context.getLevel() instanceof ServerLevel level)) {
            return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
        }

        BlockPos spawnPos = context.getClickedPos().relative(context.getClickedFace());
        ArmyRecruitmentService.RecruitmentResult result =
                ArmyRecruitmentService.recruitAtMusterBell(player, level, spawnPos);
        player.displayClientMessage(result.message(), true);
        if (result.success() && !player.isCreative()) {
            context.getItemInHand().shrink(1);
        }
        return result.success() ? InteractionResult.SUCCESS : InteractionResult.FAIL;
    }
}

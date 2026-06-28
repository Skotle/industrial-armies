package dev.tuningneo.industrialarmies.entity.ai;

import dev.tuningneo.industrialarmies.army.SoldierOrder;
import dev.tuningneo.industrialarmies.entity.CompanySoldierEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public final class HoldPositionGoal extends Goal {
    private final CompanySoldierEntity soldier;
    private final double speed;
    private int recalculatePath;

    public HoldPositionGoal(CompanySoldierEntity soldier, double speed) {
        this.soldier = soldier;
        this.speed = speed;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        BlockPos holdPosition = soldier.getHoldPosition();
        return soldier.getOrder() == SoldierOrder.HOLD
                && holdPosition != null
                && !soldier.blockPosition().closerThan(holdPosition, 2.0D);
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void start() {
        recalculatePath = 0;
    }

    @Override
    public void tick() {
        BlockPos holdPosition = soldier.getHoldPosition();
        if (holdPosition == null) {
            return;
        }
        if (--recalculatePath <= 0) {
            recalculatePath = adjustedTickDelay(10);
            soldier.getNavigation().moveTo(
                    holdPosition.getX() + 0.5D,
                    holdPosition.getY(),
                    holdPosition.getZ() + 0.5D,
                    speed);
        }
    }

    @Override
    public void stop() {
        soldier.getNavigation().stop();
    }
}

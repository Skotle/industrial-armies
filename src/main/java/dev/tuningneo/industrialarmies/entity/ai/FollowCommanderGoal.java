package dev.tuningneo.industrialarmies.entity.ai;

import dev.tuningneo.industrialarmies.army.SoldierOrder;
import dev.tuningneo.industrialarmies.entity.CompanySoldierEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public final class FollowCommanderGoal extends Goal {
    private final CompanySoldierEntity soldier;
    private final double speed;
    @Nullable
    private LivingEntity commander;
    private int pathRecalculationDelay;

    public FollowCommanderGoal(CompanySoldierEntity soldier, double speed) {
        this.soldier = soldier;
        this.speed = speed;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity owner = soldier.getOwner();
        if (owner == null || owner.isSpectator() || soldier.getOrder() != SoldierOrder.FOLLOW) {
            return false;
        }
        double startDistance = soldier.getFollowDistance();
        if (soldier.distanceToSqr(owner) <= startDistance * startDistance) {
            return false;
        }
        commander = owner;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (commander == null || soldier.getOrder() != SoldierOrder.FOLLOW) {
            return false;
        }
        double stopDistance = Math.max(2.0D, soldier.getFollowDistance() * 0.5D);
        return !soldier.getNavigation().isDone()
                && soldier.distanceToSqr(commander) > stopDistance * stopDistance;
    }

    @Override
    public void start() {
        pathRecalculationDelay = 0;
    }

    @Override
    public void stop() {
        commander = null;
        soldier.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (commander == null) {
            return;
        }
        soldier.getLookControl().setLookAt(commander, 10.0F, soldier.getMaxHeadXRot());
        if (--pathRecalculationDelay <= 0) {
            pathRecalculationDelay = adjustedTickDelay(10);
            soldier.getNavigation().moveTo(commander, speed);
        }
    }
}

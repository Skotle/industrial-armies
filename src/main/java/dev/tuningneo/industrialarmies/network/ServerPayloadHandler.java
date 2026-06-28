package dev.tuningneo.industrialarmies.network;

import dev.tuningneo.industrialarmies.army.CombatStance;
import dev.tuningneo.industrialarmies.army.SoldierOrder;
import dev.tuningneo.industrialarmies.entity.CompanySoldierEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ServerPayloadHandler {
    private static final double MAX_CONFIGURATION_DISTANCE_SQR = 12.0D * 12.0D;

    private ServerPayloadHandler() {
    }

    public static void handleUpdate(UpdateSoldierSettingsPayload payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }
        Entity entity = player.level().getEntity(payload.entityId());
        if (!(entity instanceof CompanySoldierEntity soldier)
                || !soldier.isOwnedBy(player)
                || player.distanceToSqr(soldier) > MAX_CONFIGURATION_DISTANCE_SQR) {
            return;
        }

        String sanitizedName = payload.name().strip();
        if (sanitizedName.length() > 32) {
            sanitizedName = sanitizedName.substring(0, 32);
        }
        soldier.setCustomName(sanitizedName.isBlank()
                ? Component.translatable("entity.industrial_armies.company_soldier")
                : Component.literal(sanitizedName));
        soldier.setCustomNameVisible(payload.showName());
        soldier.setCombatStance(CombatStance.byId(payload.combatStance()));
        soldier.setFollowDistance(payload.followDistance());

        SoldierOrder order = SoldierOrder.byId(payload.order());
        soldier.setOrder(order);
        if (order == SoldierOrder.FOLLOW) {
            soldier.setHoldPosition(null);
        } else if (soldier.getHoldPosition() == null) {
            soldier.setHoldPosition(soldier.blockPosition());
        }
        player.displayClientMessage(Component.translatable("message.industrial_armies.settings.saved"), true);
    }
}

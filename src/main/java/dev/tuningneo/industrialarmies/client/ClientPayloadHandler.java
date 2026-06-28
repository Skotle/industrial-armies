package dev.tuningneo.industrialarmies.client;

import dev.tuningneo.industrialarmies.entity.CompanySoldierEntity;
import dev.tuningneo.industrialarmies.network.OpenSoldierSettingsPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ClientPayloadHandler {
    private ClientPayloadHandler() {
    }

    public static void handleOpenSettings(OpenSoldierSettingsPayload payload, IPayloadContext context) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }
        Entity entity = minecraft.level.getEntity(payload.entityId());
        if (entity instanceof CompanySoldierEntity soldier) {
            minecraft.setScreen(new SoldierSettingsScreen(soldier));
        }
    }
}

package dev.tuningneo.industrialarmies.network;

import dev.tuningneo.industrialarmies.IndustrialArmies;
import dev.tuningneo.industrialarmies.client.ClientPayloadHandler;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = IndustrialArmies.MOD_ID)
public final class ModNetworking {
    private ModNetworking() {
    }

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(
                OpenSoldierSettingsPayload.TYPE,
                OpenSoldierSettingsPayload.STREAM_CODEC,
                ModNetworking::handleOpenSettingsOnClient);
        registrar.playToServer(
                UpdateSoldierSettingsPayload.TYPE,
                UpdateSoldierSettingsPayload.STREAM_CODEC,
                ServerPayloadHandler::handleUpdate);
    }

    private static void handleOpenSettingsOnClient(
            OpenSoldierSettingsPayload payload,
            IPayloadContext context
    ) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientPayloadHandler.handleOpenSettings(payload, context);
        }
    }
}

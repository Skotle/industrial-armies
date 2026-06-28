package dev.tuningneo.industrialarmies.client;

import dev.tuningneo.industrialarmies.IndustrialArmies;
import dev.tuningneo.industrialarmies.registry.ModEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = IndustrialArmies.MOD_ID, value = Dist.CLIENT)
public final class ClientModEvents {
    private ClientModEvents() {
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.COMPANY_SOLDIER.get(), CompanySoldierRenderer::new);
    }
}

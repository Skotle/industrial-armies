package dev.tuningneo.industrialarmies.event;

import dev.tuningneo.industrialarmies.IndustrialArmies;
import dev.tuningneo.industrialarmies.entity.CompanySoldierEntity;
import dev.tuningneo.industrialarmies.registry.ModEntities;
import dev.tuningneo.industrialarmies.registry.ModItems;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber(modid = IndustrialArmies.MOD_ID)
public final class ModEvents {
    private ModEvents() {
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.COMPANY_SOLDIER.get(), CompanySoldierEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void addCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.ENLISTMENT_CONTRACT);
            event.accept(ModItems.COMMAND_BATON);
        }
    }
}

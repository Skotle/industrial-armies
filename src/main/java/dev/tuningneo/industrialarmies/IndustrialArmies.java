package dev.tuningneo.industrialarmies;

import dev.tuningneo.industrialarmies.registry.ModEntities;
import dev.tuningneo.industrialarmies.registry.ModItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(IndustrialArmies.MOD_ID)
public final class IndustrialArmies {
    public static final String MOD_ID = "industrial_armies";

    public IndustrialArmies(IEventBus modBus) {
        ModEntities.ENTITY_TYPES.register(modBus);
        ModItems.ITEMS.register(modBus);
    }
}

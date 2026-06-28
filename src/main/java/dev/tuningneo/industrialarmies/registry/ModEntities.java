package dev.tuningneo.industrialarmies.registry;

import dev.tuningneo.industrialarmies.IndustrialArmies;
import dev.tuningneo.industrialarmies.entity.CompanySoldierEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, IndustrialArmies.MOD_ID);

    public static final Supplier<EntityType<CompanySoldierEntity>> COMPANY_SOLDIER =
            ENTITY_TYPES.register(
                    "company_soldier",
                    () -> EntityType.Builder.of(CompanySoldierEntity::new, MobCategory.CREATURE)
                            .sized(0.6F, 1.8F).eyeHeight(1.62F)
                            .clientTrackingRange(10).updateInterval(2)
                            .build(IndustrialArmies.MOD_ID + ":company_soldier"));

    private ModEntities() {
    }
}

package dev.tuningneo.industrialarmies.registry;

import dev.tuningneo.industrialarmies.IndustrialArmies;
import dev.tuningneo.industrialarmies.item.CommandBatonItem;
import dev.tuningneo.industrialarmies.item.EnlistmentContractItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(IndustrialArmies.MOD_ID);

    public static final DeferredItem<EnlistmentContractItem> ENLISTMENT_CONTRACT = ITEMS.registerItem(
            "enlistment_contract",
            EnlistmentContractItem::new,
            new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON));

    public static final DeferredItem<CommandBatonItem> COMMAND_BATON = ITEMS.registerItem(
            "command_baton",
            CommandBatonItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));

    private ModItems() {
    }
}

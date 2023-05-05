package net.walksanator.hextweaks.items;

import at.petrak.hexcasting.xplat.IXplatAbstractions;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.walksanator.hextweaks.HexTweaks;

public class ItemRegister {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(HexTweaks.MOD_ID, Registry.ITEM_REGISTRY);
    public static final RegistrySupplier<Item> CRYSTALLIZED_SCROLL = ITEMS.register("crystallized_scroll",
            () -> new CrystallizedScroll(props().rarity(Rarity.EPIC)));
    public static Item.Properties props() {
        return new Item.Properties().tab(IXplatAbstractions.INSTANCE.getTab());
    }

    public static void register() {
        ITEMS.register();
    }


}

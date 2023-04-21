package net.walksanator.hextweaks;

import at.petrak.hexcasting.api.PatternRegistry;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.ListIota;
import com.google.common.base.Suppliers;
import dev.architectury.registry.registries.Registries;
import net.walksanator.hextweaks.iotas.DictionaryIota;
import net.walksanator.hextweaks.iotas.HextweaksIotaType;
import net.walksanator.hextweaks.patterns.PatternRegister;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;


public class HexTweaks {
    public static final String MOD_ID = "hextweaks";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    // We can use this if we don't want to use DeferredRegister
    public static final Supplier<Registries> REGISTRIES = Suppliers.memoize(() -> Registries.get(MOD_ID));
    // Registering a new creative tab
    //public static final CreativeModeTab EXAMPLE_TAB = CreativeTabRegistry.create(new ResourceLocation(MOD_ID, "example_tab"), () ->
    //        new ItemStack(HexTweaks.EXAMPLE_ITEM.get()));
    
    //public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MOD_ID, Registry.ITEM_REGISTRY);
    //public static final RegistrySupplier<Item> EXAMPLE_ITEM = ITEMS.register("example_item", () ->
     //       new Item(new Item.Properties().tab(HexTweaks.EXAMPLE_TAB)));

    public static final List<Class<? extends Iota>> cannotBeDictKey = new ArrayList<>();

    static {
        cannotBeDictKey.add(DictionaryIota.class);
        cannotBeDictKey.add(ListIota.class);
    }

    public static int MaxKeysInDictIota = 32;

    public static void init() {
        //ITEMS.register();
        try {
            PatternRegister.registerPatterns();
        } catch (PatternRegistry.RegisterPatternException e) {
            LOGGER.error("Failed to load patterns for hextweaks");
        }

        HextweaksIotaType.registerTypes();

    }
}

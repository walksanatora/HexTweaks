package net.walksanator.hextweaks;

import at.petrak.hexcasting.api.PatternRegistry;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.ListIota;
import dev.architectury.platform.Platform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.walksanator.hextweaks.blocks.BlockRegister;
import net.walksanator.hextweaks.iotas.DictionaryIota;
import net.walksanator.hextweaks.iotas.HextweaksIotaType;
import net.walksanator.hextweaks.items.ItemRegister;
import net.walksanator.hextweaks.mass_findflay.MassSacrificeHandler;
import net.walksanator.hextweaks.mass_findflay.MassSlipwayCreateSacrifice;
import net.walksanator.hextweaks.mass_findflay.MassSlipwayDestroySacrifice;
import net.walksanator.hextweaks.patterns.PatternRegister;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ram.talia.hexal.common.entities.BaseWisp;
import ram.talia.hexal.common.entities.WanderingWisp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HexTweaks {
    public static final String MOD_ID = "hextweaks";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    //these act as registries/configs...
    public static final List<Class<? extends Iota>> cannotBeDictKey = new ArrayList<>();
    public static final List<Class<? extends Iota>> cannotBeDictValue = new ArrayList<>();
    public static final List<ResourceLocation> GrandSpells = new ArrayList<>();
    public static final Map<Integer, MassSacrificeHandler> massSacrificeHandlers = new HashMap<>();
    public static final List<Class<? extends Entity>> sacrificialEntityTypes = new ArrayList<>();
    @SuppressWarnings("CanBeFinal") //so we can allow other mods to change this
    public static int MaxKeysInDictIota = 32;

    //initial setup of some values that we *know* at compille time will be there
    static {
        //cannot be keys
        cannotBeDictKey.add(DictionaryIota.class);
        cannotBeDictKey.add(ListIota.class);

        //because the api is not here, yet we have to ban them... *sorry*
        cannotBeDictValue.add(DictionaryIota.class);
        cannotBeDictValue.add(ListIota.class);

        //Grand spells
        GrandSpells.add(new ResourceLocation("hextweaks","grand/reroll"));
        GrandSpells.add(new ResourceLocation("hextweaks","grand/massbrainsweep"));

        //sacrificial Entities types
        sacrificialEntityTypes.add(Villager.class);
    }


    public static void init() {
        try {
            PatternRegister.registerPatterns();
        } catch (PatternRegistry.RegisterPatternException e) {
            LOGGER.error("Failed to load patterns for hextweaks");
        }

        //other mods registry stuff
        HextweaksIotaType.registerTypes();

        BlockRegister.register();
        ItemRegister.register();

        //my mods registry stuff
        if (Platform.isModLoaded("hexal")) {
            massSacrificeHandlers.put(80, new MassSlipwayCreateSacrifice()); // 5 master villagers
            massSacrificeHandlers.put(16, new MassSlipwayDestroySacrifice()); // 1 master villager

            sacrificialEntityTypes.add(BaseWisp.class);
        }
    }
}

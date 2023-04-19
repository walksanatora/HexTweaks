package net.walksanator.hextweaks.fabric;

import net.walksanator.hextweaks.HexTweaks;
import net.fabricmc.api.ModInitializer;

public class HextweaksFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        HexTweaks.init(); HexTweaks.LOGGER.info("HexTweaks Loaded on fabric");
    }
}

package net.walksantor.hextweaks.fabric;

import dev.architectury.platform.Platform;
import net.walksantor.hextweaks.HexTweaks;
import net.fabricmc.api.ModInitializer;
import net.walksantor.hextweaks.HexTweaksRegistry;

public class HexTweaksFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        HexTweaks.init();
        HexTweaks.LOGGER.info("performing registration on FABRIC");
        HexTweaksRegistry.INSTANCE.register();
    }
}

package net.walksantor.hextweaks.fabric;

import dev.architectury.platform.Platform;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.walksantor.hextweaks.HexTweaks;
import net.fabricmc.api.ModInitializer;
import net.walksantor.hextweaks.HexTweaksRegistry;
import net.walksantor.hextweaks.casting.MindflayRegistry;

public class HexTweaksFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        HexTweaks.init();
        HexTweaks.LOGGER.info("performing registration on FABRIC");
        HexTweaksRegistry.INSTANCE.register();
        if (FabricLoader.getInstance().isModLoaded("spectrum")) {
            MindflayRegistry.INSTANCE.put(
                    new ResourceLocation(
                            HexTweaks.MOD_ID,
                            "fermentation_timeskip"
                    ),
                    AccelerateFlay.INSTANCE::skip12hours
            );
        }
    }
}

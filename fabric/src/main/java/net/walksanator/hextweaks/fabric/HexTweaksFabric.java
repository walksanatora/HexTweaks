package net.walksanator.hextweaks.fabric;

import dev.architectury.platform.Platform;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.walksanator.hextweaks.HexTweaks;
import net.fabricmc.api.ModInitializer;
import net.walksantor.hextweaks.HexTweaksRegistry;
import net.walksantor.hextweaks.casting.MindflayRegistry;

import java.util.Map;
import java.util.Optional;

public class HexTweaksFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        HexTweaks.init();
        HexTweaks.LOGGER.info("performing registration on FABRIC");
        HexTweaksRegistry.INSTANCE.init();
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

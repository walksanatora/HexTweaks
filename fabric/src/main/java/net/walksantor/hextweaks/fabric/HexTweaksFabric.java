package net.walksantor.hextweaks.fabric;

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
import net.walksantor.hextweaks.HexTweaks;
import net.fabricmc.api.ModInitializer;
import net.walksantor.hextweaks.HexTweaksRegistry;
import net.walksantor.hextweaks.casting.MindflayRegistry;

import java.util.Map;
import java.util.Optional;

public class HexTweaksFabric implements ModInitializer {
    @Override
    public void onInitialize() {
//        ResourceLocation re = Registries.DIMENSION.location();
//        HexTweaks.LOGGER.info("RegistryID {}",re);
//        RegistryEntryAddedCallback.event(BuiltInRegistries.REGISTRY).register( (id,res,obj) -> {
//            HexTweaks.LOGGER.info("A new Registry is born {}",res);
//        });
        DynamicRegistrySetupCallback.EVENT.register((registryView) -> {
//            Optional<Registry<Level>> levels = registryView.getOptional(Registries.DIMENSION);
//            if (levels.isPresent()) {
//                HexTweaks.LOGGER.info("Levels Exist!!!");
//                Registry<Level> worlds = levels.get();
//                for (Map.Entry<ResourceKey<Level>, Level> level : worlds.entrySet()) {
//                    HexTweaks.LOGGER.info("Level id: {}",level.getKey().location());
//                }
//            }
            HexTweaks.LOGGER.info("Performing DynamicRegistrySetup");
            Optional<Registry<Level>> Reg = registryView.getOptional(Registries.DIMENSION);
            HexTweaks.LOGGER.info("{}",Reg);
            Reg.ifPresent(levels -> HexTweaks.LOGGER.info("{}", levels.stream().count()));
            registryView.registerEntryAdded(Registries.DIMENSION, (id,res,obj) -> {
                HexTweaks.LOGGER.info("DimensionRegistry called");
                HexTweaks.breakpoint();
                HexTweaks.LOGGER.info("Dimension registration {}",res);
            });
            HexTweaks.LOGGER.info("Registry Hooked");
        });
//        Registry<?> reg = BuiltInRegistries.REGISTRY.get(re);
//        RegistryEntryAddedCallback.event(reg).register( (id,location,object) -> {
//            HexTweaks.LOGGER.info("Registry detected id: {} res: {}, obj: {}",id,location,object);
//        });
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

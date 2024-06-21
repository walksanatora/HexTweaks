package net.walksantor.hextweaks.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegisterEvent;
import net.walksantor.hextweaks.HexTweaks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.walksantor.hextweaks.HexTweaksRegistry;

@Mod(HexTweaks.MOD_ID)
@Mod.EventBusSubscriber(modid = HexTweaks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class HexTweaksForge {
    public HexTweaksForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(HexTweaks.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        HexTweaks.init();
        HexTweaksRegistry.INSTANCE.init();
    }

    @SubscribeEvent
    public static void register(RegisterEvent event) {
        ResourceKey<Registry<?>> key = (ResourceKey<Registry<?>>) event.getRegistryKey();
        HexTweaksRegistry.INSTANCE.register(key);
//        HexTweaks.LOGGER.info("performing registration on FORGE");
//        HexTweaksRegistry.INSTANCE.register();
    }

    @SubscribeEvent
    public  static void client(FMLClientSetupEvent event) {
        HexTweaks.LOGGER.info("performing client setup on FORGE");
        HexTweaksRegistry.INSTANCE.model();
    }
}

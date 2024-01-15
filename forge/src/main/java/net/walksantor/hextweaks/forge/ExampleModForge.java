package net.walksantor.hextweaks.forge;

import dev.architectury.platform.forge.EventBuses;
import net.walksantor.hextweaks.HexTweaks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(HexTweaks.MOD_ID)
public class ExampleModForge {
    public ExampleModForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(HexTweaks.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        HexTweaks.init();
    }
}

package net.walksanator.hextweaks.forge;

import dev.architectury.platform.forge.EventBuses;
import net.walksanator.hextweaks.HexTweaks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(HexTweaks.MOD_ID)
public class HexTweaksForge{
    public HexTweaksForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(HexTweaks.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        HexTweaks.init();
    }
}

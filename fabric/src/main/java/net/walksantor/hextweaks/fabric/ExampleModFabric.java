package net.walksantor.hextweaks.fabric;

import net.walksantor.hextweaks.HexTweaks;
import net.fabricmc.api.ModInitializer;

public class ExampleModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        HexTweaks.init();
    }
}

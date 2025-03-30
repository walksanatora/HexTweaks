package net.walksanator.hextweaks.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.walksantor.hextweaks.HexTweaksRegistry;
import vazkii.patchouli.fabric.client.FabricClientInitializer;

public class HexTweaksFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HexTweaksRegistry.INSTANCE.model();
    }
}

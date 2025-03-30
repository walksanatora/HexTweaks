package net.walksanator.hextweaks.fabric.mixin;

import at.petrak.hexcasting.fabric.FabricHexInitializer;
import net.walksantor.hextweaks.HexTweaksRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FabricHexInitializer.class)
public class MixinLateHexInit {
    @Inject(method = "initRegistries", at = @At("RETURN"), remap = false)
    private void lateRegistration(CallbackInfo ci) {
        HexTweaksRegistry.INSTANCE.register(null);
    }
}

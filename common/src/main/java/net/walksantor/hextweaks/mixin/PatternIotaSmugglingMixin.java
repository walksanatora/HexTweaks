package net.walksantor.hextweaks.mixin;

import at.petrak.hexcasting.api.casting.eval.CastResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.PatternIota;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import net.minecraft.server.level.ServerLevel;
import net.walksantor.hextweaks.duck.HexPatternSmugglingAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PatternIota.class)
public abstract class PatternIotaSmugglingMixin {

    @Shadow
    abstract public HexPattern getPattern();

    @Inject(method="execute",at =
        @At(value="INVOKE", target="Lat/petrak/hexcasting/common/casting/PatternRegistryManifest;matchPattern(Lat/petrak/hexcasting/api/casting/math/HexPattern;Lnet/minecraft/server/level/ServerLevel;Z)Lat/petrak/hexcasting/api/casting/PatternShapeMatch;")
    )
    private void hexdim$smuggleContext(CastingVM vm, ServerLevel world, SpellContinuation continuation, CallbackInfoReturnable<CastResult> cir) {
        ((HexPatternSmugglingAccess)(Object)getPattern()).hexdim$setSmuggledEnv(vm.getEnv());
    }
}

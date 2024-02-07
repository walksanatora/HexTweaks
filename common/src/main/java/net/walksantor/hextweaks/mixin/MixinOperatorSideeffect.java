package net.walksantor.hextweaks.mixin;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import kotlin.collections.CollectionsKt;
import net.minecraft.nbt.CompoundTag;
import net.walksantor.hextweaks.casting.environment.MishapAwareCastingEnvironment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Mixin(OperatorSideEffect.DoMishap.class)
public class MixinOperatorSideeffect {
    @Final
    @Shadow
    private Mishap mishap;

    @Final
    @Shadow
    private Mishap.Context errorCtx;

    @Inject(method = "performEffect(Lat/petrak/hexcasting/api/casting/eval/vm/CastingVM;)Z", at = @At("HEAD"), cancellable = true,remap = false)
    private void hextweaks$mishapAwareCastingEnv(CastingVM harness, CallbackInfoReturnable<Boolean> cir) {
        CastingEnvironment env = harness.getEnv();
        if (env instanceof MishapAwareCastingEnvironment mishapAwareEnv) {
            Optional<List<Iota>> res = mishapAwareEnv.onMishap(mishap,errorCtx,harness.getImage().getStack());
            if (res.isPresent()) {
                cir.setReturnValue(true);
                cir.cancel();

                CastingImage img = harness.getImage().copy(res.get(),
                        0,
                        null, false, 0, null
                );
                harness.setImage(img);
            }
         }
    }

}
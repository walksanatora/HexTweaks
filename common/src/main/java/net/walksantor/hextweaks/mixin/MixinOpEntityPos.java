package net.walksantor.hextweaks.mixin;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.common.casting.actions.queryentity.OpEntityPos;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(OpEntityPos.class)
public class MixinOpEntityPos {

    @WrapOperation(method = "execute", at= @At(value = "INVOKE", target = "Lat/petrak/hexcasting/api/casting/eval/CastingEnvironment;assertEntityInRange(Lnet/minecraft/world/entity/Entity;)V"))
    private void wrapped(CastingEnvironment instance, Entity e, Operation<Void> original) {
        //we replace the ambit check to only check for players
        if (e instanceof Player) {
            original.call(instance,e);
        }
    }
}

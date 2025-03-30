package net.walksanator.hextweaks.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.dafuqs.spectrum.blocks.titration_barrel.TitrationBarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "ram.talia.hexal.common.casting.actions.spells.great.OpTick.Spell")
public class MixinAccelerateFermentation {
    @WrapOperation(
            method = "cast(Lat/petrak/hexcasting/api/casting/eval/CastingEnvironment;Lat/petrak/hexcasting/api/casting/eval/vm/CastingImage;)Lat/petrak/hexcasting/api/casting/eval/vm/CastingImage;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;getBlockState()Lnet/minecraft/world/level/block/state/BlockState;")
    )
    public BlockState hextweaks$accelerateBarrel(BlockEntity barrel, Operation<BlockState> origin) {
            if (barrel instanceof TitrationBarrelBlockEntity titration) {
                SealTimeAccessor accessor = ((SealTimeAccessor)titration);
                accessor.setSealTime(
                        accessor.getSealTime() - 1000L
                );
            }
            return origin.call(barrel);
    }
}

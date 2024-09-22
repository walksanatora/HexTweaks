package net.walksantor.hextweaks.mixin;

import at.petrak.hexcasting.common.blocks.circles.impetuses.BlockEntityRedstoneImpetus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockEntityRedstoneImpetus.class)
public abstract class MixinClericReportLocation extends BlockEntity {

    public MixinClericReportLocation(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Redirect(
            method = "getStoredPlayer()Lnet/minecraft/server/level/ServerPlayer;",
            at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;)V"),
            remap=false
    )
    private void hextweaks$whichBlockYells(Logger instance, String s, Object o) {
        instance.error("Entity {} stored in a cleric impetus wasn't a player somehow at {}",o,worldPosition);
    }
}

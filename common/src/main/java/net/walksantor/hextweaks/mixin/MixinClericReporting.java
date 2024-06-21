package net.walksantor.hextweaks.mixin;

import at.petrak.hexcasting.api.casting.circles.BlockEntityAbstractImpetus;
import at.petrak.hexcasting.common.blocks.circles.impetuses.BlockEntityRedstoneImpetus;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityAbstractImpetus.class)
public abstract class MixinClericReporting {

    @Inject(method = "postPrint", at = @At("HEAD"))
    public void sendToPlayer(Component printDisplay, CallbackInfo ci) {
        if (((Object)this) instanceof BlockEntityRedstoneImpetus cleric) {
            cleric.getStoredPlayer().sendSystemMessage(printDisplay);
        }
    }
}

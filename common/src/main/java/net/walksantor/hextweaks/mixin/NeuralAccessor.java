package net.walksantor.hextweaks.mixin;

import io.sc3.plethora.gameplay.neural.NeuralComputer;
import io.sc3.plethora.gameplay.neural.NeuralPocketAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NeuralPocketAccess.class)
public interface NeuralAccessor {
    @Accessor
    NeuralComputer getNeural();
}

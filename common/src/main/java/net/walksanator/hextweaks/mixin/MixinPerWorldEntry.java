package net.walksanator.hextweaks.mixin;

import at.petrak.hexcasting.api.spell.math.HexPattern;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "at.petrak.hexcasting.api.PatternRegistry$PerWorldEntry")
public interface MixinPerWorldEntry {
    @Accessor
    HexPattern getPrototype();

    @Accessor
    ResourceLocation getOpId();
}

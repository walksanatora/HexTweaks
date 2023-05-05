package net.walksanator.hextweaks.mixin;

import at.petrak.hexcasting.api.spell.math.HexDir;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "at.petrak.hexcasting.api.PatternRegistry$RegularEntry")
public interface MixinRegularEntry {
    @Accessor
    HexDir getPreferredStart();

    @Accessor
    ResourceLocation getOpId();
}

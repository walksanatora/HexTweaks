package net.walksanator.hextweaks.mixin;

import at.petrak.hexcasting.api.PatternRegistry;
import at.petrak.hexcasting.api.spell.Action;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.ConcurrentMap;

@Mixin(PatternRegistry.class)
public interface MixinPatternRegistry {
    @Accessor("actionLookup")
    static ConcurrentMap<ResourceLocation, Action> getActionLookup() {
        throw new AssertionError();
    }

    @Accessor("perWorldPatternLookup")
    static ConcurrentMap<ResourceLocation,?> getPerWorldPatternLookup() {
        throw new AssertionError();
    }

    @Accessor("regularPatternLookup")
    static ConcurrentMap<String,?> getRegularPatternLookup() {
        throw new AssertionError();
    }
}

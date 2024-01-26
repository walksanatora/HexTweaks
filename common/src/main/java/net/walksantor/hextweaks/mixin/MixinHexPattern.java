package net.walksantor.hextweaks.mixin;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import net.walksantor.hextweaks.duck.HexPatternSmugglingAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(HexPattern.class)
public abstract class MixinHexPattern implements HexPatternSmugglingAccess {
    @Unique
    private CastingEnvironment env = null;

    @Override
    public CastingEnvironment hexdim$getSmuggledEnv() {
        return env;
    }

    @Override
    public void hexdim$setSmuggledEnv(CastingEnvironment env) {
        this.env = env;
    }
}
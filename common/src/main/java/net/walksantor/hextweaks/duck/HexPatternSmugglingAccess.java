package net.walksantor.hextweaks.duck;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;

public interface HexPatternSmugglingAccess {
    CastingEnvironment hexdim$getSmuggledEnv();
    void hexdim$setSmuggledEnv(CastingEnvironment env);
}

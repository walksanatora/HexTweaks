package net.walksanator.hextweaks.patterns;

import net.walksanator.hextweaks.HexTweaks;
import net.walksanator.hextweaks.patterns.OpNewFlip;
import at.petrak.hexcasting.api.PatternRegistry;
import at.petrak.hexcasting.api.spell.math.HexDir;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import net.minecraft.resources.ResourceLocation;

public class PatternRegister {
    public static void registerPatterns() throws PatternRegistry.RegisterPatternException {
        //HexPattern(SOUTH_WEST qqaw)
        PatternRegistry.mapPattern(
                HexPattern.fromAngles("qqaw", HexDir.SOUTH_EAST),
                new ResourceLocation("hextweaks","page/right"),
                new OpNewFlip(true),false
        );
        //HexPattern(SOUTH_EAST wdee)
        PatternRegistry.mapPattern(
                HexPattern.fromAngles("wdee",HexDir.SOUTH_EAST),
                new ResourceLocation("hextweaks","page/left"),
                new OpNewFlip(false), false
        );
        HexTweaks.LOGGER.info("finished loading hextweaks hexes");
    }
}

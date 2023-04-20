package net.walksanator.hextweaks.patterns;

import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.common.casting.operators.spells.OpPotionEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.walksanator.hextweaks.HexTweaks;
import net.walksanator.hextweaks.patterns.OpNewFlip;
import at.petrak.hexcasting.api.PatternRegistry;
import at.petrak.hexcasting.api.spell.math.HexDir;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.codec.binary.Hex;

public class PatternRegister {
    public static void registerPatterns() throws PatternRegistry.RegisterPatternException {
        //HexPattern(SOUTH_WEST qqaw)
        PatternRegistry.mapPattern(
                HexPattern.fromAngles("qqaw", HexDir.SOUTH_WEST),
                new ResourceLocation("hextweaks","page/right"),
                new OpNewFlip(true),false
        );
        //HexPattern(SOUTH_EAST eedw)
        PatternRegistry.mapPattern(
                HexPattern.fromAngles("eedw",HexDir.SOUTH_EAST),
                new ResourceLocation("hextweaks","page/left"),
                new OpNewFlip(false), false
        );
        //HexPattern(EAST deqwedaqedqeweqewwdweqa)
        PatternRegistry.mapPattern(
                HexPattern.fromAngles("deqwedaqedqeweqewwdweqa",HexDir.EAST),
                new ResourceLocation("hextweaks","suicide"),
                new OpSuicide(), true
        );
        PatternRegistry.mapPattern(
                HexPattern.fromAngles("weed",HexDir.EAST),
                new ResourceLocation("hextweaks","nausea"),
                new OpPotionEffect(MobEffects.CONFUSION, MediaConstants.DUST_UNIT / 4,true,false,false),false
        );
        HexTweaks.LOGGER.info("finished loading hextweaks hexes");
    }
}

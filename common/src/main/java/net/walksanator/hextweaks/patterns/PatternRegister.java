package net.walksanator.hextweaks.patterns;

import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.common.casting.operators.spells.OpPotionEffect;
import net.minecraft.world.effect.MobEffects;
import net.walksanator.hextweaks.HexTweaks;
import at.petrak.hexcasting.api.PatternRegistry;
import at.petrak.hexcasting.api.spell.math.HexDir;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import net.minecraft.resources.ResourceLocation;
import net.walksanator.hextweaks.patterns.dict.*;

public class PatternRegister {
    public static void registerPatterns() throws PatternRegistry.RegisterPatternException {
        //HexPattern(SOUTH_WEST qqaw)
        PatternRegistry.mapPattern(
                HexPattern.fromAngles("qqaw", HexDir.SOUTH_WEST),
                new ResourceLocation("hextweaks","page/right"),
                new OpPageFlip(true),false
        );
        //HexPattern(SOUTH_EAST eedw)
        PatternRegistry.mapPattern(
                HexPattern.fromAngles("eedw",HexDir.SOUTH_EAST),
                new ResourceLocation("hextweaks","page/left"),
                new OpPageFlip(false), false
        );
        //HexPattern(WEST deqwedaqedqeweqewwdweqa)
        PatternRegistry.mapPattern(
                HexPattern.fromAngles("dewdeqwwedaqedwadweqewwd",HexDir.WEST),
                new ResourceLocation("hextweaks","suicide"),
                new OpSuicide(), true
        );
        PatternRegistry.mapPattern(
                HexPattern.fromAngles("weed",HexDir.EAST),
                new ResourceLocation("hextweaks","nausea"),
                new OpPotionEffect(MobEffects.CONFUSION, MediaConstants.DUST_UNIT / 4,true,false,false),false
        );
        //HexPattern(North_East qaqwaq)
        PatternRegistry.mapPattern(
                HexPattern.fromAngles("qaqwaq",HexDir.NORTH_EAST),
                new ResourceLocation("hextweaks","dict/new"),
                new OpNewDict(), false
        );
        //HexPattern(NORTH_EAST qaqwaqeqdweeew)
        PatternRegistry.mapPattern(
                HexPattern.fromAngles("qaqwaqeqdweeew",HexDir.NORTH_EAST),
                new ResourceLocation("hextweaks","dict/from"),
                new OpDictFrom(), false
        );
        //HexPattern(NORTH_EAST qaqwaqdwqqqwae)
        PatternRegistry.mapPattern(
                HexPattern.fromAngles("qaqwaqdwqqqwae",HexDir.NORTH_EAST),
                new ResourceLocation("hextweaks","dict/break"),
                new OpDictBreak(), false
        );
        //HexPattern(NORTH_EAST qaqwaqdwqqqwq)
        PatternRegistry.mapPattern(
                HexPattern.fromAngles("qaqwaqdwqqqwq",HexDir.NORTH_EAST),
                new ResourceLocation("hextweaks","dict/break/single"),
                new OpDictBreakSingle(), false
        );
        //HexPattern(North_East qaqwaqedadad) Thoth but dictionary
        PatternRegistry.mapPattern(
                HexPattern.fromAngles("qaqwaqedadad",HexDir.NORTH_EAST),
                new ResourceLocation("hextweaks","dict/thoths"),
                new OpDictThoths(), false
        );
        //HexPattern(NORTH_EAST qaqwaqeewaqaeaq)
        PatternRegistry.mapPattern(
                HexPattern.fromAngles("qaqwaqeewaqaeaq",HexDir.NORTH_EAST),
                new ResourceLocation("hextweaks","dict/size"),
                new OpDictSize(), false
        );
        //HexPattern(North_East qaqwaqewaa)
        PatternRegistry.mapPattern(
                HexPattern.fromAngles("qaqwaqdqedqde",HexDir.NORTH_EAST),
                new ResourceLocation("hextweaks","dict/set"),
                new OpDictSet(), false
        );
        //HexPattern(North_East  qaqwaqdadeeedqaqwaqdadeeed)
        PatternRegistry.mapPattern(
                HexPattern.fromAngles("qaqwaqdadeeed",HexDir.NORTH_EAST),
                new ResourceLocation("hextweaks","dict/get"),
                new OpDictPop(), false
        );
        //HexPattern(East qaqwaqeqdd)
        PatternRegistry.mapPattern(
                HexPattern.fromAngles("qaqwaqeqdd",HexDir.NORTH_EAST),
                new ResourceLocation("hextweaks","dict/remove"),
                new OpDictRemove(), false
        );
        //HexPattern(East ddwwee)
        PatternRegistry.mapPattern(
                HexPattern.fromAngles("wdwaw",HexDir.NORTH_EAST),
                new ResourceLocation("hextweaks","itter"),
                new OpItterator(), false
        );

        //Grand Spells
        // used by "mind"flaying a ancient scroll into a budding amethyst
        // Idea for Grand spells:
        //  Reroll: (entity/mote ->)
        //   Possible Patterns:
        //    NORTH_EAST qaqqqqwwawwqqeaddwwdda
        //    NORTH_EAST qaqwawqwwawwqwaaqawwaq
        //   takes a Ancient/Grand scroll and re-rolls the pattern on it (getting more expensive each time it is used on the same scroll)
        //   Cost: 100(dust) * 1.25^x (where x is the number of times re-rolled)
        //  MindFlay+: ([entity],Vec->) +circle
        //   takes a bunch of entities (which will be sacrificed no matter the outcome)
        //   Cost: 1 shard per entity used in the ritual
        //   "Experience levels" are determined as follows (nitwit is always novice)
        //   `novice, apprentice, journeyman, expert, master`
        //    1       2           4           8       16
        //   Recipes: levels = result
        //    16 = Collapse/Remove Slipway
        //    80 = Spawn Slipway
        // Mark: (Vec->) +mixin (to extend ambit in CastingContext)
        //  it is basically a Greater sentinel, but,  you can summon multiple
        //  (cost increases the more you summon)
        //  you can destroy them to bring the cost back-down
        // UnMark: (Vec->)
        //  removes a "mark" placed by mark
        // Mark's Refl. (->[Vec])
        //  returns a list of the locations of all your marks.

        //altrenate thoths idea: it runs the program but does not reset the stack or en-listify it
        HexTweaks.LOGGER.info("finished loading hextweaks hexes");
    }
}

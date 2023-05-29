package net.walksanator.hextweaks.patterns;

import at.petrak.hexcasting.api.PatternRegistry;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.spell.Action;
import at.petrak.hexcasting.api.spell.math.HexAngle;
import at.petrak.hexcasting.api.spell.math.HexDir;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.common.casting.operators.spells.OpPotionEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.walksanator.hextweaks.HexTweaks;
import net.walksanator.hextweaks.mixin.MixinPatternRegistry;
import net.walksanator.hextweaks.mixin.MixinPerWorldEntry;
import net.walksanator.hextweaks.mixin.MixinRegularEntry;
import net.walksanator.hextweaks.patterns.craft.OpCraftGrandScroll;
import net.walksanator.hextweaks.patterns.dict.*;
import net.walksanator.hextweaks.patterns.grand.OpMassMindflay;
import net.walksanator.hextweaks.patterns.grand.OpReroll;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

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

        //Great Spells
        //HexPattern(WEST wqqewawqwqedawdweewaeqwawdaqwdewawdadwawqwawdadwaw)
        PatternRegistry.mapPattern(
                HexPattern.fromAngles("wqqewawqwqedawdweewaeqwawdaqwdewawdadwawqwawdadwaw",HexDir.WEST),
                new ResourceLocation("hextweaks","craft/grandscroll"),
                new OpCraftGrandScroll(), true
        );
        //HexPattern(WEST deqwedaqedqeweqewwdweqa)
        PatternRegistry.mapPattern(
                HexPattern.fromAngles("dewdeqwwedaqedwadweqewwd",HexDir.WEST),
                new ResourceLocation("hextweaks","suicide"),
                new OpSuicide(), true
        );

        //Grand Spells
        //HexPattern(NORTH_EAST qaqqqqwwawwqqeaddwwddas)
        PatternRegistry.mapPattern(
                fromAnglesIllegal("qaqqqqwwawwqqeaddwwddas", HexDir.NORTH_EAST),
                new ResourceLocation("hextweaks","grand/reroll"),
                new OpReroll(), false
        );
        //HexPattern(NORTH_EAST qeqwqwqwqwqeqaeqeaqeqaeqaqdededwaqdedsde)
        PatternRegistry.mapPattern(
                fromAnglesIllegal("qeqwqwqwqwqeqaeqeaqeqaeqaqdededwaqdedsde", HexDir.NORTH_EAST),
                new ResourceLocation("hextweaks","grand/massbrainsweep"),
                new OpMassMindflay(), false
        );
        

        //altrenate thoths idea: it runs the program but does not reset the stack or en-listify it
        HexTweaks.LOGGER.info("finished loading hextweaks hexes");
    }

    public static PatternRegistry.PatternEntry lookupPatternIllegal(ResourceLocation opId) {
        ConcurrentMap<ResourceLocation,?>  perWorldPatternLookup = MixinPatternRegistry.getPerWorldPatternLookup();
        ConcurrentMap<String,?> regularPatternLookup = MixinPatternRegistry.getRegularPatternLookup();
        ConcurrentMap<ResourceLocation, Action> actionLookup = MixinPatternRegistry.getActionLookup();

        if (perWorldPatternLookup.containsKey(opId)) {
            MixinPerWorldEntry it = (MixinPerWorldEntry) perWorldPatternLookup.get(opId);
            return new PatternRegistry.PatternEntry(it.getPrototype(), actionLookup.get(it.getOpId()), true);
        }
        for (var kv : regularPatternLookup.entrySet()) {
            String sig = kv.getKey();
            MixinRegularEntry entry = (MixinRegularEntry) kv.getValue();
            if (entry.getOpId().equals(opId)) {
                HexPattern pattern = fromAnglesIllegal(sig, entry.getPreferredStart());
                return new PatternRegistry.PatternEntry(pattern, actionLookup.get(entry.getOpId()), false);
            }
        }

        throw new IllegalArgumentException("could not find a pattern for " + opId);
    }

    public static HexPattern fromAnglesIllegal(String pattern, HexDir dir) {
        List<HexAngle> angles = new ArrayList<>();
        for (int i = 0; i < pattern.length(); i++) {
            char letter = pattern.charAt(i);
            HexAngle angle = switch (letter) {
                case 'w' -> HexAngle.FORWARD;
                case 'e' -> HexAngle.RIGHT;
                case 'd' -> HexAngle.RIGHT_BACK;
                case 's' -> HexAngle.BACK;
                case 'a' -> HexAngle.LEFT_BACK;
                case 'q' -> HexAngle.LEFT;
                default ->
                        throw new IllegalArgumentException("Cannot match %s at idx $idx to a direction".formatted(letter));
            };
            angles.add(angle);
        }
        return new HexPattern(dir,angles);
    }
}

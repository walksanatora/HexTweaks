package net.walksantor.hextweaks.casting

import OpSuicide
import at.petrak.hexcasting.api.casting.ActionRegistryEntry
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.math.HexAngle
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.common.lib.hex.HexActions
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.walksantor.hextweaks.HexTweaks
import java.util.function.BiConsumer

object PatternRegistry {
    private val GRAND_REGISTRY: HashMap<List<HexAngle>,Pair<Action,ResourceLocation>> = HashMap()
    private val defferred: HashMap<ResourceLocation,ActionRegistryEntry> = HashMap()
    fun registerGrandSpells(pattern: List<HexAngle>, action: Action, namespace: ResourceLocation) {
        if (GRAND_REGISTRY.containsKey(pattern)) {
            throw IllegalArgumentException("a pattern is allready registered under that pattern id: %s".format(
                GRAND_REGISTRY[pattern]?.second)
            )
        }
        GRAND_REGISTRY[pattern] = Pair(action,namespace)
    }

    val THE_FUNNY = pattern(HexDir.WEST,"dewdeqwwedaqedwadweqewwd","suicide",OpSuicide(),true)

    fun pattern(start: HexDir,angles: String,name: String, action: Action, isGrand: Boolean): ActionRegistryEntry {
        val pat = patternAllowIllegal(start,angles)
        val resourceLocation = ResourceLocation(HexTweaks.MOD_ID,name)

        val ARE = ActionRegistryEntry(pat,action)
        if (isGrand) {
            registerGrandSpells(pat.angles,action,resourceLocation)
        } else {
            if (defferred.containsKey(resourceLocation)) throw IllegalArgumentException("two patterns are vying for $resourceLocation id. fix this")
            defferred[resourceLocation] = ARE
        }
        return ARE
    }

    fun getGrandEntry(pattern: HexPattern): Pair<Action,ResourceLocation>? = GRAND_REGISTRY[pattern.angles]


    fun patternAllowIllegal(start: HexDir, angles: String): HexPattern {
        val pat = HexPattern(start, mutableListOf())
        for ((idx, c) in angles.withIndex()) {
            val angle = when (c) {
                'w' -> HexAngle.FORWARD
                'e' -> HexAngle.RIGHT
                'd' -> HexAngle.RIGHT_BACK
                // for completeness ... >:)
                's' -> HexAngle.BACK
                'a' -> HexAngle.LEFT_BACK
                'q' -> HexAngle.LEFT
                else -> throw IllegalArgumentException("Cannot match $c at idx $idx to a direction")
            }
            pat.angles.add(angle)
        }
        return pat
    }

    fun getGrandSpell(player: ServerPlayer, level: ServerLevel,pat: HexPattern): HexPattern {
        val uuid = player.uuid
        val seed = level.seed
        val upper = uuid.mostSignificantBits.xor(seed)
        val lower = uuid.leastSignificantBits.xor(seed)

        val expected_bits: MutableList<Boolean> = mutableListOf()
        for (i in 63 downTo 0) {
            val bit = (upper shr i) and 1
            expected_bits.add(bit.toInt() == 1)
        }
        for (i in 63 downTo 0) {
            val bit = (lower shr i) and 1
            expected_bits.add(bit.toInt() == 1)
        }

        val resulting: MutableList<HexAngle> = mutableListOf()
        for (sig in pat.angles) {
            if (expected_bits.removeAt(0)) {
                resulting.add(HexAngle.BACK)
                resulting.add(HexAngle.BACK)
            }
            resulting.add(sig)
        }
        return HexPattern(pat.startDir,resulting)
    }

    fun register(r: BiConsumer<ActionRegistryEntry, ResourceLocation>) {
        for ((key, value) in defferred) {
            r.accept(value, key)
        }
    }
}
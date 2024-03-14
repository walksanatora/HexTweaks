package net.walksantor.hextweaks.casting

import OpPageFlip
import OpSuicide
import at.petrak.hexcasting.api.casting.ActionRegistryEntry
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.math.HexAngle
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.common.items.ItemLoreFragment
import at.petrak.hexcasting.common.lib.hex.HexActions
import at.petrak.hexcasting.server.ScrungledPatternsSave
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.walksantor.hextweaks.HexTweaks
import net.walksantor.hextweaks.casting.actions.*
import ram.talia.hexal.common.lib.hex.HexalActions
import java.util.*
import java.util.function.BiConsumer
import java.util.function.BiFunction


object PatternRegistry {
    private val GRAND_REGISTRY: HashMap<List<HexAngle>, Pair<Action, ResourceLocation>> = HashMap()
    private val ALTERNATIVE_REGISTRY: MutableList<GrandPatternResolve> = mutableListOf()
    private val deferred: HashMap<ResourceLocation, ActionRegistryEntry> = HashMap()
    fun registerGrandSpells(pattern: List<HexAngle>, action: Action, namespace: ResourceLocation) {
        if (GRAND_REGISTRY.containsKey(pattern)) {
            throw IllegalArgumentException(
                "a pattern is allready registered under that pattern id: %s sig: %s trying to register %s".format(
                    GRAND_REGISTRY[pattern]?.second,
                    pattern.toSig(),
                    namespace

                )
            )
        }
        GRAND_REGISTRY[pattern] = Pair(action, namespace)
    }

    val THE_FUNNY = pattern(HexDir.WEST, "dewdeqwwedaqedwadweqewwd", "suicide", OpSuicide(), true)
    val INFUSE_WILL = pattern(HexDir.SOUTH_WEST, "waawaawaqwaeaeaeaeaea", "infusion", OpEnlightenPattern())
    val PAGE_RIGHT = pattern(
        HexDir.SOUTH_WEST, "qqaw", "page/right",
        OpPageFlip(true)
    )
    val PAGE_LEFT = pattern(
        HexDir.SOUTH_EAST, "eedw", "page/left",
        OpPageFlip(false)
    )

    val NUKE_CHUNK_NOWILL = pattern(
        HexDir.SOUTH_EAST, "edewedewede", "nuke_chunk_nowill", OpLackingWill
    )



    init {
        // grand flaying
        patternGrand(
            HexActions.BRAINSWEEP,
            "mindflay",
            OpMindflayPlus,
            true
        )
        patternGrand(HexActions.EXPLODE,"explode", OpBiggerBomb(false), false)
        patternGrand(HexActions.`EXPLODE$FIRE`, "fireball", OpBiggerBomb(true), false)
        //patternGrand(HexalActions.GATE_)
    }


    private fun pattern(
        start: HexDir,
        angles: String,
        name: String,
        action: Action,
        isGrand: Boolean = false
    ): ActionRegistryEntry {
        val pat = patternAllowIllegal(start, angles)
        val resourceLocation = ResourceLocation(HexTweaks.MOD_ID, name)

        val ARE = ActionRegistryEntry(pat, action)
        if (isGrand) {
            registerGrandSpells(pat.angles, action, resourceLocation)
        } else {
            if (deferred.containsKey(resourceLocation)) throw IllegalArgumentException("two patterns are vying for $resourceLocation id. fix this")
            deferred[resourceLocation] = ARE
        }
        return ARE
    }

    private fun patternGrand(
        parent: ActionRegistryEntry,
        name: String,
        action: Action,
        parentIsGreat: Boolean = false
    ) {
        if (parentIsGreat) {
            registerAlternative { angles, env ->
                val reg = IXplatAbstractions.INSTANCE.actionRegistry
                val save = ScrungledPatternsSave.open(env.world)
                val lookup = save.lookup(angles.toSig())
                if (lookup != null) {
                    if (reg.get(lookup.key) == parent) {
                        return@registerAlternative Optional.of(
                            Pair(
                                action,
                                ResourceLocation("hextweaks", name)
                            )
                        )
                    }
                }
                return@registerAlternative Optional.empty()
            }
        } else {
            val resourceLocation = ResourceLocation("hextweaks",name)
            registerGrandSpells(
                parent.prototype.angles, action, resourceLocation
            )
        }
    }

    fun registerAlternative(fn: GrandPatternResolve) = ALTERNATIVE_REGISTRY.add(fn)

    fun getGrandEntry(sigs: List<HexAngle>, env: CastingEnvironment): Pair<Action, ResourceLocation>? {
        var registry_check = GRAND_REGISTRY[sigs]
        if (registry_check == null) {
            for (func in ALTERNATIVE_REGISTRY) {
                val res = func.apply(sigs, env)
                if (res.isPresent) {
                    registry_check = res.get()
                }
            }
        }
        val caster = env.castingEntity as? ServerPlayer
        if (registry_check != null) {
            if (caster != null) {
                val resloc = registry_check.second
                val advid = ResourceLocation(resloc.namespace, "grandspell/%s".format(resloc.path))
                //HexTweaks.LOGGER.info("Trying to grant %s advancement".format(advid))
                val adv = caster.server.advancements.getAdvancement(advid)
                if (adv != null) {
                    caster.advancements.award(adv, ItemLoreFragment.CRITEREON_KEY)
                }
            }
        }
        return registry_check
    }

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

    fun getGrandSpellPattern(player: ServerPlayer, level: ServerLevel, pat: HexPattern): HexPattern =
        getGrandSpellPattern(player.uuid, level.seed, pat)

    fun getGrandSpellPattern(uuid: UUID, seed: Long, pat: HexPattern): HexPattern {
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
        return HexPattern(pat.startDir, resulting)
    }

    fun register(r: BiConsumer<ActionRegistryEntry, ResourceLocation>) {
        for ((key, value) in deferred) {
            r.accept(value, key)
        }
    }
}

typealias GrandPatternResolve = BiFunction<
        List<HexAngle>,
        CastingEnvironment,
        Optional<Pair<Action, ResourceLocation>>
        >

fun List<HexAngle>.toSig(): String {
    var out = ""
    for (angle in this) {
        out += when (angle) {
            HexAngle.FORWARD -> 'w'
            HexAngle.RIGHT -> 'e'
            HexAngle.RIGHT_BACK -> 'd'
            HexAngle.BACK -> 's'
            HexAngle.LEFT -> 'q'
            HexAngle.LEFT_BACK -> 'a'
        }
    }
    return out
}
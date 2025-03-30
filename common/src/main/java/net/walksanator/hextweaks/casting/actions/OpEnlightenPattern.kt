package net.walksanator.hextweaks.casting.actions

import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPattern
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.Util
import net.walksantor.hextweaks.casting.PatternRegistry

class OpEnlightenPattern : SpellAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val input = args.getPattern(0)
        return SpellAction.Result(EnlightenResult(input),MediaConstants.DUST_UNIT * input.angles.size,listOf())
    }

    class EnlightenResult(val arg: HexPattern) : VariableMediaActionResult() {
        override fun execute(env: CastingEnvironment): List<Iota> = listOf(
            PatternIota(
                PatternRegistry.getGrandSpellPattern(
                    env.castingEntity?.uuid?: Util.NIL_UUID,
                    env.world.seed, arg)
            )
        )


    }
}
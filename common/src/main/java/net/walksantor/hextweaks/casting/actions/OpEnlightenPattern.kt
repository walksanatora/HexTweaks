package net.walksantor.hextweaks.casting.actions

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPattern
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.Util
import net.minecraft.network.chat.Component
import net.walksantor.hextweaks.casting.PatternRegistry

class OpEnlightenPattern : VariableMediaAction {
    override val argc = 1
    override fun render(args: List<Iota>, env: CastingEnvironment): VariableMediaAction.VariableMediaActionResult {
        val input = args.getPattern(0)
        return Result(input,MediaConstants.DUST_UNIT * input.angles.size)
    }

    class Result(val arg: HexPattern, cost: Long) : VariableMediaAction.VariableMediaActionResult(mediaConsumed = cost) {
        override fun execute(env: CastingEnvironment): List<Iota> = listOf(
            PatternIota(
                PatternRegistry.getGrandSpellPattern(
                    env.castingEntity?.uuid?: Util.NIL_UUID,
                    env.world.seed, arg)
            )
        )


    }
}
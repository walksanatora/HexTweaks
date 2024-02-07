package net.walksantor.hextweaks.casting.actions

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPattern
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.Util
import net.walksantor.hextweaks.casting.PatternRegistry

class OpEnlightenPattern : VariableMediaAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): VariableMediaAction.VariableMediaActionResult {
        val input = args.getPattern(0,argc)
        return VariableMediaAction.VariableMediaActionResult(
            listOf(PatternIota(PatternRegistry.getGrandSpellPattern(env.castingEntity?.uuid?: Util.NIL_UUID,env.world.seed,input))),
            MediaConstants.DUST_UNIT*input.angles.size
        )
    }
}
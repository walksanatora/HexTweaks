package net.walksantor.hextweaks.casting.actions

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.env.CircleCastEnv
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.circle.MishapNoSpellCircle

object OpWaveRefl : ConstMediaAction {
    override val argc: Int = 0
    override val mediaCost: Long = 0
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        if (env !is CircleCastEnv) {throw MishapNoSpellCircle()}
        return env.circleState().currentPos.asActionResult
    }
}
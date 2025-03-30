package net.walksanator.hextweaks.casting.actions

import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import net.walksantor.hextweaks.casting.continuation.ContinuationWhile

object OpLoopWhile : Action {
    override fun operate(
        env: CastingEnvironment,
        image: CastingImage,
        continuation: SpellContinuation
    ): OperationResult {
        val stack = image.stack.toMutableList()
        val exec =  stack.removeLast()
        val loop = if (exec !is ListIota) {
            ListIota(listOf(exec)).list
        } else {
            exec.list
        }
        return OperationResult(image.copy(stack),listOf(),continuation.pushFrame(
            ContinuationWhile(loop)), HexEvalSounds.THOTH
        )
    }
}
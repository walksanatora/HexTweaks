package net.walksanator.hextweaks.casting.actions

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.iota.Iota

abstract class VariableMediaActionResult : RenderedSpell {
    abstract fun execute(env: CastingEnvironment): List<Iota>
    override fun cast(env: CastingEnvironment) {
        println("dummy!")
    }
    override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage? {
        val stack = image.stack.toMutableList()
        stack.addAll(execute(env))
        return image.copy(stack, opsConsumed = image.opsConsumed + 1)
    }
}
package net.walksanator.hextweaks.patterns

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.DoubleIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.ListIota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import net.minecraft.network.chat.Component
import kotlin.math.roundToInt

class OpItterator : ConstMediaAction {
    override val argc = 1
    override val isGreat = false
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val num = args[0]
        if (num !is DoubleIota) {
            throw MishapInvalidIota(num,0, Component.literal("Not a Number"))
        }
        val size = num.double.roundToInt()
        val output = List(size) { DoubleIota(it.toDouble()) }
        return listOf(ListIota(output))
    }
}
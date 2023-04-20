package net.walksanator.hextweaks.patterns

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import net.walksanator.hextweaks.iotas.DictionaryIota

class OpNewDict : ConstMediaAction {
    override val argc = 0
    override val isGreat = false
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        return listOf(DictionaryIota(HashMap()));
    }
}
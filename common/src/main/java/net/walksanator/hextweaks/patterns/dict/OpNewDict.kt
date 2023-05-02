package net.walksanator.hextweaks.patterns.dict

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import net.walksanator.hextweaks.iotas.DictionaryIota
import java.util.ArrayList

class OpNewDict : ConstMediaAction {
    override val argc = 0
    override val isGreat = false
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        return listOf(DictionaryIota(Pair<List<Iota>,List<Iota>>(ArrayList<Iota>(), ArrayList<Iota>())))
    }
}
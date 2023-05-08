package net.walksanator.hextweaks.patterns.dict

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.ListIota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import net.minecraft.network.chat.Component
import net.walksanator.hextweaks.iotas.DictionaryIota

class OpDictBreak : ConstMediaAction {
    override val argc = 1
    override val isGreat = false
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val d = args[0]
        if (d !is DictionaryIota) {throw MishapInvalidIota(d,0, Component.translatable("hextweaks.mishap.notadict"))}
        val p = d.payload
        return listOf(ListIota(p.first),ListIota(p.second))
    }
}
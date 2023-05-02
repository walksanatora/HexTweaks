package net.walksanator.hextweaks.patterns.dict

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.ListIota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import net.minecraft.network.chat.Component
import net.walksanator.hextweaks.HexTweaks
import net.walksanator.hextweaks.iotas.DictionaryIota

class OpDictFrom : ConstMediaAction {
    override val argc = 2
    override val isGreat = false
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val first = args[0]
        val second = args[1]
        if (first !is ListIota) {throw MishapInvalidIota(first,0, Component.translatable("hextweaks.list.expected"))}
        if (second !is ListIota) {throw MishapInvalidIota(second,0, Component.translatable("hextweaks.list.expected"))}

        //just the iotas
        val ikeys = first.list.toList()
        val ivals = first.list.toList()

        //real output
        val iota = DictionaryIota()

        //apply the constraints on key types
        if (ikeys.size >= ivals.size) {
            for ((i,v) in ikeys.withIndex()) {
                if (v.javaClass in HexTweaks.cannotBeDictKey) {continue}
                iota.set(v,ivals[i],ctx.caster)
            }
        } else {
            for ((i,v) in ivals.withIndex()) {
                if (ikeys[i].javaClass in HexTweaks.cannotBeDictKey) {continue}
                iota.set(ikeys[i],v,ctx.caster)
            }
        }

        //return the dictionary
        return listOf(iota)
    }
}
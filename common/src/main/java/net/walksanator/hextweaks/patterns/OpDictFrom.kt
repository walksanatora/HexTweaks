package net.walksanator.hextweaks.patterns

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.ListIota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import net.minecraft.network.chat.Component
import net.walksanator.hextweaks.HexTweaks
import net.walksanator.hextweaks.iotas.DictionaryIota
import java.util.ArrayList

class OpDictFrom : ConstMediaAction {
    override val argc = 2
    override val isGreat = false
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val first = args[0]
        val second = args[1]
        if (first !is ListIota) {throw MishapInvalidIota(first,0, Component.translatable("hextweaks.list.expected"))}
        if (second !is ListIota) {throw MishapInvalidIota(second,0, Component.translatable("hextweaks.list.expected"))}

        val ikeys =  ArrayList<Iota>()
        first.list.iterator().forEachRemaining(ikeys::add)
        val ivals = ArrayList<Iota>()
        first.list.iterator().forEachRemaining(ivals::add)

        val keys = ArrayList<Iota>()
        val values = ArrayList<Iota>()

        var size = 0
        if (ikeys.size >= ivals.size) {
            for ((i,v) in ikeys.withIndex()) {
                if (size >= HexTweaks.MaxKeysInDictIota) {break}
                if (v.javaClass in HexTweaks.cannotBeDictKey) {continue}
                keys.add(v)
                values.add(ivals[i])
                size ++
            }
        } else {
            for ((i,v) in ivals.withIndex()) {
                if (size >= HexTweaks.MaxKeysInDictIota) {break}
                if (ikeys[i].javaClass in HexTweaks.cannotBeDictKey) {continue}
                keys.add(ikeys[i])
                values.add(v)
                size ++
            }
        }

        return listOf(DictionaryIota(Pair<List<Iota>,List<Iota>>(keys,values)))
    }
}
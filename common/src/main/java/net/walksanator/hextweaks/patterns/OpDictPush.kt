package net.walksanator.hextweaks.patterns

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.spell.orNull
import net.minecraft.network.chat.Component
import net.walksanator.hextweaks.iotas.DictionaryIota
import net.walksanator.hextweaks.iotas.HextweaksIotaType

class OpDictPush : ConstMediaAction {
    override val argc = 3
    override val isGreat = false
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val dict = args[0]
        val key = args[1].orNull()
        val value = args[2].orNull()
        if (dict.type == HextweaksIotaType.DICTIONARY) {
            val data = (dict as DictionaryIota).payload
            data[key] = value
            return listOf(DictionaryIota(data))
        } else {
            throw MishapInvalidIota(dict,2, Component.translatable("hextweaks.mishap.notadict"))
        }
    }


}
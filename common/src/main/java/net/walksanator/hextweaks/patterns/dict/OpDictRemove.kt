package net.walksanator.hextweaks.patterns.dict

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import net.minecraft.network.chat.Component
import net.walksanator.hextweaks.HexTweaks
import net.walksanator.hextweaks.iotas.DictionaryIota
import net.walksanator.hextweaks.iotas.HextweaksIotaType

class OpDictRemove : ConstMediaAction {
    override val argc = 2
    override val isGreat = false

    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val dict = args[0]
        val key = args[1]
        if (dict.type == HextweaksIotaType.DICTIONARY) {
            if (HexTweaks.cannotBeDictKey.contains(key.javaClass)) {
                throw MishapInvalidIota(key,1, Component.translatable("hextweaks.mishap.cannotbekey"))
            } else {
                (dict as DictionaryIota).remove(key)
                return listOf(dict)
            }
        } else {
            throw MishapInvalidIota(dict,2, Component.translatable("hextweaks.mishap.notadict"))
        }
    }


}
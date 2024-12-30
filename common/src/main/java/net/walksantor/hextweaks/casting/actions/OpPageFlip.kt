package net.walksantor.hextweaks.casting.actions

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import at.petrak.hexcasting.common.items.storage.ItemSpellbook
import at.petrak.hexcasting.common.lib.HexItems
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.network.chat.Component

class OpPageFlip(
    private val rotateRight: Boolean
) : ConstMediaAction {
    override val argc = 0
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {

        val res = env.getHeldItemToOperateOn {
            it.`is`(HexItems.SPELLBOOK.asItem())
        }
        if (res == null) {return listOf()}
        val handStack = res.component1()
        val hand = res.component2()

        if (handStack.`is`(HexItems.SPELLBOOK.asItem())) {
            ItemSpellbook.rotatePageIdx(handStack,rotateRight)
        }

        return listOf()
    }

}

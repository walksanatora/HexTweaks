package net.walksanator.hextweaks.patterns

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapBadOffhandItem
import at.petrak.hexcasting.xplat.IXplatAbstractions
import at.petrak.hexcasting.common.lib.HexItems
import at.petrak.hexcasting.common.items.ItemSpellbook
import net.minecraft.network.chat.Component


class OpPageFlip(
        private val rotateRight: Boolean
) : ConstMediaAction {
    override val argc = 0
    override val isGreat = false

    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {

        val (handStack, hand) = ctx.getHeldItemToOperateOn {
            val dataHolder = IXplatAbstractions.INSTANCE.findDataHolder(it)
            dataHolder != null && (dataHolder.readIota(ctx.world) != null || dataHolder.emptyIota() != null)
        }

        if (handStack.`is`(HexItems.SPELLBOOK.asItem())) {
            ItemSpellbook.rotatePageIdx(handStack,rotateRight)
        } else {
            throw MishapBadOffhandItem(handStack,hand, Component.literal("Spellbook"))
        }

        return listOf()
    }

}
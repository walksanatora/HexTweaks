package net.walksantor.hextweaks.casting.actions

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style

object OpLackingWill : ConstMediaAction {
    override val argc: Int = 0
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        env.printMessage(
            Component.translatable("hextweaks.action.lackwill")
                .append(Component.translatable("hextweaks.action.lackwill.will").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.DARK_AQUA)))
        return listOf()
    }
}
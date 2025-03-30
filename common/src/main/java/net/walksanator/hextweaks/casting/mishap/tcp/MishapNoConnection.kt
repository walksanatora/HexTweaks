package net.walksanator.hextweaks.casting.mishap.tcp

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.common.lib.HexItems
import net.minecraft.Util
import net.minecraft.network.chat.Component
import net.minecraft.world.item.DyeColor

class MishapNoConnection : Mishap() {
    override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment = FrozenPigment(
        HexItems.DYE_PIGMENTS[DyeColor.LIGHT_BLUE]!!.defaultInstance,
        Util.NIL_UUID
    )

    override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Component? = Component.translatable("hextweaks.mishap.tcp.disconnect")

    override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
        //noop
    }
}
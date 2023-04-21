package net.walksanator.hextweaks.mishap

import at.petrak.hexcasting.api.misc.FrozenColorizer
import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.mishaps.Mishap
import net.minecraft.network.chat.Component
import net.minecraft.world.item.DyeColor
import net.walksanator.hextweaks.iotas.DictionaryIota

class MishapDictionaryTooBig(private val offender: DictionaryIota) : Mishap() {
    override fun accentColor(ctx: CastingContext, errorCtx: Context): FrozenColorizer = dyeColor(DyeColor.RED)

    override fun errorMessage(ctx: CastingContext, errorCtx: Context): Component {
        return Component.translatable("hextweaks.mishap.dicttoolarge",offender.display())
    }

    override fun execute(ctx: CastingContext, errorCtx: Context, stack: MutableList<Iota>) {

    }
    override fun particleSpray(ctx: CastingContext) = ParticleSpray.burst(ctx.caster.position(), 2.0)
}
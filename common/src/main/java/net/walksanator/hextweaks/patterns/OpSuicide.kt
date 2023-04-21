package net.walksanator.hextweaks.patterns

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.Iota
import net.walksanator.hextweaks.dammagesources.DamageSourceSus


class OpSuicide : ConstMediaAction {
    override val argc = 0
    override val isGreat = true
    override val causesBlindDiversion = true

    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        ctx.caster.hurt(DamageSourceSus(),Float.MAX_VALUE)
        return listOf()
    }
}
package net.walksantor.hextweaks.casting.actions

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPositiveDoubleUnderInclusive
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.common.casting.actions.spells.OpExplode
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

class OpBiggerBomb(val fireball: Boolean) : SpellAction {
    override val argc = 2
    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val pos = args.getVec3(0, argc)
        val strength = args.getPositiveDoubleUnderInclusive(1, 20.0, argc)
        env.assertVecInRange(pos)

        val clampedStrength = Mth.clamp(strength, 0.0, 10.0)
        val cost = MediaConstants.DUST_UNIT * (3 * clampedStrength + if (fireball) 1.0 else 0.125)
        return SpellAction.Result(
            Spell(pos, strength, fireball),
            cost.toLong(),
            listOf(ParticleSpray.burst(pos, strength, 50))
        )
    }
    private data class Spell(val pos: Vec3, val strength: Double, val fire: Boolean) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            // TODO: you can use this to explode things *outside* of the worldborder?
            if (!env.canEditBlockAt(BlockPos.containing(pos)))
                return

            env.world.explode(
                env.castingEntity,
                pos.x,
                pos.y,
                pos.z,
                strength.toFloat(),
                this.fire,
                Level.ExplosionInteraction.TNT
            )
        }
    }
}
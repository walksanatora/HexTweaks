package net.walksantor.hextweaks.casting.actions

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getList
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.common.items.ItemLoreFragment
import com.mojang.datafixers.util.Either

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Mob
import net.minecraft.world.phys.Vec3
import net.walksantor.hextweaks.HexTweaks
import net.walksantor.hextweaks.casting.MindflayRegistry
import net.walksantor.hextweaks.casting.mindflay.MindflayInput

object OpMindflayPlus : SpellAction {
    override val argc = 2
    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val sacrificed = args.getList(0,argc)
        val target = args[1];
        if (!(target is EntityIota || target is Vec3Iota)) {throw MishapInvalidIota(target,2, Component.translatable("iota.entity_or_position"))}

        val filter = sacrificed.filterIsInstance<EntityIota>()
            .filter { it.entity is Mob }
            .filter { env.isVecInRange(it.entity.position()) }
            .map { it.entity as Mob }
        val targetIota: Either<EntityIota,Vec3Iota> = if (target is EntityIota) {
            Either.left(target)
        } else {
            Either.right(target as Vec3Iota)
        }

        return SpellAction.Result(
            Spell(filter, targetIota),
            MediaConstants.CRYSTAL_UNIT,
            filter.map { ParticleSpray( it.position(), Vec3.ZERO, 1.0, 1.0, 20) },
        )
    }
    class Spell(val sacrafices: List<Mob>,val target: Either<EntityIota,Vec3Iota>) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            MindflayRegistry.performMindflays(
                MindflayInput(sacrafices,target,env)
            )
        }

    }
}
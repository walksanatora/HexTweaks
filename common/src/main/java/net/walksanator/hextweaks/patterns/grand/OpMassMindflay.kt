package net.walksanator.hextweaks.patterns.grand

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.RenderedSpell
import at.petrak.hexcasting.api.spell.SpellAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getList
import at.petrak.hexcasting.api.spell.iota.EntityIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.Vec3Iota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.npc.Villager
import net.walksanator.hextweaks.HexTweaks
import kotlin.math.pow
import kotlin.math.roundToInt

class OpMassMindflay : SpellAction {
    override val argc: Int = 2
    override val isGreat: Boolean = true//require enlightenment
    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        val sacrifices = args.getList(0)
        val target = args[1]
        val spray = when (target) {
            is Vec3Iota -> {
                ctx.assertVecInRange(target.vec3)
                ParticleSpray.cloud(target.vec3, 2.0, 30)
            }
            is EntityIota -> {
                ctx.assertEntityInRange(target.entity)
                ParticleSpray.cloud(target.entity.position(), 2.0, 30)
            }
            else -> throw MishapInvalidIota(target, 1, Component.translatable("iota.vec3_or_entity"))
        }
        val filteredSacrifices = sacrifices.filter { v -> v is EntityIota && v.entity is Villager } .map { v -> (v as EntityIota).entity!! as Villager }

        ctx.world.level.server.advancements

        val poofs = filteredSacrifices.map { villager ->
            ParticleSpray.burst(villager.position(),1.0,5)
        }

        return Triple(
             if (target is Vec3Iota) {
                 MassMindflayBlockSpell(sacrifices.filterIsInstance<EntityIota>().map{ v -> v.entity},BlockPos(target.vec3))
             } else {
                MassMindflayItemSpell(sacrifices.filterIsInstance<EntityIota>().map{ v -> v.entity},target as EntityIota)
                    },
            MediaConstants.SHARD_UNIT * poofs.size,
            listOf(
                spray
            ) + poofs
        )
    }

    private data class MassMindflayBlockSpell(val targets: List<Entity>, val position: BlockPos) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            //we already know that target is
            // 1. a List of sacrifices
            // 2. the position is within ambit
            var accu = 0
            for (v in targets.filterIsInstance<Villager>()) {
                accu += (2.0.pow(v.villagerData.level-1)).roundToInt()
            }
            val validSacrificeOperators = HexTweaks.massSacrificeHandlers.filterKeys { key -> key <= accu }
            for (sacrificeOperation in validSacrificeOperators.keys.sorted().reversed()) {
                if (validSacrificeOperators[sacrificeOperation]?.call(targets,position,accu,ctx) == true) {
                    for (poorSchmuck in targets) {
                        poorSchmuck.kill()//they have been used
                    }
                    break
                }
            }
        }
    }
    private data class MassMindflayItemSpell(val targets: List<Entity>, val target: EntityIota) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            //we already know that target is
            // 1. a List of entities
            // 2. the target is within ambit
            var accu = 0
            for (v in targets.filterIsInstance<Villager>()) {
                accu += (2.0.pow(v.villagerData.level)).roundToInt()
            }

        }
    }


}
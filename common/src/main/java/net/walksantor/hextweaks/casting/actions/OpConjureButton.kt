package net.walksantor.hextweaks.casting.actions

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getDouble
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.common.blocks.BlockConjured
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.DirectionalPlaceContext
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.Vec3
import net.walksantor.hextweaks.HexTweaksRegistry

class OpConjureButton : SpellAction {
    override val argc = 3
    override fun execute(
        args: List<Iota>,
        env: CastingEnvironment
    ): SpellAction.Result {
        val vecPos = args.getVec3(0, argc)
        val direction = args.getVec3(1,argc)
        val media = args.getDouble(2,argc)

        val placedir = Direction.getNearest(direction.x,direction.y,direction.z)

        val pos = BlockPos.containing(vecPos)
        env.assertPosInRangeForEditing(pos)

        val placeContext = DirectionalPlaceContext(env.world, pos, placedir, ItemStack.EMPTY, placedir.opposite)

        val worldState = env.world.getBlockState(pos)
        if (!worldState.canBeReplaced(placeContext))
            throw MishapBadBlock.of(pos, "replaceable")

        return SpellAction.Result(
            Spell(pos, placedir, media),
            MediaConstants.DUST_UNIT + (MediaConstants.DUST_UNIT * media).toLong(),
            listOf(ParticleSpray.cloud(Vec3.atCenterOf(pos), 1.0))
        )
    }

    private data class Spell(val pos: BlockPos, val direction: Direction, val dust: Double) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            if (!env.canEditBlockAt(pos))
                return

            val placeContext = DirectionalPlaceContext(env.world, pos, direction, ItemStack.EMPTY, direction.opposite)

            val worldState = env.world.getBlockState(pos)
            if (worldState.canBeReplaced(placeContext)) {
                val block = Blocks.AIR// HexTweaksRegistry.CONJURED_BUTTON.get()

                if (!IXplatAbstractions.INSTANCE.isPlacingAllowed(env.world, pos, ItemStack(block), env.caster))
                    return

                val state = block.getStateForPlacement(placeContext)
                if (state != null) {
                    env.world.setBlock(pos, state, 5)

                    val pigment = env.pigment

                    if (env.world.getBlockState(pos).block is BlockConjured) {
                        BlockConjured.setColor(env.world, pos, pigment)
                    }
                }
            }
        }
    }
}

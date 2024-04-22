package net.walksantor.hextweaks.casting.mindflay

import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.walksantor.hextweaks.casting.MindflayRegistry
import ram.talia.hexal.common.entities.BaseWisp
import ram.talia.hexal.common.entities.WanderingWisp
import ram.talia.hexal.common.lib.HexalBlocks
import kotlin.random.Random

object MindflaySlipwayRitual {
    fun createSlipway(input: MindflayInput): MindflayResult {
        if (input.target !is EntityIota) {return MindflayResult(false)}
        val entity = input.target.entity
        if (entity !is BaseWisp) {return MindflayResult(false) } // there is no wisp

        val points = MindflayRegistry.calcuateVillagerPoints(input.inputs)
        if (points < 80) { return MindflayResult(false) } // insufficient points

        val block = input.env.world.getBlockState(entity.blockPosition())
        if (!block.isAir) { return MindflayResult(false) } //wisp is inside a block. so no placing

        input.env.world.setBlock(
            entity.blockPosition(),
            HexalBlocks.SLIPWAY.defaultBlockState(),
            Block.UPDATE_NEIGHBORS
        )

        MindflayRegistry.performBrainsweeps(input.inputs,input.env.caster)

        return MindflayResult(true)
    }

    fun burstSlipway(input: MindflayInput): MindflayResult {
        if (input.target !is Vec3Iota) {return MindflayResult(false)}
        val pos = input.target.vec3
        if (input.env.isVecInRange(pos)) {return MindflayResult(false) } // out of range

        val world = input.env.world
        val bpos = BlockPos(
            pos.x().toInt(),
            pos.y().toInt(),
            pos.z().toInt()
        )

        val points = MindflayRegistry.calcuateVillagerPoints(input.inputs)
        if (points < 16) { return MindflayResult(false) }

        val state = world.getBlockState(bpos)
        if (state.block != HexalBlocks.SLIPWAY) { return MindflayResult(false) } //that isn't a slipway so we cant collapse it

        world.setBlock(bpos, Blocks.VOID_AIR.defaultBlockState(), Block.UPDATE_NEIGHBORS)

        for (x in 0..10+Random.nextInt(10,20)) {
            val wisp = WanderingWisp(world, bpos.center)
            world.addFreshEntity(wisp)
        }

        MindflayRegistry.performBrainsweeps(input.inputs,input.env.caster)

        return MindflayResult(true)
    }
}
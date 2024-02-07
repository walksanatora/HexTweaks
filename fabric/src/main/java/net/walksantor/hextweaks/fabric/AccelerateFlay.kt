package net.walksantor.hextweaks.fabric

import de.dafuqs.spectrum.blocks.titration_barrel.TitrationBarrelBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.monster.Witch
import net.walksantor.hextweaks.casting.MindflayRegistry
import net.walksantor.hextweaks.casting.mindflay.MindflayInput
import net.walksantor.hextweaks.casting.mindflay.MindflayResult
import net.walksantor.hextweaks.fabric.mixin.SealTimeAccessor
import kotlin.jvm.optionals.getOrNull
import kotlin.math.min

object AccelerateFlay {
    fun skip12hours(input: MindflayInput): MindflayResult {
        val pos = input.target.right().getOrNull()?.vec3 ?: return MindflayResult(false) //not a position
        val env = input.env
        if (env.isVecInRange(pos)) {return MindflayResult(false)}
        val blockpos = BlockPos(
            pos.x().toInt(),
            pos.y().toInt(),
            pos.z().toInt()
        )
        val be = env.world.getBlockEntity(blockpos)?: return MindflayResult(false) // no block entity at specified position
        if (be !is TitrationBarrelBlockEntity) {return MindflayResult(false) } // the block entity is not a titration barrel
        val be_accessor = (be as SealTimeAccessor)
        val points = MindflayRegistry.calcuateVillagerPoints(input.inputs)
        val witches = input.inputs.filterIsInstance<Witch>().size * 8
        val skip_ammount = min(points,witches)/8

        var time = be_accessor.sealTime
        time -= skip_ammount.toLong() * 43200000L
        be_accessor.sealTime = time

        MindflayRegistry.performBrainsweeps(input.inputs,env.caster)

        return MindflayResult(true)
    }
}
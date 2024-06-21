package net.walksantor.hextweaks.casting.actions

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.env.CircleCastEnv
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.circle.MishapNoSpellCircle
import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.world.level.block.state.properties.AttachFace
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.Vec3

object OpSlateNormal : ConstMediaAction {
    override val argc: Int = 0
    override val mediaCost: Long = 0
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        if (env !is CircleCastEnv) {throw MishapNoSpellCircle()}
        val pos = env.circleState().currentPos
        val slate = env.world.getBlockState(pos)
        val attach = slate.getValue(BlockStateProperties.ATTACH_FACE)
        val normal = when (attach) {
            AttachFace.WALL -> slate.getValue(BlockStateProperties.HORIZONTAL_FACING).normal
            AttachFace.CEILING -> Vec3i(0,-1,0)
            AttachFace.FLOOR -> Vec3i(0,1,0)
            null -> Vec3i(0,0,0) // whut?
        }.asVec3()
        return normal.asActionResult
    }
}

fun Vec3i.asVec3(): Vec3 = Vec3(this.x.toDouble(),this.y.toDouble(),this.z.toDouble())
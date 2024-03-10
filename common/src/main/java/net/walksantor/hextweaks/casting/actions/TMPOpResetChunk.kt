package net.walksantor.hextweaks.casting.actions

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.iota.Iota
import net.minecraft.world.level.ChunkPos
import net.walksantor.hextweaks.MojankResetChunk

class TMPOpResetChunk : ConstMediaAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val where = args.getBlockPos(0)
        MojankResetChunk.resetChunk(env.world, ChunkPos(where))
        return listOf()
    }
}
package net.walksantor.hextweaks.casting.actions

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.Util
import ram.talia.hexal.api.casting.iota.GateIota
import ram.talia.hexal.api.gates.GateManager

class OpOpenedGates : VariableMediaAction {
    override val argc = 0
    override fun render(args: List<Iota>, env: CastingEnvironment): VariableMediaAction.VariableMediaActionResult {
        val uuid = env.castingEntity?.uuid ?: Util.NIL_UUID
        val gates = GateManager.allMarked.asIterable().filter { kv -> kv.value.contains(uuid) }.map { kv -> kv.key }

        return ActionRes(gates, MediaConstants.SHARD_UNIT * 32 * gates.size)
    }

    class ActionRes(val gates: List<Int>, cost: Long) : VariableMediaAction.VariableMediaActionResult(cost) {
        override fun execute(env: CastingEnvironment): List<Iota> =
            listOf(ListIota(gates.map { DoubleIota(it.toDouble()) }))
    }

}
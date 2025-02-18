package net.walksantor.hextweaks.casting.actions

import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.Util
import ram.talia.hexal.api.casting.iota.GateIota
import ram.talia.hexal.api.gates.GateManager

class OpOpenedGates : SpellAction {
    override val argc = 0
    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val uuid = env.castingEntity?.uuid ?: Util.NIL_UUID
        val gates = GateManager.allMarked.asIterable().filter { kv -> kv.value.contains(uuid) }.map { kv -> kv.key }

        return SpellAction.Result(
            ActionRes(gates),
            MediaConstants.SHARD_UNIT * 32 * gates.size,
            listOf()
        )
    }

    class ActionRes(val gates: List<Int>) : VariableMediaActionResult() {
        override fun execute(env: CastingEnvironment): List<Iota> =
            listOf(ListIota(gates.map { DoubleIota(it.toDouble()) }))
    }

}
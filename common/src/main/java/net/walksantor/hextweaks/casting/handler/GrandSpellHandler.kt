package net.walksantor.hextweaks.casting.handler

import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.castables.SpecialHandler
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.math.HexAngle
import at.petrak.hexcasting.api.casting.math.HexPattern
import net.minecraft.Util
import net.minecraft.network.chat.Component
import net.walksantor.hextweaks.casting.PatternRegistry
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.math.min

class GrandSpellHandler(private val action: Action) : SpecialHandler {
    override fun act(): Action = action

    override fun getName(): Component = Component.translatable("hextweaks.handler.grand")

     class Factory : SpecialHandler.Factory<GrandSpellHandler> {
        override fun tryMatch(pattern: HexPattern, env: CastingEnvironment): GrandSpellHandler? {
            val anglesigsNoBits: MutableList<HexAngle> = mutableListOf()
            val bitfield: MutableList<Boolean> = mutableListOf()
            val iter = ArrayDeque(pattern.angles)
            while (iter.size > 0) {
                val next = iter.removeFirst()
                if (next == HexAngle.BACK) {
                    if (iter.first() == HexAngle.BACK) {
                        iter.removeFirst() //2nd s
                        anglesigsNoBits.add(iter.removeFirst()) //the next char
                        bitfield.add(true)
                    }  else {
                        anglesigsNoBits.add(next)
                        bitfield.add(false)
                    }
                } else {
                    anglesigsNoBits.add(next)
                    bitfield.add(false)
                }
            }

            val uuid: UUID = env.castingEntity?.uuid ?: Util.NIL_UUID
            val seed = env.world.server.overworld().seed // consistent seed since these can differ legally
            val upper = uuid.mostSignificantBits.xor(seed)
            val lower = uuid.leastSignificantBits.xor(seed)

            val expectedBitfield: MutableList<Boolean> = mutableListOf()
            for (i in 63 downTo 0) {
                val bit = (upper shr i) and 1
                expectedBitfield.add(bit.toInt() == 1)
            }
            for (i in 63 downTo 0) {
                val bit = (lower shr i) and 1
                expectedBitfield.add(bit.toInt() == 1)
            }

            val slice = expectedBitfield.slice(0..<min(bitfield.size,expectedBitfield.size))
            val bitsMatch = slice==bitfield.slice(0..<min(bitfield.size,expectedBitfield.size))

//            val diff = slice.indices.filter { slice[it] != bitfield[it] }
//            HexTweaks.LOGGER.info("recieved: $bitfield")
//            HexTweaks.LOGGER.info("expected: $slice")
//            HexTweaks.LOGGER.info("diffed: $diff")

            val ret = if (bitsMatch) {
                val act = PatternRegistry.getGrandEntry(anglesigsNoBits,env)
                if (act==null) {
                    null
                } else {
                    GrandSpellHandler(act.first)
                }
            } else {
                null
            }

            return ret
        }

    }
}
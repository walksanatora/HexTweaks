package net.walksantor.hextweaks.casting.handler

import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.castables.SpecialHandler
import at.petrak.hexcasting.api.casting.math.HexAngle
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import com.ibm.icu.impl.UtilityExtensions
import net.minecraft.Util
import net.minecraft.network.chat.Component
import net.walksantor.hextweaks.duck.HexPatternSmugglingAccess
import java.util.*
import kotlin.collections.ArrayDeque

class GrandSpellHandler : SpecialHandler {
    override fun act(): Action {
        TODO("Not yet implemented")
    }

    override fun getName(): Component = Component.translatable("hextweaks.handler.grand")

     class Factory : SpecialHandler.Factory<GrandSpellHandler> {
        @Suppress("CAST_NEVER_SUCCEEDS") // mixin magic it can succede
        override fun tryMatch(pattern: HexPattern): GrandSpellHandler? {
            val smuggled = (pattern as HexPatternSmugglingAccess).`hexdim$getSmuggledEnv`()
            val anglesigs_no_bits: MutableList<HexAngle> = mutableListOf()
            val bitfield: MutableList<Boolean> = mutableListOf()
            val iter = ArrayDeque(pattern.angles)
            while (iter.size > 0) {
                val next = iter.removeFirst()
                if (next == HexAngle.BACK) {
                    if (iter.first() == HexAngle.BACK) {
                        iter.removeFirst()
                        bitfield.add(true)
                    }  else {
                        anglesigs_no_bits.add(next)
                        bitfield.add(false)
                    }
                } else {
                    anglesigs_no_bits.add(next)
                    bitfield.add(false)
                }

            }

            val uuid: UUID = smuggled.caster?.uuid?: Util.NIL_UUID
            val seed = smuggled.world.seed
            val upper = uuid.mostSignificantBits.xor(seed)
            val lower = uuid.leastSignificantBits.xor(seed)

            val expected_bits: MutableList<Boolean> = mutableListOf()
            for (i in 63 downTo 0) {
                val bit = (upper shr i) and 1
                expected_bits.add(bit.toInt() == 1)
            }
            for (i in 63 downTo 0) {
                val bit = (lower shr i) and 1
                expected_bits.add(bit.toInt() == 1)
            }

            val slice = expected_bits.slice(0..bitfield.size)
            val bits_match = slice==bitfield

            return null
        }

    }
}
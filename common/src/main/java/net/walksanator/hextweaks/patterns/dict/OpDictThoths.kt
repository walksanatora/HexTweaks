package net.walksanator.hextweaks.patterns.dict

import at.petrak.hexcasting.api.PatternRegistry
import at.petrak.hexcasting.api.spell.Action
import at.petrak.hexcasting.api.spell.OperationResult
import at.petrak.hexcasting.api.spell.SpellList
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.casting.eval.FrameForEach
import at.petrak.hexcasting.api.spell.casting.eval.SpellContinuation
import at.petrak.hexcasting.api.spell.getList
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.PatternIota
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.spell.mishaps.MishapNotEnoughArgs
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.walksanator.hextweaks.iotas.DictionaryIota

class OpDictThoths : Action {
    override fun operate(
        continuation: SpellContinuation,
        stack: MutableList<Iota>,
        ravenmind: Iota?,
        ctx: CastingContext
    ): OperationResult {
        if (stack.size < 2)
            throw MishapNotEnoughArgs(2, stack.size)

        val preinstrs = stack.getList(stack.lastIndex - 1)
        val dict = stack[stack.lastIndex]
        if (dict !is DictionaryIota) {
            throw MishapInvalidIota(dict,0, Component.translatable("hextweaks.mishap.notadict"))
        }

        val ikeys = dict.payload.first
        val ivals = dict.payload.second
        val datumslist = ArrayList<Iota>()
        for ((i,v) in ikeys.withIndex()) {
            datumslist.add(
                DictionaryIota(
                    Pair(
                        listOf(v),
                        listOf(ivals[i])
                    )
                )
            )
        }

        val datums = SpellList.LList(datumslist)

        val listinstrs = preinstrs.toMutableList()
        listinstrs.add(0,PatternIota(
            PatternRegistry.lookupPattern(
                ResourceLocation("hextweaks","dict/break/single")
            ).prototype
        ))

        val instrs = SpellList.LList(listinstrs)

        stack.removeLastOrNull()
        stack.removeLastOrNull()

        val frame = FrameForEach(datums, instrs, null, mutableListOf())

        return OperationResult(
            continuation.pushFrame(frame),
            stack,
            ravenmind,
            listOf()
        )
    }
}
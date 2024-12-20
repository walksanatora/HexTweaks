package net.walksantor.hextweaks.casting.continuation

import at.petrak.hexcasting.api.casting.SpellList
import at.petrak.hexcasting.api.casting.eval.CastResult
import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect
import at.petrak.hexcasting.api.casting.eval.vm.*
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.casting.mishaps.MishapEvalTooMuch
import at.petrak.hexcasting.api.utils.serializeToNBT
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.server.level.ServerLevel
import kotlin.math.max

class ContinuationWhile(val loop: SpellList) : ContinuationFrame {
    override val type = WhileType

    object WhileType : ContinuationFrame.Type<ContinuationWhile> {
        override fun deserializeFromNBT(tag: CompoundTag, world: ServerLevel): ContinuationWhile? {
            val iotas = tag.get("loop") as ListTag
           return ContinuationWhile(HexIotaTypes.LIST.deserialize(iotas,world)!!.list)
        }
    }

    override fun breakDownwards(stack: List<Iota>): Pair<Boolean, List<Iota>> = Pair(true,stack)

    override fun evaluate(continuation: SpellContinuation, level: ServerLevel, harness: CastingVM): CastResult {
        val (cont, img) = if (harness.image.stack.getOrNull(max(harness.image.stack.size-1,0))?.isTruthy == true) {
            if (!loop.nonEmpty) {
                // An empty loop that is about to start will never end, so just throw this mishap without wasting time.
                return CastResult(
                    ListIota(loop), continuation, null,
                    listOf(
                        OperatorSideEffect.DoMishap(
                            MishapEvalTooMuch(),
                            Mishap.Context(
                                null,
                                null
                            )
                        )
                    ),
                    ResolvedPatternType.ERRORED,
                    HexEvalSounds.MISHAP
                )
            }
            val cont = continuation.pushFrame(this).pushFrame(FrameEvaluate(loop,true))
            Pair(cont,harness.image.withUsedOp())
        } else {
            Pair(continuation,harness.image)
        }
        return CastResult(
            ListIota(loop),
            cont, img, listOf(), ResolvedPatternType.EVALUATED,
            HexEvalSounds.THOTH
        )
    }

    override fun serializeToNBT(): CompoundTag {
        val tag = CompoundTag()
        tag.put("loop",loop.serializeToNBT())
        return tag
    }

    override fun size(): Int {
        val iotas = mutableListOf<Iota>()
        iotas.addAll(this.loop)
        var accu = 0
        while (iotas.isNotEmpty()) {
            val iota = iotas.removeFirst()
            accu += 1;
            iotas.addAll(iota.subIotas()?: emptyList())
        }
        return accu
    }
}
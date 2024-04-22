package net.walksantor.hextweaks.casting.rituals

import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.BossEvent
import net.walksantor.hextweaks.HexTweaks
import net.walksantor.hextweaks.MojankResetChunk
import net.walksantor.hextweaks.entities.SpellBeaconEntity
import java.util.function.BiFunction
import kotlin.random.Random

typealias RitualFactory = BiFunction<BossEvent,CompoundTag,HexRitual>

object HexRitualRegistry {
    private val RITUALS = HashMap<ResourceLocation, RitualFactory>()
    //val REGISTRY = Registry
    fun register(resloc: ResourceLocation, ritual: RitualFactory) {
        if (RITUALS.containsKey(resloc)) {
            throw IllegalArgumentException("Resurce Location is allready in the Rituals ")
        }
        RITUALS[resloc] = ritual
    }
    fun getFactory(resloc: ResourceLocation): RitualFactory = RITUALS[resloc]?: throw IllegalArgumentException("Resource Location does not point to a loaded factory")

    init {
        register(ResetChunkRitual.id,::ResetChunkRitual)
    }

}

class ResetChunkRitual(bossBar: BossEvent, tag: CompoundTag) : HexRitual(bossBar, tag) {
    init {
        stepMedia = (16*16*384)*MediaConstants.QUENCHED_SHARD_UNIT
        stepTime = 1200
        bossBar.name = Component.translatable("hextweaks.ritual.reset")
    }

    override val totalSteps: Int = 20
    override val countdown: Int = 12000 //10 minutes


    override fun ritualFinished(sbe: SpellBeaconEntity) {
        MojankResetChunk.enque_reset(sbe.chunkPosition(),sbe.level() as ServerLevel)
    }

    override fun getLocation(): ResourceLocation = id

    companion object {
        val id = ResourceLocation(HexTweaks.MOD_ID,"reset_chunk")
    }
}
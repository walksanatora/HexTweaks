package net.walksantor.hextweaks.casting.rituals

import net.minecraft.data.models.blockstates.PropertyDispatch.TriFunction
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.BossEvent
import net.minecraft.world.level.Level
import net.walksantor.hextweaks.HexTweaks

typealias RitualFactory = TriFunction<CompoundTag, Level,BossEvent,HexRitual>

object HexRitualRegistry {
    private val RITUALS = HashMap<ResourceLocation,RitualFactory>()
    //val REGISTRY = Registry
    fun register(resloc: ResourceLocation, ritual: RitualFactory) {
        if (RITUALS.containsKey(resloc)) {
            throw IllegalArgumentException("Resurce Location is allready in the Rituals ")
        }
        RITUALS[resloc] = ritual
    }
    fun getFactory(resloc: ResourceLocation): RitualFactory {
        return RITUALS[resloc]?: throw IllegalArgumentException("Specified ritual does not exists in ritual registry")
    }
}
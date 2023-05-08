package net.walksanator.hextweaks.iotas

import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.IotaType
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.walksanator.hextweaks.HexTweaks
import org.jetbrains.annotations.ApiStatus
import java.util.function.BiConsumer

object HextweaksIotaType {
    @JvmStatic
    @ApiStatus.Internal
    fun registerTypes() {
        val r = BiConsumer { type: IotaType<*>, id: ResourceLocation -> Registry.register(HexIotaTypes.REGISTRY, id, type) }
        for ((key, value) in TYPES) {
            r.accept(value, key)
        }
    }

    private val TYPES: MutableMap<ResourceLocation, IotaType<*>> = LinkedHashMap()
    @JvmField
    val DICTIONARY: IotaType<DictionaryIota> = type("dictionary",DictionaryIota.TYPE)
    private fun <U : Iota, T : IotaType<U>> type(@Suppress("SameParameterValue", "SameParameterValue") name: String, type: T): T {
        val old = TYPES.put(ResourceLocation(HexTweaks.MOD_ID, name), type)
        require(old == null) { "Typo? Duplicate id $name" }
        return type
    }
}
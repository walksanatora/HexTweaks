@file:Suppress("UNCHECKED_CAST")

package net.walksantor.hextweaks.casting

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.xplat.IXplatAbstractions
import dev.architectury.registry.registries.DeferredRegister
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.walksantor.hextweaks.HexTweaks
import net.walksantor.hextweaks.casting.iota.ByteArrayIota
import net.walksantor.hextweaks.casting.iota.ByteIota
import net.walksantor.hextweaks.casting.iota.RitualIota

object HexTweaksIotaTypes {
    val IOTATYPE = DeferredRegister.create(
        HexTweaks.MOD_ID,
        IXplatAbstractions.INSTANCE.iotaTypeRegistry.key() as ResourceKey<Registry<IotaType<*>>>)

    val BYTEARRAY = type("bytearray",ByteArrayIota.ByteArrayIotaType())
    val BYTE = type("byte",ByteIota.ByteIotaType())
    val RITUAL = type("ritual",RitualIota.RitualIotaType())

    private fun <U : Iota?, T : IotaType<U>?> type(name: String, type: T): T {
        IOTATYPE.register(
            ResourceLocation(
                HexTweaks.MOD_ID,
                name
            )
        ) { type }
        return type
    }

    fun register() {
        IOTATYPE.register()
    }
}
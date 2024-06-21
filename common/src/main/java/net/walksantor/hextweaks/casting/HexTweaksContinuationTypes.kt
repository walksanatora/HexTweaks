package net.walksantor.hextweaks.casting

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.api.casting.eval.vm.ContinuationFrame
import at.petrak.hexcasting.common.lib.HexRegistries
import dev.architectury.registry.registries.DeferredRegister
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.walksantor.hextweaks.HexTweaks
import net.walksantor.hextweaks.HexTweaksRegistry
import net.walksantor.hextweaks.casting.continuation.ContinuationWhile
import java.util.LinkedHashMap

object HexTweaksContinuationTypes {
    val CONTINUATION_REGISTRY = DeferredRegister.create(HexTweaks.MOD_ID, HexRegistries.CONTINUATION_TYPE)
    private val CONTINUATIONS: LinkedHashMap<ResourceLocation, ContinuationFrame.Type<*>> = LinkedHashMap()

    var WHILE = continuation("while",ContinuationWhile.WhileType)

    private fun continuation(name: String, continuation: ContinuationFrame.Type<*>): ContinuationFrame.Type<*> {
        val old = CONTINUATIONS.put(HexAPI.modLoc(name), continuation)
        require(old == null) { "Typo? Duplicate id $name" }
        return continuation
    }

    fun init() {
        for (entry in CONTINUATIONS) {
            CONTINUATION_REGISTRY.register(entry.key) { entry.value }
        }
        HexTweaksRegistry.regMap[HexRegistries.CONTINUATION_TYPE as ResourceKey<Registry<*>>] = CONTINUATION_REGISTRY
    }
}
package net.walksantor.hextweaks.casting

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.api.casting.eval.vm.ContinuationFrame
import at.petrak.hexcasting.xplat.IXplatAbstractions
import dev.architectury.registry.registries.DeferredRegister
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.walksantor.hextweaks.HexTweaks
import net.walksantor.hextweaks.casting.continuation.ContinuationWhile
import java.util.HashMap
import java.util.LinkedHashMap

object HexTweaksContinuationTypes {
    val CONTINUATION_REGISTRY = DeferredRegister.create("hextweaks",IXplatAbstractions.INSTANCE.continuationTypeRegistry.key() as ResourceKey<Registry<ContinuationFrame.Type<*>>>)
    private val CONTINUATIONS: LinkedHashMap<ResourceLocation, ContinuationFrame.Type<*>> = LinkedHashMap()

    var WHILE = continuation("while",ContinuationWhile.WhileType)

    private fun continuation(name: String, continuation: ContinuationFrame.Type<*>): ContinuationFrame.Type<*> {
        val old = CONTINUATIONS.put(HexAPI.modLoc(name), continuation)
        require(old == null) { "Typo? Duplicate id $name" }
        return continuation
    }

    fun register() {
        for (entry in CONTINUATIONS) {
            CONTINUATION_REGISTRY.register(entry.key) { entry.value }
        }
    }
}
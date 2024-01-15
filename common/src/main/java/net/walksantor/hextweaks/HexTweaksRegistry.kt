package net.walksantor.hextweaks

import dan200.computercraft.api.pocket.PocketUpgradeSerialiser
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
import dev.architectury.registry.registries.DeferredRegister
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.walksantor.hextweaks.computer.WandPocketUpgrade
import net.walksantor.hextweaks.computer.WandTurtleUpgrade


object HexTweaksRegistry {
    val POCKET_SERIALS = DeferredRegister.create(HexTweaks.MOD_ID,PocketUpgradeSerialiser.registryId())
    val TURTLE_SERIALS = DeferredRegister.create(HexTweaks.MOD_ID,TurtleUpgradeSerialiser.registryId())

    val WAND_POCKET = POCKET_SERIALS.register(ResourceLocation(HexTweaks.MOD_ID,"wand")) { WandPocketUpgrade.UpgradeSerialiser() }
    val WAND_TURTLE = TURTLE_SERIALS.register(ResourceLocation(HexTweaks.MOD_ID,"wand")) { WandTurtleUpgrade.UpgradeSerialiser() }

    fun register() {
        POCKET_SERIALS.register()
        TURTLE_SERIALS.register()
    }
}
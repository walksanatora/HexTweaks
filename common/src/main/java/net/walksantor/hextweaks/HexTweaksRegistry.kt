package net.walksantor.hextweaks

import at.petrak.hexcasting.api.casting.castables.SpecialHandler
import at.petrak.hexcasting.xplat.IXplatAbstractions
import dan200.computercraft.api.client.ComputerCraftAPIClient
import dan200.computercraft.api.client.turtle.TurtleUpgradeModeller
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
import dev.architectury.registry.registries.DeferredRegister
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.damagesource.*
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour.Properties
import net.walksantor.hextweaks.blocks.ConjuredButton
import net.walksantor.hextweaks.casting.handler.GrandSpellHandler
import net.walksantor.hextweaks.computer.WandPocketUpgrade
import net.walksantor.hextweaks.computer.WandTurtleUpgrade


object HexTweaksRegistry {
    var REGISTERED = false;

    val POCKET_SERIALS = DeferredRegister.create(HexTweaks.MOD_ID,PocketUpgradeSerialiser.registryId())
    val TURTLE_SERIALS = DeferredRegister.create(HexTweaks.MOD_ID,TurtleUpgradeSerialiser.registryId())
    val BLOCKS = DeferredRegister.create(HexTweaks.MOD_ID,Registries.BLOCK)
    val ITEMS = DeferredRegister.create(HexTweaks.MOD_ID,Registries.ITEM)
    val SPECIAL_HANDLERS = DeferredRegister.create(HexTweaks.MOD_ID,IXplatAbstractions.INSTANCE.specialHandlerRegistry.key() as ResourceKey<Registry<SpecialHandler.Factory<*>>>)

    val WAND_POCKET = POCKET_SERIALS.register(ResourceLocation(HexTweaks.MOD_ID,"wand")) { WandPocketUpgrade.UpgradeSerialiser() }
    val WAND_TURTLE = TURTLE_SERIALS.register(ResourceLocation(HexTweaks.MOD_ID,"wand")) { WandTurtleUpgrade.UpgradeSerializer() }

    val CONJURED_BUTTON = BLOCKS.register(ResourceLocation(HexTweaks.MOD_ID,"conjbutton")) { ConjuredButton(Properties.copy(Blocks.STONE_BUTTON)) }
    val CONJURED_BUTTON_ITEM = ITEMS.register(ResourceLocation(HexTweaks.MOD_ID,"conjbutton")) { BlockItem(
        CONJURED_BUTTON.get(), Item.Properties())
    }

    val GRAND_HANDLER = SPECIAL_HANDLERS.register(ResourceLocation(HexTweaks.MOD_ID,"grand")) { GrandSpellHandler.Factory() }

    val SUS_DAMMAGET = DamageType("hextweaks.death.sus",
        DamageScaling.ALWAYS, 100.0F,
        DamageEffects.FREEZING,
        DeathMessageType.INTENTIONAL_GAME_DESIGN)
    val SUS_DAMMAGE = DamageSource(Holder.direct(SUS_DAMMAGET))

    fun register() {
        if (REGISTERED) {return}
        POCKET_SERIALS.register()
        TURTLE_SERIALS.register()
        BLOCKS.register()
        ITEMS.register()
        SPECIAL_HANDLERS.register()
        REGISTERED = true
    }

    fun model() {
        //we gotta do this later on forge...
        ComputerCraftAPIClient.registerTurtleUpgradeModeller(
            WAND_TURTLE.get(),
            TurtleUpgradeModeller.flatItem()
        )
    }
}
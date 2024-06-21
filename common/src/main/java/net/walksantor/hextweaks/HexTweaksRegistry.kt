package net.walksantor.hextweaks

import at.petrak.hexcasting.common.lib.HexRegistries
import dan200.computercraft.api.client.ComputerCraftAPIClient
import dan200.computercraft.api.client.turtle.TurtleUpgradeModeller
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
import dev.architectury.platform.Platform
import dev.architectury.registry.client.level.entity.EntityRendererRegistry
import dev.architectury.registry.level.entity.EntityAttributeRegistry
import dev.architectury.registry.registries.DeferredRegister
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour.Properties
import net.walksantor.hextweaks.blocks.ConjuredButton
import net.walksantor.hextweaks.casting.HexTweaksContinuationTypes
import net.walksantor.hextweaks.casting.HexTweaksIotaTypes
import net.walksantor.hextweaks.casting.MindflayRegistry
import net.walksantor.hextweaks.casting.PatternRegistry
import net.walksantor.hextweaks.casting.handler.GrandSpellHandler
import net.walksantor.hextweaks.computer.WandPocketUpgrade
import net.walksantor.hextweaks.computer.WandTurtleUpgrade
import net.walksantor.hextweaks.entities.SpellBeaconEntity
import net.walksantor.hextweaks.entities.SpellBeaconEntityRender
import net.walksantor.hextweaks.items.VirtualPigment


object HexTweaksRegistry {
    var REGISTERED = false
    val regMap: HashMap<ResourceKey<Registry<*>>,DeferredRegister<*>> = HashMap();

    // vanilla registries
    val BLOCKS = reg(Registries.BLOCK)
    val ITEMS = reg(Registries.ITEM)
    val ENTITY_TYPES = reg(Registries.ENTITY_TYPE)

    val SPECIAL_HANDLERS = reg(HexRegistries.SPECIAL_HANDLER)
    val ACTIONS = reg(HexRegistries.ACTION)
    val POCKET_SERIALS = reg(PocketUpgradeSerialiser.registryId())
    val TURTLE_SERIALS = reg(TurtleUpgradeSerialiser.registryId())

    val WAND_POCKET = POCKET_SERIALS.register(ResourceLocation(HexTweaks.MOD_ID,"wand")) { WandPocketUpgrade.UpgradeSerialiser() }
    val WAND_TURTLE = TURTLE_SERIALS.register(ResourceLocation(HexTweaks.MOD_ID,"wand")) { WandTurtleUpgrade.UpgradeSerializer() }

    val RGB_PIGMENT = ITEMS.register(ResourceLocation(HexTweaks.MOD_ID,"rgb_pigment")) {
        VirtualPigment(Item.Properties().stacksTo(-1));
    }

    val GRAND_HANDLER = SPECIAL_HANDLERS.register(ResourceLocation(HexTweaks.MOD_ID,"grand")) { GrandSpellHandler.Factory() }

    val SUS_DAMMAGET = DamageType("hextweaks.death.sus",0.0f)
    val SUS_DAMMAGE = DamageSource(Holder.direct(SUS_DAMMAGET))

    val SPELL_BEACON_ENTITY = ENTITY_TYPES.register(ResourceLocation(HexTweaks.MOD_ID,"sbe")) {
        SpellBeaconEntity.BUILDER.build("sbe")
    }

    fun init() {
        if (Platform.isForge()) {
            EntityAttributeRegistry.register({ SPELL_BEACON_ENTITY.get()}, {SpellBeaconEntity.createAttributes()})
        }
        HexTweaksIotaTypes.init()
        MindflayRegistry.register()
        HexTweaksContinuationTypes.init()
    }

    fun register(key: ResourceKey<Registry<*>>?) {
        if (!REGISTERED) {
            PatternRegistry.register { are, rl -> ACTIONS.register(rl) { are } }
            REGISTERED = true
        }
        if (key == null) {
            POCKET_SERIALS.register()
            TURTLE_SERIALS.register()
            BLOCKS.register()
            ITEMS.register()
            SPECIAL_HANDLERS.register()
            HexTweaksIotaTypes.IOTATYPE.register()
            ENTITY_TYPES.register()
            EntityAttributeRegistry.register({ SPELL_BEACON_ENTITY.get()}, {SpellBeaconEntity.createAttributes()})
            ACTIONS.register()
//            MindflayRegistry.register()
            HexTweaksContinuationTypes.CONTINUATION_REGISTRY.register()
        } else {
            val reg3 = regMap[key]
            if (reg3 != null) {
                reg3.register()
            } else {
                HexTweaks.LOGGER.warn("Registry type {} does not exists in regMap",key)
            }
        }
    }

    fun model() {
        //client init on forge...
        ComputerCraftAPIClient.registerTurtleUpgradeModeller(
            WAND_TURTLE.get(),
            TurtleUpgradeModeller.flatItem()
        )
        EntityRendererRegistry.register({ SPELL_BEACON_ENTITY.get()}, { ctx -> SpellBeaconEntityRender(ctx) })
    }

    fun <T> reg(key: ResourceKey<Registry<T>>): DeferredRegister<T> {
        val reg2 = DeferredRegister.create(HexTweaks.MOD_ID,key)
        regMap[key as ResourceKey<Registry<*>>] = reg2
        return reg2
    }
}
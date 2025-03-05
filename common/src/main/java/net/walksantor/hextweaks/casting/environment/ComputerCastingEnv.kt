package net.walksantor.hextweaks.casting.environment

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.api.addldata.ADMediaHolder
import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.PatternShapeMatch
import at.petrak.hexcasting.api.casting.eval.CastResult
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.MishapEnvironment
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect
import at.petrak.hexcasting.api.casting.mishaps.MishapDisallowedSpell
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.api.utils.compareMediaItem
import at.petrak.hexcasting.api.utils.otherHand
import at.petrak.hexcasting.api.utils.putInt
import at.petrak.hexcasting.common.lib.HexItems
import com.thunderbear06.computer.peripherals.DummyPocket
import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.api.pocket.IPocketAccess
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.TurtleSide
import dan200.computercraft.shared.computer.core.ComputerFamily
import dan200.computercraft.shared.computer.core.ServerComputer
import dan200.computercraft.shared.turtle.core.TurtleBrain
import dev.architectury.platform.Platform
import io.sc3.plethora.gameplay.neural.NeuralPocketAccess
import com.thunderbear06.entity.android.BaseAndroidEntity
import net.minecraft.Util.NIL_UUID
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.Container
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.GameType
import net.minecraft.world.phys.Vec3
import net.walksantor.hextweaks.HexTweaks
import net.walksantor.hextweaks.HexTweaksRegistry
import net.walksantor.hextweaks.mixin.NeuralAccessor
import java.util.function.Predicate

class ComputerCastingEnv(val turtleData: Pair<ITurtleAccess, TurtleSide>?, val pocketData: IPocketAccess?,level: ServerLevel,val computer: IComputerAccess) : CastingEnvironment(level) {

    constructor(old: ComputerCastingEnv, newWorld: ServerLevel) : this(old.turtleData,old.pocketData,newWorld,old.computer)

    private val mishap = run {
        if (turtleData == null) {
            if (pocketData!!.entity is ServerPlayer) {
                ComputerMishapEnvironment(world,pocketData.entity as ServerPlayer,this)
            } else {
                ComputerMishapEnvironment(world, null, this)
            }
        }
        ComputerMishapEnvironment(world,null,this)
    }

    override fun getCastingEntity(): LivingEntity? {
        if (pocketData != null) {
            if (pocketData.entity is LivingEntity) {
                return pocketData.entity as LivingEntity
            }
        }
        return null
    }
    override fun getMishapEnvironment(): MishapEnvironment = mishap

    override fun mishapSprayPos(): Vec3 {
        if (turtleData != null) {
            val bpos = turtleData.first.position
            return bpos.center
        } else {
            return castingEntity!!.position()
        }
    }

    private fun getInventory(): Container {
        return if (turtleData != null) {
          turtleData.first.inventory
        } else {
            val ent = pocketData!!.entity
            if (ent is ServerPlayer) {
                ent.inventory
            } else if (Platform.isModLoaded("cc-androids") && ent is BaseAndroidEntity) {
                ent.inventory
            } else {
                throw IllegalStateException("Pocket Computer not on a entity with a supported inventory wah wah")
            }
        }
    }

    override fun extractMediaEnvironment(cost: Long, simulate: Boolean): Long {
        @Suppress("NAME_SHADOWING") var cost = (cost * HexTweaks.getCONFIG().computerCostMult).toLong()
        val inventory  = getInventory()
        val adMediaHolders: ArrayList<ADMediaHolder> = ArrayList()
        for (i in 0..inventory.containerSize) {
            val item = inventory.getItem(i)
            val media = HexAPI.instance().findMediaHolder(item)
            if (media?.canProvide() == true) {
                adMediaHolders.add(media)
            }
        }
        adMediaHolders.sortWith(::compareMediaItem)
        adMediaHolders.reverse()
        for (source in adMediaHolders) {
            val found = source.withdrawMedia(cost, simulate)
            cost -= found
            if (cost <= 0) {
                break
            }
        }
        return cost
    }

    override fun isVecInRangeEnvironment(vec: Vec3?): Boolean {
        val position: Vec3?
        if (pocketData != null) {
            val ent = pocketData.entity
            if (ent is ServerPlayer) {
                val sentinel = HexAPI.instance().getSentinel(ent)
                if ((sentinel != null && sentinel.extendsRange()) && ent.level()
                        .dimension() === sentinel.dimension() && (vec!!.distanceToSqr(sentinel.position()) <= PlayerBasedCastEnv.SENTINEL_RADIUS * PlayerBasedCastEnv.SENTINEL_RADIUS * HexTweaks.getCONFIG().computerAmbitMult)
                ) {
                    return true
                }
            }
            position = pocketData.position
        } else {
            position = turtleData!!.first.position.center
        }

        return vec!!.distanceToSqr(position!!) <= PlayerBasedCastEnv.AMBIT_RADIUS * PlayerBasedCastEnv.AMBIT_RADIUS * HexTweaks.getCONFIG().computerAmbitMult
    }

    override fun hasEditPermissionsAtEnvironment(pos: BlockPos): Boolean {
        if (pocketData != null) {
            val ent = pocketData.entity
            if (ent is ServerPlayer) {
                return ent.gameMode.gameModeForPlayer != GameType.ADVENTURE && this.world.mayInteract(
                    ent, pos
                )
            }
        } else {//it is a turtle. we are just going to give it god mode
            return true
        }
        return true
    }

    override fun getCastingHand(): InteractionHand {
        return if (turtleData != null) {
            when (turtleData.second) {
                TurtleSide.LEFT -> InteractionHand.MAIN_HAND
                TurtleSide.RIGHT -> InteractionHand.OFF_HAND
            }
        } else {
            val ent = pocketData!!.entity
            if (Platform.isModLoaded("cc-androids") && (ent is BaseAndroidEntity)) {
                val downcast = ent as LivingEntity;// loom is stupid and fails to remap the upcasted BaseAndroidEntity in the next line
                if (downcast.getItemInHand(InteractionHand.MAIN_HAND).item == HexItems.STAFF_MINDSPLICE) {
                    InteractionHand.MAIN_HAND
                } else {
                    InteractionHand.OFF_HAND
                }
            } else {
                InteractionHand.MAIN_HAND
            }
        }
    }

    override fun getUsableStacks(mode: StackDiscoveryMode?): List<ItemStack> {
        val out = ArrayList<ItemStack>()
        //TODO: check back how base hexcasting handles these modes after I deprecated caster
        val castingPlayer = this.castingEntity as? ServerPlayer
        if (castingPlayer != null) {
            when (mode) {
                StackDiscoveryMode.QUERY -> {
                    val offhand = castingPlayer.getItemInHand(otherHand(this.castingHand))
                    if (!offhand.isEmpty) {
                        out.add(offhand)
                    }


                    // If we're casting from the main hand, try to pick from the slot one to the right of the selected slot
                    // Otherwise, scan the hotbar left to right
                    val anchorSlot = if (this.castingHand == InteractionHand.MAIN_HAND
                    ) (castingPlayer.inventory.selected + 1) % 9
                    else 0


                    for (delta in 0..8) {
                        val slot = (anchorSlot + delta) % 9
                        out.add(castingPlayer.inventory.getItem(slot))
                    }
                }
                StackDiscoveryMode.EXTRACTION -> {
                    // https://wiki.vg/Inventory is WRONG
                    // slots 0-8 are the hotbar
                    // for what purpose I cannot imagine
                    // http://redditpublic.com/images/b/b2/Items_slot_number.png looks right
                    // and offhand is 150 Inventory.java:464

                    // First, the inventory backwards
                    // We use inv.items here to get the main inventory, but not offhand or armor
                    val inv = castingPlayer.inventory
                    for (i in inv.items.indices.reversed()) {
                        if (i != inv.selected) {
                            out.add(inv.items[i])
                        }
                    }


                    // then the offhand, then the selected hand
                    out.addAll(inv.offhand)
                    out.add(inv.getSelected())
                }
                else -> {}
            }
        } else {
            val inventory  = getInventory()
            for (i in 0..inventory.containerSize) {
                val item = inventory.getItem(i)
                out.add(item)
            }
        }
        return out
    }

    override fun getPrimaryStacks(): MutableList<HeldItemInfo> {
        if (pocketData != null) {
            val ent = this.castingEntity
            if (ent == null) {return mutableListOf()}
            return mutableListOf(
                 HeldItemInfo(ent.getItemInHand(this.otherHand), this.otherHand),
                 HeldItemInfo(ent.getItemInHand(this.castingHand), this.castingHand)
            )
        } else {
            val slot = turtleData!!.first.selectedSlot
            return mutableListOf(HeldItemInfo(
                turtleData.first.inventory.getItem(slot),
                InteractionHand.MAIN_HAND
            ))
        }
    }

    override fun getHeldItemToOperateOn(stackOk: Predicate<ItemStack>): HeldItemInfo? {
        if (turtleData != null) {
            val inv = turtleData.first.inventory
            val slot = turtleData.first.selectedSlot
            val item = inv.getItem(slot)
            return if (item == ItemStack.EMPTY || !stackOk.test(item)) {
                null
            } else {
                HeldItemInfo(item,InteractionHand.MAIN_HAND)
            }
        } else {
            return super.getHeldItemToOperateOn(stackOk)
        }
    }

    override fun replaceItem(stackOk: Predicate<ItemStack>?, replaceWith: ItemStack?, hand: InteractionHand?): Boolean {
        HexTweaks.LOGGER.warn("Yell at walksanator on discord. ComputerCastingEnv#replaceItem is NYI")
        return false
    }

    override fun getPigment(): FrozenPigment {
        val color = turtleData?.first?.colour ?: pocketData!!.colour
        val stack = ItemStack(HexTweaksRegistry.RGB_PIGMENT.get(),0)
        stack.putInt("rgb",color)
        return FrozenPigment(stack, NIL_UUID)
    }

    override fun setPigment(pigment: FrozenPigment?): FrozenPigment {
        if (pigment != null) {
            if (turtleData != null) {
                turtleData.first.colour = pigment.colorProvider.getColor(0f,Vec3.ZERO) and 0x00ffffff
            } else {
                pocketData!!.colour = pigment.colorProvider.getColor(0f,Vec3.ZERO) and 0x00ffffff
            }
        }
        val color = turtleData?.first?.colour ?: pocketData!!.colour
        val stack = ItemStack(HexTweaksRegistry.RGB_PIGMENT.get(),0)
        stack.putInt("rgb",color)
        return FrozenPigment(stack,NIL_UUID)
    }

    override fun produceParticles(particles: ParticleSpray?, colorizer: FrozenPigment?) {
        particles!!.sprayParticles(this.world, pigment)
    }

    override fun isEnlightened(): Boolean {
        val family = getServerComputer().family
        return when (family) {
            ComputerFamily.NORMAL -> false
            else -> true
        }
    }

    override fun isCreativeMode(): Boolean = getServerComputer().family == ComputerFamily.COMMAND

    override fun printMessage(message: Component) {
        computer.queueEvent("reveal",
            computer.attachmentName,
            message.string
        )
    }

    private fun getServerComputer(): ServerComputer {
        return if (pocketData != null) {
            if (Platform.isModLoaded("plethora") && (pocketData is NeuralPocketAccess)) {
                (pocketData as NeuralAccessor).neural as ServerComputer as ServerComputer
            } else if (Platform.isModLoaded("cc-androids") && pocketData is DummyPocket) {
                (pocketData.entity!! as BaseAndroidEntity).computer.serverComputer as ServerComputer
            } else {
                pocketData as ServerComputer
            }
        } else {
            (turtleData!!.first as TurtleBrain).owner.serverComputer!!
        }
    }


    override fun postExecution(result: CastResult?) {
        super.postExecution(result)

        for (sideEffect in result!!.sideEffects) {
            if (sideEffect is OperatorSideEffect.DoMishap) {
                val msg = sideEffect.mishap.errorMessageWithName(this, sideEffect.errorCtx)
                if (msg != null) {
                    computer.queueEvent(
                        "mishap",
                        computer.attachmentName,
                        sideEffect.mishap.javaClass.name,
                        msg,
                        sideEffect.errorCtx.pattern.toString()
                    )
                }
            }
        }
    }

    override fun precheckAction(match: PatternShapeMatch?) {
        super.precheckAction(match)

        // TODO: this doesn't let you select special handlers.
        // Might be worth making a "no casting" tag on each thing
        val key = actionKey(match)
        if (!HexTweaks.getCONFIG().isPatternAllowed(key)) {
            throw MishapDisallowedSpell()
        }
    }
}
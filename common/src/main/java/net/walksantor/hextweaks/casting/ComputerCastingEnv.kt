package net.walksantor.hextweaks.casting

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.api.addldata.ADMediaHolder
import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.MishapEnvironment
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.api.utils.compareMediaItem
import at.petrak.hexcasting.api.utils.extractMedia
import at.petrak.hexcasting.api.utils.otherHand
import dan200.computercraft.api.pocket.IPocketAccess
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.TurtleSide
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.Container
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.GameType
import net.minecraft.world.phys.Vec3
import java.util.function.Predicate

class ComputerCastingEnv(private val turtleData: Pair<ITurtleAccess, TurtleSide>?, private val pocketData: IPocketAccess?,var level: ServerLevel) : CastingEnvironment(level) {
    private val mishap = run {
        if (turtleData == null) {
            if (pocketData!!.entity is ServerPlayer) {
                ComputerMishapEnvironment(world,pocketData.entity as ServerPlayer)
            }
        }
        ComputerMishapEnvironment(world,null)
    }

    override fun getCaster(): ServerPlayer? {
        if (pocketData != null) {
            if (pocketData.entity is ServerPlayer) {
                return pocketData.entity as ServerPlayer
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
            return caster!!.position()
        }
    }

    private fun getInventory(): Container {
        return if (turtleData != null) {
          turtleData.first.inventory
        } else {
            (pocketData!!.entity!! as ServerPlayer).inventory
        }
    }

    override fun extractMediaEnvironment(cost: Long): Long {
        @Suppress("NAME_SHADOWING") var cost = cost
        val inventory  = getInventory()
        val adMediaHolders: ArrayList<ADMediaHolder> = ArrayList()
        for (i in 0..inventory.containerSize) {
            val item = inventory.getItem(i)
            val media = HexAPI.instance().findMediaHolder(item)
            if (media != null) {
                adMediaHolders.add(media)
            }
        }
        adMediaHolders.sortWith(::compareMediaItem)
        adMediaHolders.reverse()
        for (source in adMediaHolders) {
            val found = extractMedia(source, cost, drainForBatteries = false, simulate = false)
            cost -= found
            if (cost <= 0) {
                break
            }
        }

        return cost
    }

    override fun isVecInRangeEnvironment(vec: Vec3?): Boolean {
        if (pocketData != null) {
            if (pocketData.entity is ServerPlayer) {
                val sentinel = HexAPI.instance().getSentinel(this.caster)
                if ((sentinel != null && sentinel.extendsRange()) && this.caster!!.level()
                        .dimension() === sentinel.dimension() && (vec!!.distanceToSqr(sentinel.position()) <= PlayerBasedCastEnv.SENTINEL_RADIUS * PlayerBasedCastEnv.SENTINEL_RADIUS)
                ) {
                    return true
                }
            }
        }

        return vec!!.distanceToSqr(this.caster!!.position()) <= PlayerBasedCastEnv.AMBIT_RADIUS * PlayerBasedCastEnv.AMBIT_RADIUS
    }

    override fun hasEditPermissionsAtEnvironment(pos: BlockPos): Boolean {
        if (pocketData != null) {
            if (pocketData.entity is ServerPlayer) {
                return this.caster!!.gameMode.gameModeForPlayer != GameType.ADVENTURE && this.world.mayInteract(
                    this.caster!!, pos
                )
            }
        } else {//it is a turtle. we are just going to give it god mode
            return true
        }
        return true
    }

    override fun getCastingHand(): InteractionHand {
        if (turtleData != null) {
            when (turtleData.second) {
                TurtleSide.LEFT -> InteractionHand.MAIN_HAND
                TurtleSide.RIGHT -> InteractionHand.OFF_HAND
            }
        }
        return InteractionHand.MAIN_HAND
    }

    override fun getUsableStacks(mode: StackDiscoveryMode?): List<ItemStack> {
        val out = java.util.ArrayList<ItemStack>()
        if (this.caster != null) {
            when (mode) {
                StackDiscoveryMode.QUERY -> {
                    val offhand = this.caster!!.getItemInHand(otherHand(this.castingHand))
                    if (!offhand.isEmpty) {
                        out.add(offhand)
                    }


                    // If we're casting from the main hand, try to pick from the slot one to the right of the selected slot
                    // Otherwise, scan the hotbar left to right
                    val anchorSlot = if (this.castingHand == InteractionHand.MAIN_HAND
                    ) (this.caster!!.inventory.selected + 1) % 9
                    else 0


                    for (delta in 0..8) {
                        val slot = (anchorSlot + delta) % 9
                        out.add(this.caster!!.inventory.getItem(slot))
                    }
                }
                StackDiscoveryMode.EXTRACTION -> {
                    // https://wiki.vg/Inventory is WRONG
                    // slots 0-8 are the hotbar
                    // for what purpose I cannot imagine
                    // http://redditpublic.com/images/b/b2/Items_slot_number.png looks right
                    // and offhand is 150 Inventory.java:464
                    val out = java.util.ArrayList<ItemStack>()


                    // First, the inventory backwards
                    // We use inv.items here to get the main inventory, but not offhand or armor
                    val inv = this.caster!!.inventory
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
        return mutableListOf()
    }

    override fun replaceItem(stackOk: Predicate<ItemStack>?, replaceWith: ItemStack?, hand: InteractionHand?): Boolean {
        TODO("Not yet implemented")
    }

    override fun getPigment(): FrozenPigment {
        TODO("Not yet implemented")
    }

    override fun setPigment(pigment: FrozenPigment?): FrozenPigment? {
        TODO("Not yet implemented")
    }

    override fun produceParticles(particles: ParticleSpray?, colorizer: FrozenPigment?) {
        TODO("Not yet implemented")
    }

    override fun printMessage(message: Component?) {
        TODO("Not yet implemented")
    }
}
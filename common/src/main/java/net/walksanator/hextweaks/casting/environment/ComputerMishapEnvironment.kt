package net.walksanator.hextweaks.casting.environment

import at.petrak.hexcasting.api.casting.eval.MishapEnvironment
import dan200.computercraft.shared.turtle.core.InteractDirection
import dan200.computercraft.shared.turtle.core.TurtleDropCommand
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.Vec3
import net.walksanator.hextweaks.HexTweaks

class ComputerMishapEnvironment(world: ServerLevel, player: ServerPlayer?,val env: net.walksanator.hextweaks.casting.environment.ComputerCastingEnv) : MishapEnvironment(world,player) {
    override fun yeetHeldItemsTowards(targetPos: Vec3?) {
        if (env.turtleData != null) {
            val slot = env.turtleData.first.selectedSlot
            val item = env.turtleData.first.inventory.getItem(slot)
            env.turtleData.first.inventory.setItem(slot, ItemStack.EMPTY)
            val pos = env.turtleData.first.position.center
            val delta = targetPos!!.subtract(pos).normalize().scale(0.5)
            yeetItem(item,pos,delta)
        } else {
            val pos = env.caster!!.position()
            val delta = targetPos!!.subtract(pos).normalize().scale(0.5)

            for (hand in InteractionHand.entries) {
                val stack = env.caster!!.getItemInHand(hand)
                env.caster!!.setItemInHand(hand, ItemStack.EMPTY)
                this.yeetItem(stack, pos, delta)
            }
        }
    }

    override fun dropHeldItems() {
        if (env.turtleData != null) {
            env.turtleData.first.executeCommand(
                TurtleDropCommand(InteractDirection.FORWARD,64)
            )
        } else {
            this.yeetHeldItemsTowards(env.caster!!.position().add(env.caster!!.lookAngle ))
        }
    }

    override fun drown() {
        net.walksanator.hextweaks.HexTweaks.LOGGER.warn("Yell at walksanator on discord. ComputerMishapEnvironment#drown is NYI")
    }

    override fun damage(healthProportion: Float) {
        net.walksanator.hextweaks.HexTweaks.LOGGER.warn("Yell at walksanator on discord. ComputerMishapEnvironment#dammage is NYI")
    }

    override fun removeXp(amount: Int) {
        net.walksanator.hextweaks.HexTweaks.LOGGER.warn("Yell at walksanator on discord. ComputerMishapEnvironment#removeXp is NYI")
    }

    override fun blind(ticks: Int) {
        net.walksanator.hextweaks.HexTweaks.LOGGER.warn("Yell at walksanator on discord. ComputerMishapEnvironment#blind is NYI")
    }
}
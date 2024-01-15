package net.walksantor.hextweaks.casting

import at.petrak.hexcasting.api.casting.eval.MishapEnvironment
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.phys.Vec3

class ComputerMishapEnvironment(world: ServerLevel, player: ServerPlayer?) : MishapEnvironment(world,player) {
    override fun yeetHeldItemsTowards(targetPos: Vec3?) {
        TODO("Not yet implemented")
    }

    override fun dropHeldItems() {
        TODO("Not yet implemented")
    }

    override fun drown() {
        TODO("Not yet implemented")
    }

    override fun damage(healthProportion: Float) {
        TODO("Not yet implemented")
    }

    override fun removeXp(amount: Int) {
        TODO("Not yet implemented")
    }

    override fun blind(ticks: Int) {
        TODO("Not yet implemented")
    }
}
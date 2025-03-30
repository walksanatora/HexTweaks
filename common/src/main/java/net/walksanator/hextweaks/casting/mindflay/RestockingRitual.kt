package net.walksanator.hextweaks.casting.mindflay

import at.petrak.hexcasting.api.casting.iota.EntityIota
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.npc.Villager
import net.walksantor.hextweaks.casting.MindflayRegistry

object RestockingRitual {
    fun restockVillager(input: MindflayInput): net.walksanator.hextweaks.casting.mindflay.MindflayResult {
        if (input.target !is EntityIota) return net.walksanator.hextweaks.casting.mindflay.MindflayResult(false)
        val target = input.target.entity
        if (target !is Villager)
            return net.walksanator.hextweaks.casting.mindflay.MindflayResult(false) // the target is not a villager

        val points = MindflayRegistry.calcuateVillagerPoints(input.inputs)
        if (points < target.offers.size)
            return net.walksanator.hextweaks.casting.mindflay.MindflayResult(false) //we dont have enough points

        target.restock()
        MindflayRegistry.performBrainsweeps(input.inputs, input.env.castingEntity as? ServerPlayer)
        return net.walksanator.hextweaks.casting.mindflay.MindflayResult(true)//we restocked
    }

}
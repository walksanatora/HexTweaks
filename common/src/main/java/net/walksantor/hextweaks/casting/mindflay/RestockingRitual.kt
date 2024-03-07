package net.walksantor.hextweaks.casting.mindflay

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.npc.Villager
import net.walksantor.hextweaks.casting.MindflayRegistry

object RestockingRitual {
    fun restockVillager(input: MindflayInput): MindflayResult {
        if (input.target.left().isEmpty)
            return MindflayResult(false) // the target is not a entity
        val target = input.target.left().get().entity
        if (target !is Villager)
            return MindflayResult(false) // the target is not a villager

        val points = MindflayRegistry.calcuateVillagerPoints(input.inputs)
        if (points < target.offers.size)
            return MindflayResult(false) //we dont have enough points

        target.restock()
        MindflayRegistry.performBrainsweeps(input.inputs, input.env.castingEntity as? ServerPlayer)
        return MindflayResult(true)//we restocked
    }

}
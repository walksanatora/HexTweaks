package net.walksantor.hextweaks.casting.mindflay

import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.MobSpawnType
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.entity.npc.VillagerProfession
import net.walksantor.hextweaks.HexTweaks
import net.walksantor.hextweaks.HexTweaksRegistry
import net.walksantor.hextweaks.casting.MindflayRegistry
import net.walksantor.hextweaks.casting.rituals.ResetChunkRitual

object StartResetChunkRitual {
    fun start(input: MindflayInput): MindflayResult {
        if (input.target !is Vec3Iota) return MindflayResult(false) // target is not a position
        val filtered = input.inputs.filterIsInstance<Villager>().filter {it.villagerData.profession == VillagerProfession.MASON }.size
        if (filtered != input.inputs.size) return MindflayResult(false) // the inputs are not *only* masons
        if (MindflayRegistry.calcuateVillagerPoints(input.inputs) < 32) return MindflayResult(false) // insufficent points
        MindflayRegistry.performBrainsweeps(input.inputs,input.env.castingEntity as? ServerPlayer)

        val v3 = input.target.vec3
        val pos = BlockPos(v3.x.toInt(), v3.y.toInt(), v3.z.toInt())

        val spawned = HexTweaksRegistry.SPELL_BEACON_ENTITY.get().spawn(input.env.world,pos,MobSpawnType.TRIGGERED)
            ?: return MindflayResult(false)

        spawned.ritual = ResetChunkRitual(spawned.boss_event, CompoundTag())
        spawned.owner = input.env.castingEntity?.uuid

        input.env.world.server.playerList.broadcastSystemMessage(Component.translatable("hextweaks.ritual.reset.start"),false)

        return MindflayResult(true)
    }

}
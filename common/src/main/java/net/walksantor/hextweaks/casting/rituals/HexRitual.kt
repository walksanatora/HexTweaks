package net.walksantor.hextweaks.casting.rituals

import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.BossEvent
import net.minecraft.world.level.Level

abstract class HexRitual(val level: Level, val bossBar: BossEvent) {
    var steps_completed = 0

    abstract fun getMediaForStep(): Long
    abstract fun getTimeForStep(): Int
    abstract fun ritualFinished()
    abstract fun getLocation(): ResourceLocation


    fun stepFinished() {
        steps_completed++
    }
    fun tick() {/* nop */}


    fun saveState(): CompoundTag = CompoundTag()

}
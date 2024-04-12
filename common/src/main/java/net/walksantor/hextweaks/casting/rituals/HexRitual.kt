package net.walksantor.hextweaks.casting.rituals

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.Level

abstract class HexRitual(val level: Level) {
    var steps_completed = 0

    abstract fun getMediaForStep()
    abstract fun getTimeForStep()
    abstract fun getTotalSteps()

    fun stepFinished() {
        steps_completed++
    }
    abstract fun ritualFinished()
    fun load_state(tag: CompoundTag) {}
    fun save_state(): CompoundTag = CompoundTag()
}
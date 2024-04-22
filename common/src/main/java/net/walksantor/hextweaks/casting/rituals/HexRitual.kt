package net.walksantor.hextweaks.casting.rituals

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.utils.getInt
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.BossEvent
import net.minecraft.world.level.Level
import net.walksantor.hextweaks.entities.SpellBeaconEntity

abstract class HexRitual(private val bossBar: BossEvent, tag: CompoundTag) {
    var stepsCompleted = tag.getInt("steps",0)
        protected set
    var stepMedia = MediaConstants.QUENCHED_SHARD_UNIT
        protected set
    var stepTime = 200
        protected set

    open val totalSteps = 10 //override this to change it
    open val countdown = 300


    abstract fun ritualFinished(sbe: SpellBeaconEntity)
    abstract fun getLocation(): ResourceLocation


    open fun stepFinished() {
        stepsCompleted++
    }
    open fun tick(timeLeft: Int, mediaLeft: Long) {
        bossBar.progress = (stepsCompleted.toFloat() / totalSteps.toFloat())
        bossBar.color = if (timeLeft > 0) {
            if (mediaLeft > 0) {
                BossEvent.BossBarColor.WHITE
            } else {
                BossEvent.BossBarColor.BLUE
            }
        } else {
            if (mediaLeft > 0) {
                BossEvent.BossBarColor.PURPLE
            } else {
                BossEvent.BossBarColor.RED
            }
        }
    }

    open fun saveState(tag: CompoundTag) {
        tag.putInt("steps",stepsCompleted)
    }
}
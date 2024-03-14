package net.walksantor.hextweaks.casting.iota

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.walksantor.hextweaks.casting.HexTweaksIotaTypes

class RitualIota(val ritualid: ResourceLocation) : Iota(HexTweaksIotaTypes.RITUAL, ritualid) {

    class RitualIotaType : IotaType<RitualIota>() {
        override fun deserialize(tag: Tag?, world: ServerLevel?): RitualIota = RitualIota(
            ResourceLocation((tag as CompoundTag).getString("namespace"),tag.getString("path"))
        )

        override fun display(tag: Tag?): Component = Component.translatable("hextweaks.iota.ritual")

        override fun color(): Int = 0xFF0000 // literally ChatGPT suggested color

    }

    override fun isTruthy(): Boolean = true

    override fun toleratesOther(that: Iota?): Boolean {
        if (that is RitualIota) {
            return that.ritualid == ritualid
        }
        return false
    }

    override fun serialize(): Tag {
        val tag = CompoundTag()
        tag.putString("namespace",ritualid.namespace)
        tag.putString("path",ritualid.path)
        return tag
    }
}
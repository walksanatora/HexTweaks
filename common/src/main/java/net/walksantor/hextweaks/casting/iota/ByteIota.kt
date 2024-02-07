package net.walksantor.hextweaks.casting.iota

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import net.minecraft.nbt.ByteTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel

class ByteIota(payload: Byte, type: IotaType<ByteIota>) : Iota(type, payload) {
    override fun isTruthy(): Boolean = (payload as Byte) != Byte.MIN_VALUE

    override fun toleratesOther(that: Iota?): Boolean {
        if (that?.type == type) {
            return (that as ByteIota).payload == payload
        }
        return false
    }

    override fun serialize(): Tag = ByteTag.valueOf(payload as Byte)

    class ByteIotaType : IotaType<ByteIota>() {
        override fun deserialize(tag: Tag?, world: ServerLevel?): ByteIota = ByteIota((tag as ByteTag).asByte,this)

        override fun display(tag: Tag?): Component = Component.translatable("hextweaks.iota.byte")

        override fun color(): Int = 0xFF000 // literally ChatGPT suggested color

    }

}
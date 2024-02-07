package net.walksantor.hextweaks.casting.iota

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import net.minecraft.nbt.ByteArrayTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel

class ByteArrayIota(payload: ByteArray, type: IotaType<ByteArrayIota>) : Iota(type, payload) {
    override fun isTruthy(): Boolean = (payload as ByteArray).isNotEmpty()

    override fun toleratesOther(that: Iota?): Boolean {
        if (that?.type == type) {
            return (that as ByteArrayIota).payload == payload
        }
        return false
    }

    override fun serialize(): Tag = ByteArrayTag(payload as ByteArray)

    class ByteArrayIotaType : IotaType<ByteArrayIota>() {
        override fun deserialize(tag: Tag?, world: ServerLevel?): ByteArrayIota? = ByteArrayIota((tag as ByteArrayTag).asByteArray,this)

        override fun display(tag: Tag?): Component = Component.translatable("hextweaks.iota.bytearray")

        override fun color(): Int = 0xFFFF00 // literally ChatGPT suggested color

    }

}
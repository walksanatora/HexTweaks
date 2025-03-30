package net.walksanator.hextweaks.computer

import at.petrak.hexcasting.common.lib.HexItems
import com.google.gson.JsonObject
import dan200.computercraft.api.peripheral.IPeripheral
import dan200.computercraft.api.pocket.IPocketAccess
import dan200.computercraft.api.turtle.AbstractTurtleUpgrade
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.TurtleSide
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
import dan200.computercraft.api.turtle.TurtleUpgradeType
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.GsonHelper
import net.minecraft.world.item.ItemStack
import net.walksanator.hextweaks.HexTweaks

class WandTurtleUpgrade(val item:ItemStack) : AbstractTurtleUpgrade(
    ResourceLocation(net.walksanator.hextweaks.HexTweaks.MOD_ID,"wand"),
    TurtleUpgradeType.PERIPHERAL,
    "Magical",
    item
) {
    override fun update(access: ITurtleAccess, side: TurtleSide) {
        val peripheral = access.getPeripheral(side)
        if (peripheral is WandPeripheral) {
            if (!peripheral.isInit) {return}
            peripheral.vm.image = peripheral.vm.image.copy(opsConsumed = 0)
        }
    }

    override fun createPeripheral(turtle: ITurtleAccess, side: TurtleSide): IPeripheral = WandPeripheral(Pair(turtle,side),null)
    class UpgradeSerializer : TurtleUpgradeSerialiser<WandTurtleUpgrade> {
        override fun fromJson(id: ResourceLocation?, `object`: JsonObject): WandTurtleUpgrade {
            print("fromJson")
            print(id)
            val item = GsonHelper.getAsItem(`object`,"item")
            return WandTurtleUpgrade(ItemStack(item))
        }

        override fun fromNetwork(id: ResourceLocation, buffer: FriendlyByteBuf): WandTurtleUpgrade {
            return WandTurtleUpgrade(buffer.readItem())
        }

        override fun toNetwork(buffer: FriendlyByteBuf, upgrade: WandTurtleUpgrade) {
            buffer.writeItem(upgrade.item)
        }
    }
}
package net.walksantor.hextweaks.computer

import at.petrak.hexcasting.common.lib.HexItems
import com.google.gson.JsonObject
import dan200.computercraft.api.peripheral.IPeripheral
import dan200.computercraft.api.turtle.AbstractTurtleUpgrade
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.TurtleSide
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
import dan200.computercraft.api.turtle.TurtleUpgradeType
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.walksantor.hextweaks.HexTweaks

class WandTurtleUpgrade : AbstractTurtleUpgrade(
    ResourceLocation(HexTweaks.MOD_ID,"wand"),
    TurtleUpgradeType.PERIPHERAL,
    "Magical",
    ItemStack(HexItems.STAFF_MINDSPLICE,1)
) {
    override fun createPeripheral(turtle: ITurtleAccess, side: TurtleSide): IPeripheral = WandPeripheral(Pair(turtle,side),null)
    class UpgradeSerializer : TurtleUpgradeSerialiser<WandTurtleUpgrade> {
        override fun fromJson(id: ResourceLocation?, `object`: JsonObject?): WandTurtleUpgrade {
            print("fromJson")
            print(id)
            print(`object`)
            return WandTurtleUpgrade()
        }

        override fun fromNetwork(id: ResourceLocation?, buffer: FriendlyByteBuf?): WandTurtleUpgrade {
            print("fromNetwork")
            print(id)
            print(buffer)
            return WandTurtleUpgrade()
        }

        override fun toNetwork(buffer: FriendlyByteBuf?, upgrade: WandTurtleUpgrade?) {
            print("toNetwork")
            print(buffer)
            print(upgrade)
        }
    }
}
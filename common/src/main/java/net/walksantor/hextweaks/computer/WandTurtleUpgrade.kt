package net.walksantor.hextweaks.computer

import com.google.gson.JsonObject
import dan200.computercraft.api.peripheral.IPeripheral
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser
import dan200.computercraft.api.turtle.AbstractTurtleUpgrade
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.TurtleSide
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
import dan200.computercraft.api.turtle.TurtleUpgradeType
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.walksantor.hextweaks.HexTweaks

class WandTurtleUpgrade(stack: ItemStack) : AbstractTurtleUpgrade(
    ResourceLocation(HexTweaks.MOD_ID,"wand"),
    TurtleUpgradeType.PERIPHERAL,
    "Magical",
    stack
) {
    override fun createPeripheral(turtle: ITurtleAccess, side: TurtleSide): IPeripheral = WandPeripheral(Pair(turtle,side),null)
    class UpgradeSerialiser : TurtleUpgradeSerialiser<WandTurtleUpgrade> {
        override fun fromJson(id: ResourceLocation?, `object`: JsonObject?): WandTurtleUpgrade {
            print(id)
            print(`object`)
            TODO("Not yet implemented")
        }

        override fun fromNetwork(id: ResourceLocation?, buffer: FriendlyByteBuf?): WandTurtleUpgrade {
            print(id)
            print(buffer)
            TODO("Not yet implemented")
        }

        override fun toNetwork(buffer: FriendlyByteBuf?, upgrade: WandTurtleUpgrade?) {
            print(buffer)
            print(upgrade)
            TODO("Not yet implemented")
        }
    }
}
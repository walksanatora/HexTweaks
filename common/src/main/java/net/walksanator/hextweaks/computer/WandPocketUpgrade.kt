package net.walksanator.hextweaks.computer

import com.google.gson.JsonObject
import dan200.computercraft.api.peripheral.IPeripheral
import dan200.computercraft.api.pocket.AbstractPocketUpgrade
import dan200.computercraft.api.pocket.IPocketAccess
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.GsonHelper
import net.minecraft.world.item.ItemStack
import net.walksanator.hextweaks.HexTweaks

class WandPocketUpgrade(val stack: ItemStack) : AbstractPocketUpgrade(
    ResourceLocation(net.walksanator.hextweaks.HexTweaks.MOD_ID,"wand"),
    "Magical",
    stack
) {

    override fun update(access: IPocketAccess?, peripheral: IPeripheral?) {
        if (peripheral is WandPeripheral) {
            if (!peripheral.isInit) {return}
            peripheral.vm.image = peripheral.vm.image.copy(opsConsumed = 0)
        }
    }
    override fun createPeripheral(access: IPocketAccess?): IPeripheral = WandPeripheral(null,access)
    class UpgradeSerialiser : PocketUpgradeSerialiser<WandPocketUpgrade> {
        override fun fromJson(id: ResourceLocation?, `object`: JsonObject?): WandPocketUpgrade {
            val item = GsonHelper.getAsItem(`object`,"item")
            return WandPocketUpgrade(ItemStack(item))
        }

        override fun fromNetwork(id: ResourceLocation, buffer: FriendlyByteBuf): WandPocketUpgrade {
            return WandPocketUpgrade(buffer.readItem())
        }

        override fun toNetwork(buffer: FriendlyByteBuf, upgrade: WandPocketUpgrade) {
            buffer.writeItem(upgrade.stack)
        }
    }
}
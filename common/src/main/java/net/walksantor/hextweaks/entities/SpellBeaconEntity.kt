package net.walksantor.hextweaks.entities

import net.minecraft.core.registries.Registries
import net.minecraft.world.entity.*
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level


class SpellBeaconEntity(entityType: EntityType<out LivingEntity>, level: Level) : LivingEntity(entityType, level) {
    companion object {
        val BUILDER: EntityType.Builder<SpellBeaconEntity> = EntityType.Builder.of(
            ::SpellBeaconEntity, MobCategory.MISC
        ).fireImmune().canSpawnFarFromPlayer()
    }
    override fun getArmorSlots(): MutableIterable<ItemStack> {
        TODO("Not yet implemented")
    }

    override fun setItemSlot(equipmentSlot: EquipmentSlot, itemStack: ItemStack) {
        TODO("Not yet implemented")
    }

    override fun getItemBySlot(equipmentSlot: EquipmentSlot): ItemStack {
        TODO("Not yet implemented")
    }

    override fun getMainArm(): HumanoidArm {
        TODO("Not yet implemented")
    }
}
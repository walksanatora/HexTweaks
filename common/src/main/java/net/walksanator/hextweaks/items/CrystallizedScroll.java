package net.walksanator.hextweaks.items;

import at.petrak.hexcasting.api.item.IotaHolderItem;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.common.items.ItemScroll;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CrystallizedScroll extends ItemScroll implements IotaHolderItem {
    public CrystallizedScroll(Properties properties) {
        super(properties,1);
    }

    @Override
    public boolean canWrite(ItemStack stack, @Nullable Iota iota) {
        return false;
    }

    @Override
    public void writeDatum(ItemStack stack, @Nullable Iota iota) {}//you cannot write to it


    @Override
    public boolean isFoil(@NotNull ItemStack stack) {return true;}
}

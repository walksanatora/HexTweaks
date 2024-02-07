package net.walksantor.hextweaks.items;

import at.petrak.hexcasting.api.item.PigmentItem;
import at.petrak.hexcasting.api.pigment.ColorProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

import static javax.swing.UIManager.getInt;

public class VirtualPigment extends Item implements PigmentItem {

    public VirtualPigment(Properties properties) {
        super(properties);
    }



    @Override
    public ColorProvider provideColor(ItemStack stack, UUID owner) {
        int rgb = stack.getOrCreateTag().getInt("rgb");
        return new MyColorProvider(rgb);
    }
    protected static class MyColorProvider extends ColorProvider {
        private final int rgba;
        MyColorProvider(int rgba) {
            this.rgba = rgba;
        }
        @Override
        protected int getRawColor(float time, Vec3 position) {
            return rgba;
        }
    }
}

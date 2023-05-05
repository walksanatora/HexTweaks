package net.walksanator.hextweaks.blocks;

import at.petrak.hexcasting.api.PatternRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.walksanator.hextweaks.HexTweaks;
import net.walksanator.hextweaks.items.CrystallizedScroll;
import net.walksanator.hextweaks.items.ItemRegister;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

import static net.walksanator.hextweaks.patterns.PatternRegister.lookupPatternIllegal;

public class CrystalizedScrollBlock extends Block {

    public CrystalizedScrollBlock(Properties properties) {
        super(properties);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onPlace(@NotNull BlockState newState, Level level, @NotNull BlockPos position, @NotNull BlockState OldState, boolean movedByPiston) {
        if (!level.isClientSide) {
            Random rand = new Random();
            //create the item
            ItemStack scroll = new ItemStack(ItemRegister.CRYSTALLIZED_SCROLL.get());

            //create the data
            CompoundTag ctag = new CompoundTag();

            ResourceLocation target = HexTweaks.GrandSpells.get(
                    rand.nextInt(HexTweaks.GrandSpells.size())
            );

            PatternRegistry.PatternEntry entry = lookupPatternIllegal( //use the illegal lookup method since Grand spells can have Illegal signatures
                    target
            );

            ctag.put(CrystallizedScroll.TAG_PATTERN,
                    entry.prototype().serializeToNBT()
                    );
            ctag.put(CrystallizedScroll.TAG_OP_ID,StringTag.valueOf(target.toString()));

            scroll.setTag(ctag);

            //turn the item into an entity
            ItemEntity scroll_ent = new ItemEntity(level,position.getX(),position.getY(),position.getZ(),scroll);

            //spawn
            level.addFreshEntity(scroll_ent);
            level.setBlockAndUpdate(position,Blocks.AIR.defaultBlockState());
        }
    }
}

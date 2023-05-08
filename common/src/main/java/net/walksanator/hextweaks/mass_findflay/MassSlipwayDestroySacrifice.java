package net.walksanator.hextweaks.mass_findflay;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import dev.architectury.platform.Platform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import ram.talia.hexal.common.blocks.BlockSlipway;

import java.util.List;

@SuppressWarnings("unused")
public class MassSlipwayDestroySacrifice implements MassSacrificeHandler {
    @Override
    public boolean call(List<Entity> sacrifices, BlockPos position, Integer totalLevel, CastingContext ctx) {
        if (!Platform.isModLoaded("hexal")) { return false; } //hexal is not loaded exit early
        if (!(ctx.getWorld().getLevel().getBlockState(position).getBlock() instanceof BlockSlipway)) { return false;}
        ctx.getWorld().getLevel().setBlockAndUpdate(position, Blocks.AIR.defaultBlockState());
        return true; //successfully so we return true
    }
}

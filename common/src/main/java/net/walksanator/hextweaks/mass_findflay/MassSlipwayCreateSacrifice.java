package net.walksanator.hextweaks.mass_findflay;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import dev.architectury.platform.Platform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.AirBlock;
import ram.talia.hexal.common.lib.HexalBlocks;

import java.util.List;

@SuppressWarnings("unused")
public class MassSlipwayCreateSacrifice implements MassSacrificeHandler {
    @Override
    public boolean call(List<Entity> sacrifices, BlockPos position, Integer totalLevel, CastingContext ctx) {
        if (!Platform.isModLoaded("hexal")) { return false; } //hexal is not loaded exit early
        if (!(ctx.getWorld().getLevel().getBlockState(position).getBlock() instanceof AirBlock )) {return false;} //block is not air, exit early
        ctx.getWorld().getLevel().setBlockAndUpdate(position, HexalBlocks.SLIPWAY.defaultBlockState());
        return true; //successfully so we return true
    }
}

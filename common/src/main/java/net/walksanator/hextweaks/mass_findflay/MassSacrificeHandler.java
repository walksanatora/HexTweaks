package net.walksanator.hextweaks.mass_findflay;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

import java.util.List;

public interface MassSacrificeHandler {
    //takes a list of sacrifices that will be used in the ritual
    //the position we are working with
    //the total levels of the villagers (so we don't have to calculate it)
    //and the casting context (so we can get the player/level if needed)
    @SuppressWarnings("unused")
    boolean call(List<Entity> sacrifices, BlockPos position, Integer totalLevel, CastingContext ctx);
}

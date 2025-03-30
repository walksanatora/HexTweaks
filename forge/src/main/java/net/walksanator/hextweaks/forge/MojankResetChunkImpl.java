package net.walksanator.hextweaks.forge;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import com.sk89q.worldedit.forge.ForgeAdapter;

public class MojankResetChunkImpl {
    public static World getWEWorld(ServerLevel level) {
        return ForgeAdapter.adapt(level);
    }

    public static BlockVector3 getWEBlockPos(BlockPos pos) {
        return ForgeAdapter.adapt(pos);
    }
}

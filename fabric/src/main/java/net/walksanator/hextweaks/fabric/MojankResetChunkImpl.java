package net.walksanator.hextweaks.fabric;

import com.sk89q.worldedit.fabric.FabricAdapter;
import com.sk89q.worldedit.fabric.FabricWorld;
import com.sk89q.worldedit.fabric.FabricWorldEdit;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public class MojankResetChunkImpl {
    public static World getWEWorld(ServerLevel level) {
        return FabricAdapter.adapt(level);
    }

    public static BlockVector3 getWEBlockPos(BlockPos pos) {
        return FabricAdapter.adapt(pos);
    }
}

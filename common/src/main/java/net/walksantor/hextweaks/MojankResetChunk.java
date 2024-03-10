package net.walksantor.hextweaks;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.datafixers.util.Unit;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

public class MojankResetChunk {
    private static boolean locked = false;

    public static void resetChunk(ServerLevel serverLevel, ChunkPos chunkPos) {
        HexTweaks.LOGGER.info("RESETTING CHUNK %s".formatted(chunkPos));
        while (locked) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                //ignored
            }
        }
        locked = true;
        ServerChunkCache serverChunkCache = serverLevel.getChunkSource();
        serverChunkCache.chunkMap.debugReloadGenerator();

        LevelChunk levelChunk = serverChunkCache.getChunk(chunkPos.x, chunkPos.z, false);
        if (levelChunk != null) {
            for (BlockPos blockPos : BlockPos.betweenClosed(chunkPos.getMinBlockX(), serverLevel.getMinBuildHeight(), chunkPos.getMinBlockZ(), chunkPos.getMaxBlockX(), serverLevel.getMaxBuildHeight() - 1, chunkPos.getMaxBlockZ())) {
                serverLevel.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 16);
            }
        }
        HexTweaks.LOGGER.info("Set Blocks to AIR");
        ProcessorMailbox<Runnable> processorMailbox = ProcessorMailbox.create(Util.backgroundExecutor(), "worldgen-resetchunks");
        UnmodifiableIterator<ChunkStatus> var33 = ImmutableList.of(ChunkStatus.BIOMES, ChunkStatus.NOISE, ChunkStatus.SURFACE, ChunkStatus.CARVERS, ChunkStatus.FEATURES, ChunkStatus.INITIALIZE_LIGHT).iterator();

        HexTweaks.LOGGER.info("created mailbox and iter");

        while (var33.hasNext()) {
            ChunkStatus chunkStatus = var33.next();
            long r = System.currentTimeMillis();
            Supplier<Unit> var10000 = () -> Unit.INSTANCE;
            CompletableFuture<Unit> completableFuture = CompletableFuture.supplyAsync(var10000, processorMailbox::tell);

            LevelChunk levelChunk2 = serverChunkCache.getChunk(chunkPos.x, chunkPos.z, false);
            if (levelChunk2 != null) {
                ArrayList<ChunkAccess> list = Lists.newArrayList();
                int u = Math.max(1, chunkStatus.getRange());
                for (int v = chunkPos.z - u; v <= chunkPos.z + u; ++v) {
                    for (int w = chunkPos.x - u; w <= chunkPos.x + u; ++w) {
                        ChunkAccess chunkAccess = serverChunkCache.getChunk(w, v, chunkStatus.getParent(), true);
                        ChunkAccess chunkAccess2 = getChunkAccess(chunkAccess);
                        list.add(chunkAccess2);
                    }
                }
                Function var10001 = (unit) -> {
                    return chunkStatus.generate(processorMailbox::tell, serverLevel, serverChunkCache.getGenerator(), serverLevel.getStructureManager(), serverChunkCache.getLightEngine(), (chunkAccess) -> {
                        HexTweaks.LOGGER.info("Well Fuck.");
                        throw new UnsupportedOperationException("Not creating full chunks here");
                    }, list).thenApply((either) -> {
                        if (chunkStatus == ChunkStatus.NOISE) {
                            either.left().ifPresent((chunkAccess) -> Heightmap.primeHeightmaps(chunkAccess, ChunkStatus.POST_FEATURES));
                        }
                        return Unit.INSTANCE;
                    });
                };
                completableFuture = completableFuture.thenComposeAsync(var10001, processorMailbox::tell);
            }
            MinecraftServer var36 = serverLevel.getServer();
            Objects.requireNonNull(completableFuture);
            var36.managedBlock(completableFuture::isDone);
            HexTweaks.LOGGER.info(chunkStatus + " took " + (System.currentTimeMillis() - r) + " ms");
        }

        long x = System.currentTimeMillis();

        LevelChunk levelChunk3 = serverChunkCache.getChunk(chunkPos.x, chunkPos.z, false);
        if (levelChunk3 != null) {
            for (BlockPos blockPos2 : BlockPos.betweenClosed(chunkPos.getMinBlockX(), serverLevel.getMinBuildHeight(), chunkPos.getMinBlockZ(), chunkPos.getMaxBlockX(), serverLevel.getMaxBuildHeight() - 1, chunkPos.getMaxBlockZ())) {
                serverChunkCache.blockChanged(blockPos2);
            }
        }
        locked=false;
        HexTweaks.LOGGER.info("blockChanged took " + (System.currentTimeMillis() - x) + " ms");
        HexTweaks.LOGGER.info("RESET FINISHED ON CHUNK %s".formatted(chunkPos));
    }

    private static ChunkAccess getChunkAccess(ChunkAccess chunkAccess) {
        ChunkAccess chunkAccess2;
        if (chunkAccess instanceof ImposterProtoChunk) {
            chunkAccess2 = new ImposterProtoChunk(((ImposterProtoChunk) chunkAccess).getWrapped(), true);
        } else if (chunkAccess instanceof LevelChunk) {
            chunkAccess2 = new ImposterProtoChunk((LevelChunk) chunkAccess, true);
        } else {
            chunkAccess2 = chunkAccess;
        }
        return chunkAccess2;
    }
}

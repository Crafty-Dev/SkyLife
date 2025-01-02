package de.crafty.skylife.world;

import de.crafty.skylife.SkyLife;
import de.crafty.skylife.SkyLifeClient;
import de.crafty.skylife.SkyLifeServer;
import de.crafty.skylife.util.VectorUtils;
import de.crafty.skylife.world.chunkgen.SkyLifeChunkGenOverworld;
import net.minecraft.core.BlockPos;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.storage.ServerLevelData;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class SpawnGenerator {

    public static final TreeGrower SKYBLOCK_OAK = new TreeGrower(
            "oak",
            0.1F,
            Optional.empty(),
            Optional.empty(),
            Optional.of(TreeFeatures.OAK),
            Optional.empty(),
            Optional.empty(),
            Optional.empty()
    );

    public static void start(ServerLevel world, ServerLevelData worldProperties) {

        if (!(world.getChunkSource().getGenerator() instanceof SkyLifeChunkGenOverworld))
            return;

        SpawnGenerator.genIslands(world, SkyLife.ISLAND_COUNT);
    }


    private static void genIslands(ServerLevel world, int amount) {

        List<BlockPos> spawns = new ArrayList<>();

        double distance = 75.0F;
        double circumference = (amount - 1) * distance;
        double radius = circumference / (Math.PI * 2);
        double angle = 360.0D / amount;
        double radian = Math.toRadians(angle);

        for (int i = 0; i < amount; i++) {

            Vector3f vec = VectorUtils.rotateAroundY(new Vector3f(1, 0, 1), radian * i);
            vec.normalize();
            vec.mul((float) radius, (float) 0, (float) radius);

            spawns.add(SpawnGenerator.genIsland(world, (int) vec.x(), 63, (int) vec.z()));
        }

        SpawnGenerator.createSpawnLocations(world, spawns);
    }

    private static BlockPos genIsland(ServerLevel world, int x, int y, int z) {

        BlockPos src = new BlockPos(x, y, z);

        world.setBlock(src, Blocks.GRASS_BLOCK.defaultBlockState(), 2);

        SpawnGenerator.SKYBLOCK_OAK.growTree(world, world.getChunkSource().getGenerator(), src.above(), Blocks.OAK_SAPLING.defaultBlockState(), world.getRandom());

        BlockPos top = src.above();
        while (!world.getBlockState(top).isAir())
            top = top.above();

        return top;

    }

    private static void createSpawnLocations(ServerLevel world, List<BlockPos> positions) {
        SkyLifeWorldDataLoader dataLoader = SkyLifeWorldDataLoader.getInstance(world.getServer());
        dataLoader.createOriginalSpawns(positions);
    }

    public static void assignNextSpawn(ServerLevel world, ServerPlayer player) {
        if(player.getStats().getValue(Stats.CUSTOM, Stats.PLAY_TIME) > 0)
            return;

        SkyLifeWorldDataLoader dataLoader = SkyLifeWorldDataLoader.getInstance(world.getServer());

        BlockPos spawnPos = dataLoader.assignNextSpawn(player.getUUID());

        player.setRespawnPosition(ServerLevel.OVERWORLD, spawnPos, 0.0F, true, false);

        Vector3f vec = new Vector3f();
        Vector3f playerVec = new Vector3f(spawnPos.getX(), 0, spawnPos.getZ());
        vec.sub(playerVec);
        playerVec.normalize();

        player.teleportTo(world, spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, Set.of(), (float) (Math.atan2(vec.z(), vec.x()) * 180F / Math.PI) - 90.0F, 0.0F, true);
    }
}

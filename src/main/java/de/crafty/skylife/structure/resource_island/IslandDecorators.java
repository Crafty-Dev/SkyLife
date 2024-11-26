package de.crafty.skylife.structure.resource_island;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IslandDecorators {



    private static void placeFeatureConfigured(ResourceKey<ConfiguredFeature<?, ?>> feature, WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos featurePos) {
        Holder<ConfiguredFeature<?, ?>> holder = level.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE).getHolder(feature).orElse(null);
        if (holder != null) {
            holder.value().place(level, chunkGenerator, random, featurePos);
        }

    }

    private static void placeFeature(ResourceKey<PlacedFeature> feature, WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos featurePos) {
        Holder<PlacedFeature> holder = level.registryAccess().registryOrThrow(Registries.PLACED_FEATURE).getHolder(feature).orElse(null);
        if (holder != null) {
            holder.value().place(level, chunkGenerator, random, featurePos);
        }

    }

    private static void decorateGrass(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource randomSource, BlockPos pos){
        Optional<Holder.Reference<PlacedFeature>> optional = level.registryAccess()
                .registryOrThrow(Registries.PLACED_FEATURE)
                .getHolder(VegetationPlacements.GRASS_BONEMEAL);

        BlockState state = level.getBlockState(pos);
        if (state.isAir()) {
            Holder<PlacedFeature> holder;
            if (randomSource.nextInt(8) == 0) {
                List<ConfiguredFeature<?, ?>> list = level.getBiome(pos).value().getGenerationSettings().getFlowerFeatures();
                if (list.isEmpty()) {
                    return;
                }

                holder = ((RandomPatchConfiguration) list.getFirst().config()).feature();
            } else {
                if (optional.isEmpty()) {
                    return;
                }

                holder = optional.get();
            }

            holder.value().place(level, chunkGenerator, randomSource, pos);
        }
    }


    static class Oak implements ResourceIslandStructure.IslandDecorator {

        @Override
        public void decorateSpecific(List<BlockPos> topBlocks, List<BlockPos> bottomBlocks, WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, ChunkPos origin, Vec3i templateSize) {

            topBlocks.forEach(blockPos -> {
                if(random.nextFloat() < 1.0F / (templateSize.getX() * templateSize.getZ()) * 2) {
                    placeFeatureConfigured(TreeFeatures.OAK, level, chunkGenerator, random, blockPos.above());
                }else if(random.nextFloat() < 0.75F)
                    decorateGrass(level, chunkGenerator, random, blockPos.above());
            });

        }
    }

    static class Birch implements ResourceIslandStructure.IslandDecorator {

        @Override
        public void decorateSpecific(List<BlockPos> topBlocks, List<BlockPos> bottomBlocks, WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, ChunkPos origin, Vec3i templateSize) {

            topBlocks.forEach(blockPos -> {
                if(random.nextFloat() < 1.0F / (templateSize.getX() * templateSize.getZ()) * 2) {
                    placeFeatureConfigured(TreeFeatures.BIRCH, level, chunkGenerator, random, blockPos.above());
                }else if(random.nextFloat() < 0.75F)
                    decorateGrass(level, chunkGenerator, random, blockPos.above());
            });

        }
    }

    static class Spruce implements ResourceIslandStructure.IslandDecorator {

        @Override
        public void decorateSpecific(List<BlockPos> topBlocks, List<BlockPos> bottomBlocks, WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, ChunkPos origin, Vec3i templateSize) {

            topBlocks.forEach(blockPos -> {
                if(random.nextFloat() < 1.0F / (templateSize.getX() * templateSize.getZ()) * 2) {
                    placeFeatureConfigured(TreeFeatures.SPRUCE, level, chunkGenerator, random, blockPos.above());
                }else if(random.nextFloat() < 0.75F)
                    decorateGrass(level, chunkGenerator, random, blockPos.above());
            });

        }
    }

    static class DarkOak implements ResourceIslandStructure.IslandDecorator {

        @Override
        public void decorateSpecific(List<BlockPos> topBlocks, List<BlockPos> bottomBlocks, WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, ChunkPos origin, Vec3i templateSize) {

            topBlocks.forEach(blockPos -> {
                if(random.nextFloat() < 1.0F / (templateSize.getX() * templateSize.getZ()) * 1.5F) {
                    placeFeatureConfigured(TreeFeatures.DARK_OAK, level, chunkGenerator, random, blockPos.above());
                }else if(random.nextFloat() < 0.75F)
                    decorateGrass(level, chunkGenerator, random, blockPos.above());
            });

        }
    }

    static class Jungle implements ResourceIslandStructure.IslandDecorator {

        @Override
        public void decorateSpecific(List<BlockPos> topBlocks, List<BlockPos> bottomBlocks, WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, ChunkPos origin, Vec3i templateSize) {

            topBlocks.forEach(blockPos -> {
                if(random.nextFloat() < 1.0F / (templateSize.getX() * templateSize.getZ()) * 2) {
                    placeFeatureConfigured(TreeFeatures.JUNGLE_TREE, level, chunkGenerator, random, blockPos.above());
                }else if(random.nextFloat() < 0.75F)
                    decorateGrass(level, chunkGenerator, random, blockPos.above());
            });

        }
    }

    static class Acacia implements ResourceIslandStructure.IslandDecorator {

        @Override
        public void decorateSpecific(List<BlockPos> topBlocks, List<BlockPos> bottomBlocks, WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, ChunkPos origin, Vec3i templateSize) {

            topBlocks.forEach(blockPos -> {
                if(random.nextFloat() < 1.0F / (templateSize.getX() * templateSize.getZ()) * 2) {
                    placeFeatureConfigured(TreeFeatures.ACACIA, level, chunkGenerator, random, blockPos.above());
                }else if(random.nextFloat() < 0.75F)
                    decorateGrass(level, chunkGenerator, random, blockPos.above());
            });

        }
    }

    static class Mangrove implements ResourceIslandStructure.IslandDecorator {

        @Override
        public void decorateSpecific(List<BlockPos> topBlocks, List<BlockPos> bottomBlocks, WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, ChunkPos origin, Vec3i templateSize ) {


            topBlocks.forEach(blockPos -> {
                if(random.nextFloat() < 1.0F / (templateSize.getX() * templateSize.getZ()) * 3.5F) {
                    placeFeatureConfigured(TreeFeatures.MANGROVE, level, chunkGenerator, random, blockPos.above());
                }else if(this.isSuroundedByBlocks(level, blockPos) && random.nextFloat() < 0.4F){
                    level.setBlock(blockPos, Blocks.WATER.defaultBlockState(), Block.UPDATE_ALL);
                }
            });

        }


        private boolean isSuroundedByBlocks(WorldGenLevel level, BlockPos pos){
            for(Direction direction : Direction.values()){
                if(direction == Direction.DOWN || direction == Direction.UP)
                    continue;

                if(level.getBlockState(pos.relative(direction)).isAir())
                    return false;
            }

            return true;
        }
    }

}

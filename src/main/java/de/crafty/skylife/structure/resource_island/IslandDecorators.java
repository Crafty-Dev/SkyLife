package de.crafty.skylife.structure.resource_island;

import de.crafty.skylife.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.*;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;

import java.util.List;
import java.util.Optional;

public class IslandDecorators {


    private static void placeFeatureConfigured(ResourceKey<ConfiguredFeature<?, ?>> feature, WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos featurePos) {
        Holder<ConfiguredFeature<?, ?>> holder = level.registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE).getOrThrow(feature);
        if (holder != null) {
            holder.value().place(level, chunkGenerator, random, featurePos);
        }

    }

    private static void placeFeature(ResourceKey<PlacedFeature> feature, WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos featurePos) {
        Holder<PlacedFeature> holder = level.registryAccess().lookupOrThrow(Registries.PLACED_FEATURE).getOrThrow(feature);
        holder.value().place(level, chunkGenerator, random, featurePos);

    }

    private static void decorateGrass(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource randomSource, BlockPos pos) {
        Optional<Holder.Reference<PlacedFeature>> optional = level.registryAccess()
                .lookupOrThrow(Registries.PLACED_FEATURE)
                .get(VegetationPlacements.GRASS_BONEMEAL);

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

    private static boolean isSuroundedByBlocks(WorldGenLevel level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (direction == Direction.DOWN || direction == Direction.UP)
                continue;

            if (!level.getLevel().isLoaded(pos.relative(direction)))
                return false;

            BlockState attached = level.getBlockState(pos.relative(direction));

            if (attached.isAir() || attached.getPistonPushReaction() == PushReaction.DESTROY) {
                return false;
            }
        }

        return true;
    }


    static class Oak implements ResourceIslandStructure.IslandDecorator {

        @Override
        public void decorateSpecific(List<BlockPos> topBlocks, List<BlockPos> bottomBlocks, WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, ChunkPos origin, Vec3i templateSize) {

            topBlocks.forEach(blockPos -> {
                if (random.nextFloat() < 1.0F / (templateSize.getX() * templateSize.getZ()) * 2) {
                    placeFeatureConfigured(TreeFeatures.OAK_BEES_005, level, chunkGenerator, random, blockPos.above());
                } else if (random.nextFloat() < 0.75F)
                    decorateGrass(level, chunkGenerator, random, blockPos.above());
            });

        }
    }

    static class Birch implements ResourceIslandStructure.IslandDecorator {

        @Override
        public void decorateSpecific(List<BlockPos> topBlocks, List<BlockPos> bottomBlocks, WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, ChunkPos origin, Vec3i templateSize) {

            topBlocks.forEach(blockPos -> {
                if (random.nextFloat() < 1.0F / (templateSize.getX() * templateSize.getZ()) * 2) {
                    placeFeatureConfigured(random.nextFloat() < 0.05F ? TreeFeatures.SUPER_BIRCH_BEES : TreeFeatures.BIRCH_BEES_005, level, chunkGenerator, random, blockPos.above());
                } else if (random.nextFloat() < 0.75F)
                    decorateGrass(level, chunkGenerator, random, blockPos.above());
            });

        }
    }

    static class Spruce implements ResourceIslandStructure.IslandDecorator {

        @Override
        public void decorateSpecific(List<BlockPos> topBlocks, List<BlockPos> bottomBlocks, WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, ChunkPos origin, Vec3i templateSize) {

            topBlocks.forEach(blockPos -> {
                if (random.nextFloat() < 1.0F / (templateSize.getX() * templateSize.getZ()) * 2) {
                    placeFeatureConfigured(TreeFeatures.SPRUCE, level, chunkGenerator, random, blockPos.above());
                } else if (random.nextFloat() < 0.75F)
                    decorateGrass(level, chunkGenerator, random, blockPos.above());
            });

        }
    }

    static class DarkOak implements ResourceIslandStructure.IslandDecorator {

        @Override
        public void decorateSpecific(List<BlockPos> topBlocks, List<BlockPos> bottomBlocks, WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, ChunkPos origin, Vec3i templateSize) {

            topBlocks.forEach(blockPos -> {
                if (random.nextFloat() < 1.0F / (templateSize.getX() * templateSize.getZ()) * 1.5F) {
                    placeFeatureConfigured(TreeFeatures.DARK_OAK, level, chunkGenerator, random, blockPos.above());
                } else if (random.nextFloat() < 0.75F)
                    decorateGrass(level, chunkGenerator, random, blockPos.above());
            });

        }
    }

    static class Jungle implements ResourceIslandStructure.IslandDecorator {

        @Override
        public void decorateSpecific(List<BlockPos> topBlocks, List<BlockPos> bottomBlocks, WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, ChunkPos origin, Vec3i templateSize) {

            topBlocks.forEach(blockPos -> {
                if (random.nextFloat() < 1.0F / (templateSize.getX() * templateSize.getZ()) * 2) {
                    placeFeatureConfigured(TreeFeatures.JUNGLE_TREE, level, chunkGenerator, random, blockPos.above());
                } else if (random.nextFloat() < 0.65F)
                    decorateGrass(level, chunkGenerator, random, blockPos.above());
                else if (level.canSeeSky(blockPos.above()) && random.nextFloat() < 0.25F)
                    placeFeatureConfigured(VegetationFeatures.BAMBOO_SOME_PODZOL, level, chunkGenerator, random, blockPos.above());
            });

        }
    }

    static class Acacia implements ResourceIslandStructure.IslandDecorator {

        @Override
        public void decorateSpecific(List<BlockPos> topBlocks, List<BlockPos> bottomBlocks, WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, ChunkPos origin, Vec3i templateSize) {

            topBlocks.forEach(blockPos -> {
                if (random.nextFloat() < 1.0F / (templateSize.getX() * templateSize.getZ()) * 2) {
                    placeFeatureConfigured(TreeFeatures.ACACIA, level, chunkGenerator, random, blockPos.above());
                } else if (random.nextFloat() < 0.75F)
                    decorateGrass(level, chunkGenerator, random, blockPos.above());
            });

        }
    }

    static class Mangrove implements ResourceIslandStructure.IslandDecorator {

        @Override
        public void decorateSpecific(List<BlockPos> topBlocks, List<BlockPos> bottomBlocks, WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, ChunkPos origin, Vec3i templateSize) {


            topBlocks.forEach(blockPos -> {
                if (random.nextFloat() < 1.0F / (templateSize.getX() * templateSize.getZ()) * 3.5F) {
                    placeFeatureConfigured(TreeFeatures.MANGROVE, level, chunkGenerator, random, blockPos.above());
                } else if (isSuroundedByBlocks(level, blockPos) && random.nextFloat() < 0.4F) {
                    level.setBlock(blockPos, Blocks.WATER.defaultBlockState(), Block.UPDATE_ALL);
                }
            });

        }

    }

    static class Cherry implements ResourceIslandStructure.IslandDecorator {


        @Override
        public void decorateSpecific(List<BlockPos> topBlocks, List<BlockPos> bottomBlocks, WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, ChunkPos origin, Vec3i templateSize) {

            topBlocks.forEach(blockPos -> {
                if (random.nextFloat() < 1.0F / (templateSize.getX() * templateSize.getZ()) * 2.0F) {
                    placeFeatureConfigured(TreeFeatures.CHERRY, level, chunkGenerator, random, blockPos.above());
                } else if (isSuroundedByBlocks(level, blockPos) && random.nextFloat() < 0.125) {
                    level.setBlock(blockPos, Blocks.WATER.defaultBlockState(), Block.UPDATE_CLIENTS);
                } else if (random.nextFloat() < 0.75F)
                    decorateGrass(level, chunkGenerator, random, blockPos.above());
            });

        }
    }

    static class Desert implements ResourceIslandStructure.IslandDecorator {


        @Override
        public void decorateSpecific(List<BlockPos> topBlocks, List<BlockPos> bottomBlocks, WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, ChunkPos origin, Vec3i templateSize) {

            topBlocks.forEach(blockPos -> {
                if (random.nextFloat() < 1.0F / (templateSize.getX() * templateSize.getZ()) * 4.5F)
                    placeFeatureConfigured(VegetationFeatures.PATCH_CACTUS, level, chunkGenerator, random, blockPos.above());
                else if (random.nextFloat() < 0.125F)
                    level.setBlock(blockPos.above(), Blocks.DEAD_BUSH.defaultBlockState(), Block.UPDATE_CLIENTS);

            });

        }
    }


    static class Oil implements ResourceIslandStructure.IslandDecorator {


        @Override
        public void decorateSpecific(List<BlockPos> topBlocks, List<BlockPos> bottomBlocks, WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, ChunkPos origin, Vec3i templateSize) {

            topBlocks.forEach(blockPos -> {
                if (level.getBlockState(blockPos).is(Blocks.LAPIS_BLOCK)) {

                    level.setBlock(blockPos, random.nextFloat() < 1.0F / 3.0F ? BlockRegistry.OIL.defaultBlockState() : Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
                    FluidState fluidState = level.getFluidState(blockPos);
                    if (!fluidState.isEmpty()) {
                        level.scheduleTick(blockPos, fluidState.getType(), 0);
                    }
                }
            });
        }
    }


    static class Dripstone implements ResourceIslandStructure.IslandDecorator {


        @Override
        public void decorateSpecific(List<BlockPos> topBlocks, List<BlockPos> bottomBlocks, WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, ChunkPos origin, Vec3i templateSize) {

            topBlocks.forEach(blockPos -> {
                if (level.getBlockState(blockPos).is(Blocks.LAPIS_BLOCK)) {

                    level.setBlock(blockPos, random.nextFloat() < 1.0F / 3.0F ? Blocks.LAVA.defaultBlockState() : Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
                    FluidState fluidState = level.getFluidState(blockPos);
                    if (!fluidState.isEmpty()) {
                        level.scheduleTick(blockPos, fluidState.getType(), 0);
                    }
                }

                if (random.nextFloat() < 0.5F) {
                    if (random.nextFloat() < 0.5F)
                        placeFeatureConfigured(CaveFeatures.POINTED_DRIPSTONE, level, chunkGenerator, random, blockPos.above());
                    else
                        placeFeatureConfigured(CaveFeatures.LARGE_DRIPSTONE, level, chunkGenerator, random, blockPos.above());
                }
            });

            bottomBlocks.forEach(blockPos -> {
                if (random.nextFloat() < 0.5F) {
                    if (random.nextFloat() < 0.5F)
                        placeFeatureConfigured(CaveFeatures.POINTED_DRIPSTONE, level, chunkGenerator, random, blockPos.below());
                    else
                        placeFeatureConfigured(CaveFeatures.LARGE_DRIPSTONE, level, chunkGenerator, random, blockPos.below());
                }

            });
        }
    }

    static class LushCave implements ResourceIslandStructure.IslandDecorator {


        @Override
        public void decorateSpecific(List<BlockPos> topBlocks, List<BlockPos> bottomBlocks, WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, ChunkPos origin, Vec3i templateSize) {

            topBlocks.forEach(blockPos -> {
                if (level.getBlockState(blockPos).is(Blocks.LAPIS_BLOCK)) {

                    level.setBlock(blockPos, random.nextFloat() < 1.0F / 3.0F ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
                    FluidState fluidState = level.getFluidState(blockPos);
                    if (!fluidState.isEmpty()) {
                        level.scheduleTick(blockPos, fluidState.getType(), 0);
                    }
                }

                if (random.nextFloat() < 0.25F) {
                    placeFeatureConfigured(CaveFeatures.MOSS_PATCH, level, chunkGenerator, random, blockPos.above());
                }
            });

        }
    }


    static class NetherCrimson implements ResourceIslandStructure.IslandDecorator {

        @Override
        public void decorateSpecific(List<BlockPos> topBlocks, List<BlockPos> bottomBlocks, WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, ChunkPos origin, Vec3i templateSize) {

            topBlocks.forEach(topPos -> {

                if (random.nextFloat() < 1.0F / (templateSize.getX() * templateSize.getZ()) * 3.0F)
                    placeFeatureConfigured(TreeFeatures.CRIMSON_FUNGUS, level, chunkGenerator, random, topPos.above());
                else if (random.nextFloat() < 0.125F)
                    placeFeatureConfigured(NetherFeatures.CRIMSON_FOREST_VEGETATION_BONEMEAL, level, chunkGenerator, random, topPos.above());
            });

        }
    }


    static class NetherWarped implements ResourceIslandStructure.IslandDecorator {


        @Override
        public void decorateSpecific(List<BlockPos> topBlocks, List<BlockPos> bottomBlocks, WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, ChunkPos origin, Vec3i templateSize) {

            topBlocks.forEach(topPos -> {
                if (random.nextFloat() < 1.0F / (templateSize.getX() * templateSize.getZ()) * 2.0F)
                    placeFeatureConfigured(TreeFeatures.WARPED_FUNGUS, level, chunkGenerator, random, topPos.above());
                else if (random.nextFloat() < 0.375F) {
                    if (random.nextFloat() < 0.25F)
                        placeFeatureConfigured(NetherFeatures.WARPED_FOREST_VEGETATION_BONEMEAL, level, chunkGenerator, random, topPos.above());
                    else if (random.nextFloat() < 0.75F)
                        placeFeatureConfigured(NetherFeatures.NETHER_SPROUTS_BONEMEAL, level, chunkGenerator, random, topPos.above());

                    if (random.nextInt(6) == 0)
                        placeFeatureConfigured(NetherFeatures.TWISTING_VINES_BONEMEAL, level, chunkGenerator, random, topPos.above());
                }
            });

        }
    }

    static class NetherSoulSand implements ResourceIslandStructure.IslandDecorator {


        @Override
        public void decorateSpecific(List<BlockPos> topBlocks, List<BlockPos> bottomBlocks, WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, ChunkPos origin, Vec3i templateSize) {

            topBlocks.forEach(topPos -> {

                if (level.getBlockState(topPos).is(Blocks.LAPIS_BLOCK))
                    level.setBlock(topPos, random.nextFloat() < 0.2F ? Blocks.NETHER_WART.defaultBlockState() : Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);

                else if (random.nextFloat() < 0.2F)
                    level.setBlock(topPos.above(), Blocks.NETHER_WART.defaultBlockState().setValue(NetherWartBlock.AGE, random.nextInt(NetherWartBlock.MAX_AGE + 1)), Block.UPDATE_CLIENTS);

            });

        }
    }

    static class NetherBasalt implements ResourceIslandStructure.IslandDecorator {


        @Override
        public void decorateSpecific(List<BlockPos> topBlocks, List<BlockPos> bottomBlocks, WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, ChunkPos origin, Vec3i templateSize) {

            topBlocks.forEach(topPos -> {

                if (level.getBlockState(topPos).is(Blocks.LAPIS_BLOCK)) {
                    level.setBlock(topPos, random.nextFloat() < 0.3F ? Blocks.LAVA.defaultBlockState() : Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
                    FluidState fluidState = level.getFluidState(topPos);
                    if (!fluidState.isEmpty()) {
                        level.scheduleTick(topPos, fluidState.getType(), 0);
                    }
                } else if (isSuroundedByBlocks(level, topPos))
                    level.setBlock(topPos, Blocks.MAGMA_BLOCK.defaultBlockState(), Block.UPDATE_CLIENTS);
            });

        }
    }


    static class NetherWastes implements ResourceIslandStructure.IslandDecorator {


        @Override
        public void decorateSpecific(List<BlockPos> topBlocks, List<BlockPos> bottomBlocks, WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, ChunkPos origin, Vec3i templateSize) {

        }

    }


    static class EndChorus implements ResourceIslandStructure.IslandDecorator {


        @Override
        public void decorateSpecific(List<BlockPos> topBlocks, List<BlockPos> bottomBlocks, WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, ChunkPos origin, Vec3i templateSize) {

            topBlocks.forEach(topPos -> {

                if (random.nextFloat() < 0.0625)
                    placeFeatureConfigured(EndFeatures.CHORUS_PLANT, level, chunkGenerator, random, topPos.above());

                if(random.nextFloat() < 0.0625F / 3.0F){
                    BlockPos aboveTop = topPos.above();
                    Shulker shulker = EntityType.SHULKER.create(level.getLevel(), EntitySpawnReason.STRUCTURE);
                    if(shulker != null){
                        shulker.moveTo(aboveTop.getX(), aboveTop.getY(), aboveTop.getZ(), random.nextFloat() * 360.0F, 0.0F);
                        shulker.finalizeSpawn(level.getLevel(), level.getCurrentDifficultyAt(aboveTop), EntitySpawnReason.STRUCTURE, null);
                        level.addFreshEntityWithPassengers(shulker);
                    }
                }
            });

            bottomBlocks.forEach(blockPos -> {
                if(random.nextFloat() < 0.0625F / 4.0F){
                    BlockPos belowBottom = blockPos.below();
                    Shulker shulker = EntityType.SHULKER.create(level.getLevel(), EntitySpawnReason.STRUCTURE);
                    if(shulker != null){
                        shulker.moveTo(belowBottom.getX(), belowBottom.getY(), belowBottom.getZ(), random.nextFloat() * 360.0F, 0.0F);
                        shulker.setXRot(180.0F);
                        shulker.finalizeSpawn(level.getLevel(), level.getCurrentDifficultyAt(belowBottom), EntitySpawnReason.STRUCTURE, null);
                        level.addFreshEntityWithPassengers(shulker);
                    }
                }
            });

        }

    }


    static class EndRuines implements ResourceIslandStructure.IslandDecorator {


        @Override
        public void decorateSpecific(List<BlockPos> topBlocks, List<BlockPos> bottomBlocks, WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, ChunkPos origin, Vec3i templateSize) {

            topBlocks.forEach(topPos -> {

                if (random.nextFloat() < 1.0F / (templateSize.getX() * templateSize.getZ()) * 10.0F) {
                    level.setBlock(topPos, Blocks.STONE_BRICKS.defaultBlockState(), Block.UPDATE_CLIENTS);
                    if(random.nextFloat() < 0.45F){
                        level.setBlock(topPos.above(), Blocks.STONE_BRICK_WALL.defaultBlockState(), Block.UPDATE_CLIENTS);
                        if(random.nextFloat() < 0.65F){
                            level.setBlock(topPos.above().above(), random.nextFloat() < 0.5F ? Blocks.LANTERN.defaultBlockState() : Blocks.STONE_BRICK_WALL.defaultBlockState(), Block.UPDATE_CLIENTS);
                        }
                    }
                }else if (random.nextFloat() < 0.0625 / 2.0F)
                        placeFeatureConfigured(EndFeatures.CHORUS_PLANT, level, chunkGenerator, random, topPos.above());


                if(random.nextFloat() < 0.0625F / 3.0F){
                    BlockPos aboveTop = topPos.above();
                    Shulker shulker = EntityType.SHULKER.create(level.getLevel(), EntitySpawnReason.STRUCTURE);
                    if(shulker != null){
                        shulker.moveTo(aboveTop.getX(), aboveTop.getY(), aboveTop.getZ(), random.nextFloat() * 360.0F, 0.0F);
                        shulker.finalizeSpawn(level.getLevel(), level.getCurrentDifficultyAt(aboveTop), EntitySpawnReason.STRUCTURE, null);
                        level.addFreshEntityWithPassengers(shulker);
                    }
                }
            });

            bottomBlocks.forEach(blockPos -> {
                if(random.nextFloat() < 0.0625F / 4.0F){
                    BlockPos belowBottom = blockPos.below();
                    Shulker shulker = EntityType.SHULKER.create(level.getLevel(), EntitySpawnReason.STRUCTURE);
                    if(shulker != null){
                        shulker.moveTo(belowBottom.getX(), belowBottom.getY(), belowBottom.getZ(), random.nextFloat() * 360.0F, 0.0F);
                        shulker.setXRot(180.0F);
                        shulker.finalizeSpawn(level.getLevel(), level.getCurrentDifficultyAt(belowBottom), EntitySpawnReason.STRUCTURE, null);
                        level.addFreshEntityWithPassengers(shulker);
                    }
                }
            });

        }
    }

}

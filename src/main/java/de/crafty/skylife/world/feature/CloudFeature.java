package de.crafty.skylife.world.feature;

import com.mojang.serialization.Codec;
import de.crafty.skylife.block.SmallCloudBlock;
import de.crafty.skylife.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class CloudFeature extends Feature<NoneFeatureConfiguration> {


    public CloudFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> featurePlaceContext) {
        Predicate<BlockState> canReplace = Feature.isReplaceable(BlockTags.FEATURES_CANNOT_REPLACE);

        WorldGenLevel worldGenLevel = featurePlaceContext.level();
        BlockPos origin = featurePlaceContext.origin();
        RandomSource randomSource = featurePlaceContext.random();

        boolean dense = randomSource.nextFloat() < 0.35F;

        BlockPos cloudPos = origin;

        for (int i = 0; i < randomSource.nextInt(15) + 1; i++) {
            int size = 2 + randomSource.nextInt(3);

            this.genCloudCube(dense, size, worldGenLevel, cloudPos, canReplace, origin);
            cloudPos = origin.offset(1 - size + randomSource.nextInt(size), -1 + randomSource.nextInt(3), 1 - size + randomSource.nextInt(size));
        }


        return true;
    }


    private void genCloudCube(boolean dense, int baseSize, WorldGenLevel worldGenLevel, BlockPos pos, Predicate<BlockState> canReplace, BlockPos origin) {
        BlockState cloud = dense ? BlockRegistry.DENSE_CLOUD.defaultBlockState() : BlockRegistry.CLOUD.defaultBlockState();

        baseSize = Math.max(baseSize, 2);
        int height = 1;

        int originChunkX = SectionPos.blockToSectionCoord(origin.getX());
        int originChunkZ = SectionPos.blockToSectionCoord(origin.getZ());

        List<BlockPos> cloudPositions = new ArrayList<>();
        boolean canGenerate = true;

        for (int x = 0; x < baseSize; x++) {
            for (int z = 0; z < baseSize; z++) {
                for (int y = 0; y < height; y++) {
                    BlockPos setPos = pos.offset(x, y, z);
                    int chunkX = SectionPos.blockToSectionCoord(setPos.getX());
                    int chunkZ = SectionPos.blockToSectionCoord(setPos.getZ());

                    if(chunkX > originChunkX + 1 || chunkZ > originChunkZ + 1 || chunkX < originChunkX - 1 || chunkZ < originChunkZ - 1) {
                        canGenerate = false;
                        break;
                    }

                    cloudPositions.add(setPos);
                }
            }
        }

        for (int x = -1; x < baseSize + 1; x++) {
            for (int z = -1; z < baseSize + 1; z++) {
                for (int y = -1; y < height + 1; y++) {
                    if (this.isEdge(x, z, -1, baseSize) ||
                            (x == -1 && y == height) || (z == -1 && y == height) || (x == baseSize && y == -1) || (z == baseSize && y == -1) ||
                            (x == -1 && y == -1) || (z == -1 && y == -1) || (x == baseSize && y == height) || (z == baseSize && y == height)
                    )
                        continue;


                    BlockPos setPos = pos.offset(x, y, z);
                    int chunkX = SectionPos.blockToSectionCoord(setPos.getX());
                    int chunkZ = SectionPos.blockToSectionCoord(setPos.getZ());

                    if(chunkX > originChunkX + 1 || chunkZ > originChunkZ + 1 || chunkX < originChunkX - 1 || chunkZ < originChunkZ - 1) {
                        canGenerate = false;
                        break;
                    }
                    cloudPositions.add(setPos);
                }
            }
        }

        if (!canGenerate)
            return;

        cloudPositions.forEach(pos1 -> {
            this.safeSetBlock(worldGenLevel, pos1, cloud, canReplace);
            for(Direction direction : Direction.values()) {
                BlockPos relative = pos1.relative(direction);
                if(!cloudPositions.contains(relative) && worldGenLevel.getBlockState(relative).isAir() && worldGenLevel.getRandom().nextFloat() < 0.3F) {
                    this.safeSetBlock(worldGenLevel, relative, BlockRegistry.SMALL_CLOUD.defaultBlockState().setValue(SmallCloudBlock.ATTACHED, direction.getOpposite()), canReplace);
                }
            }
        });
    }

    private boolean isEdge(int i, int j, int min, int max) {
        return (i == min && j == max) || (i == max && j == min) || (i == max && j == max) || (i == min && j == min);
    }

}

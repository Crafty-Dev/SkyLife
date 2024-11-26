package de.crafty.skylife.structure.resource_island;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.crafty.skylife.SkyLife;
import de.crafty.skylife.registry.StructureRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ResourceIslandStructure extends Structure {

    public static final RandomBlockProvider GRASS_BLOCK = randomSource -> Blocks.GRASS_BLOCK.defaultBlockState();
    public static final RandomBlockProvider DIRT = randomSource -> Blocks.DIRT.defaultBlockState();
    public static final RandomBlockProvider MUD = randomSource -> Blocks.MUD.defaultBlockState();

    public static final RandomBlockProvider STONE = randomSource -> {
        float f = randomSource.nextFloat();
        return f < 0.25F ? Blocks.COBBLESTONE.defaultBlockState() : Blocks.STONE.defaultBlockState();
    };



    public static final MapCodec<ResourceIslandStructure> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.STRING.fieldOf("resource_type").forGetter(resourceIslandStructure -> resourceIslandStructure.resourceType.name().toLowerCase()),
                    settingsCodec(instance)
            )
            .apply(instance, (s, structureSettings) -> new ResourceIslandStructure(ResourceType.valueOf(s.toUpperCase()), structureSettings)));


    private final ResourceType resourceType;

    protected ResourceIslandStructure(ResourceType resourceType, StructureSettings structureSettings) {
        super(structureSettings);

        this.resourceType = resourceType;
    }

    @Override
    protected @NotNull Optional<GenerationStub> findGenerationPoint(GenerationContext generationContext) {
        RandomSource randomSource = generationContext.random();
        int genY = 48 + randomSource.nextInt(22);
        int radius = 2 + randomSource.nextInt(4);
        Rotation rotation = Rotation.getRandom(randomSource);
        BlockPos genPos = new BlockPos(generationContext.chunkPos().getWorldPosition().getX(), genY, generationContext.chunkPos().getWorldPosition().getZ());
        return Optional.of(
                new GenerationStub(
                        genPos,
                        structurePiecesBuilder -> structurePiecesBuilder.addPiece(new ResourceIslandPiece(this.resourceType, randomSource, generationContext.structureTemplateManager(), ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "resource_island/default"), rotation, genPos))
                )
        );
    }


    @Override
    public @NotNull StructureType<?> type() {
        return StructureRegistry.RESOURCE_ISLAND;
    }


    @Override
    public void afterPlace(WorldGenLevel worldGenLevel, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource randomSource, BoundingBox boundingBox, ChunkPos chunkPos, PiecesContainer piecesContainer) {

        ResourceIslandPiece piece = (ResourceIslandPiece) piecesContainer.pieces().getFirst();

        //System.out.println(piecesContainer.pieces().size());
        List<BlockPos> topBlocks = new ArrayList<>();
        for(int x = boundingBox.minX(); x < boundingBox.maxX(); x++) {
            for(int z = boundingBox.minZ(); z < boundingBox.maxZ(); z++) {
                for(int y = boundingBox.maxY(); y >= boundingBox.minY(); y--) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if(piece.originBlocks.contains(pos)) {
                        topBlocks.add(pos);
                        break;
                    }
                }
            }
        }

        List<BlockPos> bottomBlocks = new ArrayList<>();
        for(int x = boundingBox.minX(); x < boundingBox.maxX(); x++) {
            for(int z = boundingBox.minZ(); z < boundingBox.maxZ(); z++) {
                for(int y = 0; y < boundingBox.maxY(); y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if(piece.originBlocks.contains(pos)) {
                        bottomBlocks.add(pos);
                        break;
                    }
                }
            }
        }

        this.resourceType.getDecorator().decorateSpecific(topBlocks, bottomBlocks, worldGenLevel, structureManager, chunkGenerator, randomSource, boundingBox, chunkPos, piece.template().getSize());

    }


    public enum ResourceType {
        OAK(GRASS_BLOCK, Blocks.DIRT.defaultBlockState(), STONE, STONE, new IslandDecorators.Oak()),
        BIRCH(GRASS_BLOCK, Blocks.DIRT.defaultBlockState(), STONE, STONE, new IslandDecorators.Birch()),
        SPRUCE (GRASS_BLOCK, Blocks.DIRT.defaultBlockState(), STONE, STONE, new IslandDecorators.Spruce()),
        DARK_OAK(GRASS_BLOCK, Blocks.DIRT.defaultBlockState(), STONE, STONE, new IslandDecorators.DarkOak()),
        JUNGLE(GRASS_BLOCK, Blocks.DIRT.defaultBlockState(), STONE, STONE, new IslandDecorators.Jungle()),
        ACACIA(GRASS_BLOCK, Blocks.DIRT.defaultBlockState(), STONE, STONE, new IslandDecorators.Acacia()),
        MANGROVE(MUD, Blocks.MUD.defaultBlockState(), STONE, STONE, new IslandDecorators.Mangrove());

        final RandomBlockProvider topBlock, bottomBlock, fillerBlock;
        final BlockState depthBlock;
        final IslandDecorator decorator;

        ResourceType(RandomBlockProvider topBlock, BlockState depthBlock, RandomBlockProvider botomBlock, RandomBlockProvider fillerBlock, IslandDecorator decorator) {
            this.topBlock = topBlock;
            this.bottomBlock = botomBlock;
            this.fillerBlock = fillerBlock;

            this.depthBlock = depthBlock;
            this.decorator = decorator;
        }

        public RandomBlockProvider topBlockProvider() {
            return this.topBlock;
        }

        public RandomBlockProvider bottomBlockProvider() {
            return this.bottomBlock;
        }

        public RandomBlockProvider fillerBlockProvider() {
            return this.fillerBlock;
        }


        public IslandDecorator getDecorator() {
            return this.decorator;
        }
    }

    public interface IslandDecorator {


        void decorateSpecific(List<BlockPos> topBlocks, List<BlockPos> bottomBlocks, WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox boundingBox, ChunkPos origin, Vec3i templateSize);

    }
}

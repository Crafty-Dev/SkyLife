package de.crafty.skylife.structure.resource_island;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.crafty.skylife.SkyLife;
import de.crafty.skylife.registry.BlockRegistry;
import de.crafty.skylife.registry.EntityRegistry;
import de.crafty.skylife.registry.StructureRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.EndGatewayConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ResourceIslandStructure extends Structure {

    private static final ResourceLocation DEFAULT_TEMPLATE = ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "resource_island/default");
    private static final ResourceLocation ORE_TEMPLATE = ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "resource_island/ore");

    public static final RandomBlockProvider NULL = randomSource -> null;
    public static final RandomBlockProvider GRASS_BLOCK = randomSource -> Blocks.GRASS_BLOCK.defaultBlockState();
    public static final RandomBlockProvider DIRT = randomSource -> Blocks.DIRT.defaultBlockState();
    public static final RandomBlockProvider SAND = randomSource -> Blocks.SAND.defaultBlockState();
    public static final RandomBlockProvider MUD = randomSource -> Blocks.MUD.defaultBlockState();


    public static final RandomBlockProvider STONE = randomSource -> {
        float f = randomSource.nextFloat();
        return f < 0.25F ? Blocks.COBBLESTONE.defaultBlockState() : Blocks.STONE.defaultBlockState();
    };

    public static final RandomBlockProvider OIL_STONE = randomSource -> {
        float f = randomSource.nextFloat();

        if (f < 0.125F)
            return BlockRegistry.OILY_STONE.defaultBlockState();

        return f < 0.25F ? Blocks.COBBLESTONE.defaultBlockState() : Blocks.STONE.defaultBlockState();
    };

    public static final RandomBlockProvider STONE_DRIPSTONE = randomSource -> randomSource.nextFloat() < 0.5F ? Blocks.DRIPSTONE_BLOCK.defaultBlockState() : Blocks.STONE.defaultBlockState();

    public static final RandomBlockProvider STONE_LUSH_CAVE = randomSource -> {
        float f = randomSource.nextFloat();

        if (f < 0.25F)
            return Blocks.CLAY.defaultBlockState();

        return f < 0.65F ? Blocks.MOSS_BLOCK.defaultBlockState() : Blocks.STONE.defaultBlockState();
    };


    //Nether
    public static final RandomBlockProvider NETHER_CRIMSON = randomSource -> Blocks.CRIMSON_NYLIUM.defaultBlockState();
    public static final RandomBlockProvider NETHER_WARPED = randomSource -> Blocks.WARPED_NYLIUM.defaultBlockState();
    public static final RandomBlockProvider NETHER_SOUL_SAND = randomSource -> {
        return randomSource.nextFloat() < 0.35F ? Blocks.SOUL_SOIL.defaultBlockState() : Blocks.SOUL_SAND.defaultBlockState();
    };

    public static final RandomBlockProvider NETHERRACK = randomSource -> {
        float f = randomSource.nextFloat();

        if (f < 0.0625F)
            return Blocks.NETHER_QUARTZ_ORE.defaultBlockState();
        else if (f < 0.125F)
            return Blocks.NETHER_GOLD_ORE.defaultBlockState();
        else
            return Blocks.NETHERRACK.defaultBlockState();
    };

    public static final RandomBlockProvider NETHER_MAGMA = randomSource -> {
        return randomSource.nextFloat() < 0.4F ? Blocks.MAGMA_BLOCK.defaultBlockState() : null;
    };


    public static final RandomBlockProvider NETHER_BASALT = randomSource -> {
        return randomSource.nextFloat() < 0.25F ? Blocks.BLACKSTONE.defaultBlockState() : Blocks.BASALT.defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y);
    };


    public static final RandomBlockProvider END = randomSource -> {
        if (randomSource.nextInt(64) == 0)
            return BlockRegistry.END_NETHERITE_ORE.defaultBlockState();
        if (randomSource.nextInt(32) == 0)
            return BlockRegistry.END_DIAMOND_ORE.defaultBlockState();

        return Blocks.END_STONE.defaultBlockState();
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

        Rotation rotation = Rotation.getRandom(randomSource);
        BlockPos genPos = new BlockPos(generationContext.chunkPos().getWorldPosition().getX(), genY, generationContext.chunkPos().getWorldPosition().getZ());
        return Optional.of(
                new GenerationStub(
                        genPos,
                        structurePiecesBuilder -> structurePiecesBuilder.addPiece(new ResourceIslandPiece(this.resourceType, randomSource, generationContext.structureTemplateManager(), this.resourceType.templateLocation, rotation, genPos))
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

        List<BlockPos> topBlocks = new ArrayList<>();
        for (int x = boundingBox.minX(); x <= boundingBox.maxX(); x++) {
            for (int z = boundingBox.minZ(); z <= boundingBox.maxZ(); z++) {
                for (int y = boundingBox.maxY(); y >= boundingBox.minY(); y--) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (piece.originBlocks.contains(pos)) {
                        topBlocks.add(pos);
                        break;
                    }
                }
            }
        }

        List<BlockPos> bottomBlocks = new ArrayList<>();
        for (int x = boundingBox.minX(); x < boundingBox.maxX(); x++) {
            for (int z = boundingBox.minZ(); z < boundingBox.maxZ(); z++) {
                for (int y = 0; y < boundingBox.maxY(); y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (piece.originBlocks.contains(pos)) {
                        bottomBlocks.add(pos);
                        break;
                    }
                }
            }
        }

        this.resourceType.getDecorator().decorateSpecific(topBlocks, bottomBlocks, worldGenLevel, structureManager, chunkGenerator, randomSource, boundingBox, chunkPos, piece.template().getSize());

        if (this.resourceType == ResourceType.END_CHORUS || this.resourceType == ResourceType.END_RUINES) {
            if (randomSource.nextFloat() < 0.05F / 2.0F)
                Feature.END_GATEWAY.place(EndGatewayConfiguration.delayedExitSearch(), worldGenLevel, worldGenLevel.getLevel().getChunkSource().getGenerator(), randomSource, new BlockPos(randomSource.nextBoolean() ? (boundingBox.minX() + 1) : (boundingBox.maxX() - 1), piece.getLocatorPosition().getY() + 15, randomSource.nextBoolean() ? (boundingBox.minZ() + 1) : (boundingBox.maxZ() - 1)));

        }

        if (topBlocks.isEmpty())
            return;

        MobSpawnSettings mobSpawnSettings = worldGenLevel.getBiome(piece.getLocatorPosition()).value().getMobSettings();
        WeightedRandomList<MobSpawnSettings.SpawnerData> availableCreatures = mobSpawnSettings.getMobs(MobCategory.CREATURE);
        if (availableCreatures.isEmpty())
            return;

        if (randomSource.nextFloat() < 0.75F) {
            Optional<MobSpawnSettings.SpawnerData> optional = availableCreatures.getRandom(randomSource);
            if (optional.isEmpty())
                return;

            MobSpawnSettings.SpawnerData spawnerData = optional.get();

            SpawnGroupData spawnGroupData = null;

            int min = 1;
            int max = Mth.clamp(spawnerData.maxCount, min, 3);

            int amount = min + randomSource.nextInt(1 + max - min);

            for (int i = 0; i < amount; i++) {
                boolean success = false;

                for (int j = 0; !success && j < 4; j++) {

                    BlockPos spawnPos = topBlocks.get(randomSource.nextInt(topBlocks.size())).above();
                    if (!spawnerData.type.canSummon())
                        break;

                    int tries = 0;
                    while (!SpawnPlacements.isSpawnPositionOk(spawnerData.type, worldGenLevel, spawnPos) && tries < 5) {
                        spawnPos = topBlocks.get(randomSource.nextInt(topBlocks.size())).above();
                        tries++;
                    }

                    if (!worldGenLevel.noCollision(spawnerData.type.getSpawnAABB(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ())) || !SpawnPlacements.checkSpawnRules(spawnerData.type, worldGenLevel, EntitySpawnReason.STRUCTURE, spawnPos, worldGenLevel.getRandom())) {
                        continue;
                    }


                    Entity entity = spawnerData.type.create(worldGenLevel.getLevel(), EntitySpawnReason.STRUCTURE);

                    if (entity == null)
                        continue;

                    entity.moveTo(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), randomSource.nextFloat() * 360.0F, 0.0F);
                    if (entity instanceof Mob mob) {
                        if (mob.checkSpawnRules(worldGenLevel, EntitySpawnReason.STRUCTURE) && mob.checkSpawnObstruction(worldGenLevel)) {
                            spawnGroupData = mob.finalizeSpawn(worldGenLevel, worldGenLevel.getCurrentDifficultyAt(mob.blockPosition()), EntitySpawnReason.STRUCTURE, spawnGroupData);
                            worldGenLevel.addFreshEntityWithPassengers(mob);
                            success = true;
                        }
                    }


                }
            }

        }

        if (this.resourceType != ResourceType.OIL)
            return;

        if (piece.hasCheckedSheep() || !piece.shouldSpawnSheeps())
            return;

        piece.setCheckedSheep(true);
        SpawnGroupData spawnGroupData = null;

        int amount = 1 + randomSource.nextInt(2);
        for (int i = 0; i < amount; i++) {

            BlockPos spawnPos = topBlocks.get(randomSource.nextInt(topBlocks.size())).above();
            int tries = 0;
            while (!SpawnPlacements.isSpawnPositionOk(EntityRegistry.OIL_SHEEP, worldGenLevel, spawnPos) && tries < 5) {
                spawnPos = topBlocks.get(randomSource.nextInt(topBlocks.size())).above();
                tries++;
            }

            Mob sheep = EntityRegistry.OIL_SHEEP.create(worldGenLevel.getLevel(), EntitySpawnReason.STRUCTURE);
            if (sheep == null)
                return;

            sheep.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, randomSource.nextFloat() * 360.0F, 0.0F);
            spawnGroupData = sheep.finalizeSpawn(worldGenLevel, worldGenLevel.getCurrentDifficultyAt(sheep.blockPosition()), EntitySpawnReason.STRUCTURE, spawnGroupData);
            worldGenLevel.addFreshEntityWithPassengers(sheep);

        }
    }


    public enum ResourceType {
        CHERRY(GRASS_BLOCK, Blocks.DIRT.defaultBlockState(), NULL, STONE, new IslandDecorators.Cherry()),
        OAK(GRASS_BLOCK, Blocks.DIRT.defaultBlockState(), NULL, STONE, new IslandDecorators.Oak()),
        BIRCH(GRASS_BLOCK, Blocks.DIRT.defaultBlockState(), NULL, STONE, new IslandDecorators.Birch()),
        SPRUCE(GRASS_BLOCK, Blocks.DIRT.defaultBlockState(), NULL, STONE, new IslandDecorators.Spruce()),
        DARK_OAK(GRASS_BLOCK, Blocks.DIRT.defaultBlockState(), NULL, STONE, new IslandDecorators.DarkOak()),
        JUNGLE(GRASS_BLOCK, Blocks.DIRT.defaultBlockState(), NULL, STONE, new IslandDecorators.Jungle()),
        ACACIA(GRASS_BLOCK, Blocks.DIRT.defaultBlockState(), NULL, STONE, new IslandDecorators.Acacia()),
        MANGROVE(MUD, Blocks.MUD.defaultBlockState(), NULL, STONE, new IslandDecorators.Mangrove()),
        DESERT(SAND, Blocks.SANDSTONE.defaultBlockState(), NULL, STONE, new IslandDecorators.Desert()),
        DRIPSTONE(NULL, null, NULL, STONE_DRIPSTONE, new IslandDecorators.Dripstone(), ORE_TEMPLATE),
        LUSH_CAVE(NULL, null, NULL, STONE_LUSH_CAVE, new IslandDecorators.LushCave(), ORE_TEMPLATE),

        CRIMSON(NETHER_CRIMSON, null, NETHER_MAGMA, NETHERRACK, new IslandDecorators.NetherCrimson()),
        WARPED(NETHER_WARPED, null, NETHER_MAGMA, NETHERRACK, new IslandDecorators.NetherWarped()),
        SOULSAND(NETHER_SOUL_SAND, Blocks.SOUL_SAND.defaultBlockState(), NULL, NETHERRACK, new IslandDecorators.NetherSoulSand(), ORE_TEMPLATE),
        BASALT(NULL, null, NULL, NETHER_BASALT, new IslandDecorators.NetherBasalt(), ORE_TEMPLATE),
        WASTES(NULL, null, NETHER_MAGMA, NETHERRACK, new IslandDecorators.NetherWastes()),

        END_CHORUS(NULL, null, NULL, END, new IslandDecorators.EndChorus()),
        END_RUINES(NULL, null, NULL, END, new IslandDecorators.EndRuines()),

        OIL(NULL, null, NULL, OIL_STONE, new IslandDecorators.Oil(), ORE_TEMPLATE);

        final RandomBlockProvider topBlock, bottomBlock, fillerBlock;
        final BlockState depthBlock;
        final IslandDecorator decorator;
        final ResourceLocation templateLocation;

        ResourceType(RandomBlockProvider topBlock, BlockState depthBlock, RandomBlockProvider bottomBlock, RandomBlockProvider fillerBlock, IslandDecorator decorator, ResourceLocation templateLocation) {
            this.topBlock = topBlock;
            this.bottomBlock = bottomBlock;
            this.fillerBlock = fillerBlock;

            this.depthBlock = depthBlock;
            this.decorator = decorator;
            this.templateLocation = templateLocation;
        }

        ResourceType(RandomBlockProvider topBlock, BlockState depthBlock, RandomBlockProvider bottomBlock, RandomBlockProvider fillerBlock, IslandDecorator decorator) {
            this(topBlock, depthBlock, bottomBlock, fillerBlock, decorator, DEFAULT_TEMPLATE);
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

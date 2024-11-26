package de.crafty.skylife.structure.resource_island;

import de.crafty.skylife.registry.StructureRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.ScatteredFeaturePiece;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.ArrayList;
import java.util.List;

public class ResourceIslandPiece extends TemplateStructurePiece {


    private final ResourceIslandStructure.ResourceType resourceType;
    protected final List<BlockPos> originBlocks = new ArrayList<>();

    public ResourceIslandPiece(ResourceIslandStructure.ResourceType resourceType, RandomSource randomSource, StructureTemplateManager structureTemplateManager, ResourceLocation resourceLocation, Rotation rotation, BlockPos blockPos) {
        super(StructureRegistry.Pieces.RESOURCE_ISLAND, 0, structureTemplateManager, resourceLocation, resourceLocation.toString(), new StructurePlaceSettings().setMirror(Mirror.NONE).setRotation(rotation).setRotationPivot(new BlockPos(0, 0, 0)).addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK), blockPos);

        this.resourceType = resourceType;
    }

    public ResourceIslandPiece(StructureTemplateManager templateManager, CompoundTag tag) {
        super(StructureRegistry.Pieces.RESOURCE_ISLAND, tag, templateManager, resourceLocation -> new StructurePlaceSettings().setMirror(Mirror.NONE).setRotation(Rotation.valueOf(tag.getString("Rot"))).setRotationPivot(new BlockPos(0, 0, 0)).addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK));

        this.resourceType = ResourceIslandStructure.ResourceType.valueOf(tag.getString("resource_type").toUpperCase());
    }


    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext structurePieceSerializationContext, CompoundTag compoundTag) {
        super.addAdditionalSaveData(structurePieceSerializationContext, compoundTag);
        compoundTag.putString("Rot", this.placeSettings.getRotation().name());
        compoundTag.putString("resource_type", resourceType.name().toLowerCase());
    }

    @Override
    public void postProcess(WorldGenLevel worldGenLevel, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource randomSource, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
        super.postProcess(worldGenLevel, structureManager, chunkGenerator, randomSource, boundingBox, chunkPos, blockPos);

        int templateWidth = this.template().getSize().getX();
        int templateDepth = this.template().getSize().getZ();

        //Pre-process emerald blocks
        for (int x = 0; x <= this.template().getSize().getX(); x++) {
            for (int z = 0; z <= this.template().getSize().getZ(); z++) {
                for (int y = 0; y <= this.template().getSize().getY(); y++) {
                    BlockState state = this.getBlock(worldGenLevel, x, y, z, boundingBox);
                    if (state.is(Blocks.EMERALD_BLOCK)) {
                        this.placeBlock(worldGenLevel, randomSource.nextFloat() < 0.5F ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.GLASS.defaultBlockState(), x, y, z, boundingBox);
                    }
                }
            }
        }

        //Process gold blocks
        //ATTENTION: Pay attention when replacing with temporary blocks like glass or obsidian, emerald, etc...
        //Do that in afterPlace method of structure
        for (int x = 0; x <= this.template().getSize().getX(); x++) {
            for (int z = 0; z <= this.template().getSize().getZ(); z++) {
                for (int y = 0; y <= this.template().getSize().getY(); y++) {
                    BlockState state = this.getBlock(worldGenLevel, x, y, z, boundingBox);
                    if (state.is(Blocks.GOLD_BLOCK)) {

                        boolean attachedObsidian = false;
                        boolean attachedGlass = false;

                        for (Direction direction : Direction.values()) {
                            int attachedX = x + direction.getStepX();
                            int attachedZ = z + direction.getStepZ();
                            int attachedY = y + direction.getStepY();

                            BlockState attached = this.getBlock(worldGenLevel, attachedX, attachedY, attachedZ, boundingBox);
                            if (attached.is(Blocks.OBSIDIAN))
                                attachedObsidian = true;

                            if (attached.is(Blocks.GLASS))
                                attachedGlass = true;
                        }

                        if ((attachedObsidian || !attachedGlass) && randomSource.nextFloat() < 0.5F)
                            this.placeBlock(worldGenLevel, Blocks.BEDROCK.defaultBlockState(), x, y, z, boundingBox);
                        else
                            this.placeBlock(worldGenLevel, Blocks.AIR.defaultBlockState(), x, y, z, boundingBox);
                    }
                }
            }
        }

        //Process emerald blocks
        for (int x = 0; x <= this.template().getSize().getX(); x++) {
            for (int z = 0; z <= this.template().getSize().getZ(); z++) {
                for (int y = 0; y <= this.template().getSize().getY(); y++) {
                    BlockState state = this.getBlock(worldGenLevel, x, y, z, boundingBox);
                    if (state.is(Blocks.OBSIDIAN))
                        this.placeBlock(worldGenLevel, Blocks.BEDROCK.defaultBlockState(), x, y, z, boundingBox);

                    if (state.is(Blocks.GLASS))
                        this.placeBlock(worldGenLevel, Blocks.AIR.defaultBlockState(), x, y, z, boundingBox);

                }
            }
        }

        //Process bedrock
        for (int x = 0; x <= this.template().getSize().getX(); x++) {
            for (int z = 0; z <= this.template().getSize().getZ(); z++) {
                for (int y = 0; y <= this.template().getSize().getY(); y++) {
                    BlockState state = this.getBlock(worldGenLevel, x, y, z, boundingBox);
                    if (state.is(Blocks.BEDROCK))
                        this.placeBlock(worldGenLevel, this.resourceType.fillerBlockProvider().randomBlock(randomSource), x, y, z, boundingBox);

                }
            }
        }


        List<BlockPos> allBlocks = new ArrayList<>();
        for(int x = 0; x <= this.template().getSize().getX(); x++) {
            for(int z = 0; z < this.template().getSize().getZ(); z++) {
                for(int y = 0; y <= this.template().getSize().getY(); y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if(!this.getBlock(worldGenLevel, x, y, z, boundingBox).isAir())
                        allBlocks.add(pos);
                }
            }
        }

        this.originBlocks.clear();
        allBlocks.forEach(pos -> this.originBlocks.add(this.getWorldPos(pos.getX(), pos.getY(), pos.getZ())));

        List<BlockPos> topBlocks = new ArrayList<>();

        for (int x = 0; x <= this.template().getSize().getX(); x++) {
            for (int z = 0; z <= this.template().getSize().getZ(); z++) {
                for (int y = this.template().getSize().getY(); y >= 0; y--) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (allBlocks.contains(pos)) {
                        topBlocks.add(new BlockPos(x, y, z));
                        break;
                    }
                }
            }
        }

        topBlocks.forEach(topPos -> {
            BlockState belowBlock = this.getBlock(worldGenLevel, topPos.getX(), topPos.getY() - 1, topPos.getZ(), boundingBox);
            BlockState belowBelowBlock = this.getBlock(worldGenLevel, topPos.getX(), topPos.getY() - 2, topPos.getZ(), boundingBox);
            BlockState belowBelowBelowBlock = this.getBlock(worldGenLevel, topPos.getX(), topPos.getY() - 3, topPos.getZ(), boundingBox);

            if (!belowBlock.is(Blocks.AIR))
                this.placeBlock(worldGenLevel,this.resourceType.topBlockProvider().randomBlock(randomSource), topPos.getX(), topPos.getY(), topPos.getZ(), boundingBox);

            BlockState depthBlock = this.resourceType.depthBlock;
            if(depthBlock == null)
                return;

            if (topPos.getY() >= 1 && !belowBlock.is(Blocks.AIR) && !belowBelowBlock.is(Blocks.AIR) && randomSource.nextFloat() < 0.75F) {
                this.placeBlock(worldGenLevel, depthBlock, topPos.getX(), topPos.getY() - 1, topPos.getZ(), boundingBox);

                if (topPos.getY() >= 2 && !belowBelowBlock.is(Blocks.AIR) && !belowBelowBelowBlock.is(Blocks.AIR) && randomSource.nextFloat() < 0.65F)
                    this.placeBlock(worldGenLevel, depthBlock, topPos.getX(), topPos.getY() - 2, topPos.getZ(), boundingBox);
            }

        });

    }

    @Override
    protected void handleDataMarker(String string, BlockPos blockPos, ServerLevelAccessor serverLevelAccessor, RandomSource randomSource, BoundingBox boundingBox) {

    }
}

package de.crafty.skylife.blockentities;

import de.crafty.skylife.block.MeltingBlock;
import de.crafty.skylife.config.SkyLifeConfigs;
import de.crafty.skylife.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MeltingBlockEntity extends BlockEntity {


    public static final float BASE_MELTING_TIME = 20 * 20;
    private float progression;
    private final float baseEfficiency = 1.0F;

    //Used for rendering
    //Without this variable, the game wouldn't replace the block smoothly
    public MeltingBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.MELTING_BLOCK, pos, state);
    }


    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registryLookup) {
        this.progression = tag.getFloat("progression");

    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registryLookup) {
        super.saveAdditional(tag, registryLookup);
        tag.putFloat("progression", this.progression);
    }


    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        CompoundTag tag = new CompoundTag();
        this.saveAdditional(tag, registryLookup);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void increaseProgression(float amount) {
        this.progression += amount;
        this.setChanged();
    }

    public void decreaseProgression(float amount) {
        this.progression -= amount;
        this.setChanged();
    }


    public float currentProgression() {
        return this.progression;
    }

    public float getMeltingTime() {
        return BASE_MELTING_TIME;
    }

    public float getProgress() {
        return Math.min(this.progression / BASE_MELTING_TIME, 1.0F);
    }


    public static void tick(Level world, BlockPos pos, BlockState state, MeltingBlockEntity blockEntity) {

        MeltingBlock meltingBlock = (MeltingBlock) state.getBlock();
        BlockState below = world.getBlockState(pos.below());

        float heatEfficiency = SkyLifeConfigs.BLOCK_MELTING.getHeatEfficiencyForBlock(meltingBlock.getBlockType(), below, pos, world);
        if (heatEfficiency > 0.0F)
            blockEntity.increaseProgression((blockEntity.baseEfficiency * heatEfficiency) / (world.getBlockState(pos.above()).getBlock() == Blocks.WATER ? 2.0F : 1.0F));

        if (heatEfficiency == 0.0F && blockEntity.currentProgression() >= 0)
            blockEntity.decreaseProgression(1);


        if (blockEntity.currentProgression() < 0) {
            world.setBlockAndUpdate(pos, meltingBlock.getBlockType().defaultBlockState());
            world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.25F, 2.6F, true);
        }

        if (blockEntity.currentProgression() >= BASE_MELTING_TIME) {
            world.setBlockAndUpdate(pos, SkyLifeConfigs.BLOCK_MELTING.getMeltingResultForBlock(meltingBlock.getBlockType()).defaultFluidState().createLegacyBlock());
            world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BUCKET_FILL_LAVA, SoundSource.BLOCKS, 1.0F, 1.0F, true);

        }
    }
}

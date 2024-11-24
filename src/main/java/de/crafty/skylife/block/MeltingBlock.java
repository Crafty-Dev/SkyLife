package de.crafty.skylife.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.crafty.skylife.blockentities.MeltingBlockEntity;
import de.crafty.skylife.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MeltingBlock extends BaseEntityBlock {

    public static final MapCodec<MeltingBlock> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(Block.CODEC.fieldOf("blockType").forGetter(MeltingBlock::getBlockType), propertiesCodec())
                    .apply(instance, MeltingBlock::new));

    private final Block blockType;

    public MeltingBlock(Block blockType, Properties settings) {
        super(settings);

        this.blockType = blockType;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MeltingBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, BlockEntityRegistry.MELTING_BLOCK, MeltingBlockEntity::tick);
    }

    public Block getBlockType() {
        return this.blockType;
    }

}

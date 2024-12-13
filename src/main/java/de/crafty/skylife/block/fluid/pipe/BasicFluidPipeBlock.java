package de.crafty.skylife.block.fluid.pipe;

import com.mojang.serialization.MapCodec;
import de.crafty.lifecompat.api.fluid.logistic.pipe.AbstractFluidPipeBlockEntity;
import de.crafty.skylife.blockentities.fluid.BasicFluidPipeBlockEntity;
import de.crafty.skylife.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BasicFluidPipeBlock extends SkyLifeFluidPipe {

    public static final MapCodec<BasicFluidPipeBlock> CODEC = simpleCodec(BasicFluidPipeBlock::new);

    public BasicFluidPipeBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BasicFluidPipeBlockEntity(blockPos, blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, BlockEntityRegistry.BASIC_FLUID_PIPE, AbstractFluidPipeBlockEntity::tick);
    }
}

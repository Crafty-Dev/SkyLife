package de.crafty.skylife.blockentities.fluid;

import de.crafty.lifecompat.api.fluid.logistic.pipe.AbstractFluidPipeBlockEntity;
import de.crafty.skylife.block.fluid.pipe.SkyLifeFluidPipe;
import de.crafty.skylife.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BasicFluidPipeBlockEntity extends AbstractFluidPipeBlockEntity {


    public BasicFluidPipeBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityRegistry.BASIC_FLUID_PIPE, blockPos, blockState, SkyLifeFluidPipe.Tier.BASIC.getBufferSize());
    }

    @Override
    public int getExtractionRate() {
        return SkyLifeFluidPipe.Tier.BASIC.getMaxInOut();
    }

    @Override
    public int getInsertionRate() {
        return SkyLifeFluidPipe.Tier.BASIC.getMaxInOut();
    }
}

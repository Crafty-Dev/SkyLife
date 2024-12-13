package de.crafty.skylife.blockentities.fluid;

import de.crafty.lifecompat.fluid.blockentity.AbstractFluidContainerBlockEntity;
import de.crafty.lifecompat.util.FluidUnitConverter;
import de.crafty.skylife.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

//TODO find out why fluid tank does not continue filling after it has been full for once
public class BasicFluidStorageBlockEntity extends AbstractFluidContainerBlockEntity {

    public BasicFluidStorageBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityRegistry.BASIC_FLUID_STORAGE, blockPos, blockState, FluidUnitConverter.buckets(8.0F));
    }


    @Override
    public boolean canDrainLiquid(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    @Override
    public boolean canFillLiquid(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return true;
    }
}

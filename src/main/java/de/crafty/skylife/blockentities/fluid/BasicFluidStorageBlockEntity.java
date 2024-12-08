package de.crafty.skylife.blockentities.fluid;

import de.crafty.lifecompat.fluid.blockentity.AbstractFluidContainerBlockEntity;
import de.crafty.lifecompat.util.FluidUnitConverter;
import de.crafty.skylife.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BasicFluidStorageBlockEntity extends AbstractFluidContainerBlockEntity {

    public BasicFluidStorageBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityRegistry.BASIC_FLUID_STORAGE, blockPos, blockState, FluidUnitConverter.buckets(8.0F));
    }




}

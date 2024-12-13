package de.crafty.skylife.blockentities.machines.integrated;

import de.crafty.lifecompat.energy.block.BaseEnergyBlock;
import de.crafty.lifecompat.fluid.blockentity.AbstractFluidEnergyConsumerBlockEntity;
import de.crafty.lifecompat.util.FluidUnitConverter;
import de.crafty.skylife.block.machines.integrated.FluidPumpBlock;
import de.crafty.skylife.registry.BlockEntityRegistry;
import de.crafty.skylife.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;

import java.util.ArrayList;
import java.util.List;

public class FluidPumpBlockEntity extends AbstractFluidEnergyConsumerBlockEntity {

    private int suckingProgress, totalSuckingTime;

    public FluidPumpBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityRegistry.FLUID_PUMP, blockPos, blockState, BlockRegistry.FLUID_PUMP.getCapacity(), FluidUnitConverter.buckets(6.0F));

        //10 seconds
        this.totalSuckingTime = 20 * 10;
    }

    @Override
    public boolean isAccepting(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    @Override
    public boolean isConsuming(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return serverLevel.getFluidState(blockPos.below()).isSource();
    }

    @Override
    public int getMaxInput(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return 120;
    }

    @Override
    public int getConsumptionPerTick(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return 80;
    }

    @Override
    public boolean canDrainLiquid(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    @Override
    public boolean canFillLiquid(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return false;
    }


    public int getSuckingProgress() {
        return this.suckingProgress;
    }

    public void setTotalSuckingTime(int totalSuckingTime) {
        this.totalSuckingTime = totalSuckingTime;
        this.setChanged();
    }

    public int getTotalSuckingTime() {
        return this.totalSuckingTime;
    }

    @Override
    public List<Direction> getInputDirections(ServerLevel world, BlockPos pos, BlockState state) {
        List<Direction> directions = new ArrayList<>();

        for (Direction side : Direction.values()) {
            DirectionProperty facingProp = state.hasProperty(BaseEnergyBlock.FACING) ? BaseEnergyBlock.FACING : state.hasProperty(BaseEnergyBlock.HORIZONTAL_FACING) ? BaseEnergyBlock.HORIZONTAL_FACING : null;
            EnumProperty<BaseEnergyBlock.IOMode> sideMode = BaseEnergyBlock.calculateIOSide(facingProp != null ? state.getValue(facingProp) : Direction.NORTH, side);

            if (state.hasProperty(sideMode) && state.getValue(sideMode).isInput())
                directions.add(side);
        }

        return directions;
    }

    @Override
    protected void performAction(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {

        this.suckingProgress++;
        this.setChanged();

        if (this.suckingProgress >= this.totalSuckingTime) {
            this.suckingProgress = 0;
            FluidState liquidBelow = serverLevel.getFluidState(blockPos.below());
            if (liquidBelow.isSource()){
                this.fillWithLiquid(serverLevel, blockPos, blockState, liquidBelow.getType(), FluidUnitConverter.buckets(1.0F));
                serverLevel.setBlock(blockPos.below(), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            }

            this.setChanged();
        }

    }

    public static void tick(Level level, BlockPos pos, BlockState state, FluidPumpBlockEntity blockEntity) {
        if (level.isClientSide())
            return;

        //System.out.println(blockEntity.getInputDirections((ServerLevel) level, pos, state));

        if (!level.getFluidState(pos.below()).isSource() && blockEntity.suckingProgress > 0) {
            blockEntity.suckingProgress = 0;
            blockEntity.setChanged();
        }

        if(state.getValue(FluidPumpBlock.ENERGY) && blockEntity.getStoredEnergy() < blockEntity.getConsumptionPerTick((ServerLevel) level, pos, state))
            level.setBlock(pos, state.setValue(FluidPumpBlock.ENERGY, false), Block.UPDATE_CLIENTS);

        if(!state.getValue(FluidPumpBlock.ENERGY) && blockEntity.getStoredEnergy() >= blockEntity.getConsumptionPerTick((ServerLevel) level, pos, state))
            level.setBlock(pos, state.setValue(FluidPumpBlock.ENERGY, true), Block.UPDATE_CLIENTS);

        blockEntity.energyTick((ServerLevel) level, pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);

        tag.putInt("suckingProgress", this.suckingProgress);
        tag.putInt("totalSuckingTime", this.totalSuckingTime);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);

        this.suckingProgress = tag.getInt("suckingProgress");
        this.totalSuckingTime = tag.getInt("totalSuckingTime");
    }
}

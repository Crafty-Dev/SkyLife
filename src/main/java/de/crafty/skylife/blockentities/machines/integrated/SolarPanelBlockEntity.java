package de.crafty.skylife.blockentities.machines.integrated;

import de.crafty.lifecompat.api.energy.provider.AbstractEnergyProvider;
import de.crafty.lifecompat.energy.block.BaseEnergyBlock;
import de.crafty.skylife.block.machines.integrated.BriquetteGeneratorBlock;
import de.crafty.skylife.block.machines.integrated.SolarPanelBlock;
import de.crafty.skylife.registry.BlockEntityRegistry;
import de.crafty.skylife.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import java.util.ArrayList;
import java.util.List;

public class SolarPanelBlockEntity extends AbstractEnergyProvider {

    private boolean upgraded;

    public SolarPanelBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityRegistry.SOLAR_PANEL, blockPos, blockState, BlockRegistry.SOLAR_PANEL.getCapacity());
    }

    @Override
    public boolean isTransferring(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    @Override
    public boolean isGenerating(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return serverLevel.canSeeSky(blockPos) && serverLevel.isDay();
    }

    @Override
    public int getMaxOutput(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return 120;
    }

    @Override
    public int getGenerationPerTick(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return this.isUpgraded() ? 80 : 40;
    }


    public boolean isUpgraded() {
        return this.upgraded;
    }

    public void setUpgraded(boolean upgraded) {
        this.upgraded = upgraded;
        this.setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);

        tag.putBoolean("upgraded", this.upgraded);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);

        this.upgraded = tag.getBoolean("upgraded");
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, SolarPanelBlockEntity blockEntity) {
        if (level.isClientSide())
            return;

        if (blockState.getValue(SolarPanelBlock.ACTIVE) && !blockEntity.isGenerating((ServerLevel) level, blockPos, blockState))
            level.setBlock(blockPos, blockState.setValue(SolarPanelBlock.ACTIVE, false), SolarPanelBlock.UPDATE_CLIENTS);

        if (!blockState.getValue(SolarPanelBlock.ACTIVE) && blockEntity.isGenerating((ServerLevel) level, blockPos, blockState))
            level.setBlock(blockPos, blockState.setValue(SolarPanelBlock.ACTIVE, true), SolarPanelBlock.UPDATE_CLIENTS);

        blockEntity.energyTick((ServerLevel) level, blockPos, blockState);
    }
}

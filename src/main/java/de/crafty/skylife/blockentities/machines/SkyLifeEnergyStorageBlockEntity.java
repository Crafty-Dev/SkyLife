package de.crafty.skylife.blockentities.machines;

import de.crafty.lifecompat.energy.blockentity.SimpleEnergyStorageBlockEntity;
import de.crafty.skylife.block.machines.SkyLifeEnergyStorageBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class SkyLifeEnergyStorageBlockEntity extends SimpleEnergyStorageBlockEntity {

    private final SkyLifeEnergyStorageBlock.Tier tier;

    public SkyLifeEnergyStorageBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState, SkyLifeEnergyStorageBlock.Tier tier) {
        super(blockEntityType, blockPos, blockState, tier.getCapacity());

        this.tier = tier;
    }

    public SkyLifeEnergyStorageBlock.Tier getTier() {
        return this.tier;
    }

    @Override
    public int getMaxInput(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return this.tier.getMaxIO();
    }

    @Override
    public int getMaxOutput(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return this.tier.getMaxIO();
    }
}

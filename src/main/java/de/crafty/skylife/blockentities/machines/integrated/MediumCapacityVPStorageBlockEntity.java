package de.crafty.skylife.blockentities.machines.integrated;

import de.crafty.skylife.block.machines.SkyLifeEnergyStorageBlock;
import de.crafty.skylife.blockentities.machines.SkyLifeEnergyStorageBlockEntity;
import de.crafty.skylife.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public class MediumCapacityVPStorageBlockEntity extends SkyLifeEnergyStorageBlockEntity {

    public MediumCapacityVPStorageBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityRegistry.MC_VP_STORAGE, blockPos, blockState, SkyLifeEnergyStorageBlock.Tier.MEDIUM);
    }

    @Override
    public int getMaxInput(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return SkyLifeEnergyStorageBlock.Tier.MEDIUM.getMaxIO();
    }

    @Override
    public int getMaxOutput(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return SkyLifeEnergyStorageBlock.Tier.MEDIUM.getMaxIO();
    }
}

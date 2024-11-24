package de.crafty.skylife.blockentities.machines.integrated;

import de.crafty.skylife.block.machines.SkyLifeEnergyStorageBlock;
import de.crafty.skylife.blockentities.machines.SkyLifeEnergyStorageBlockEntity;
import de.crafty.skylife.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class LowCapacityVPStorageBlockEntity extends SkyLifeEnergyStorageBlockEntity {

    public LowCapacityVPStorageBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityRegistry.LC_VP_STORAGE, blockPos, blockState, SkyLifeEnergyStorageBlock.Tier.LOW);
    }

}

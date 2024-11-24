package de.crafty.skylife.blockentities.machines.integrated;

import de.crafty.lifecompat.api.energy.cable.AbstractEnergyCableBlockEntity;
import de.crafty.skylife.block.machines.SkyLifeEnergyCable;
import de.crafty.skylife.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public class ImprovedEnergyCableBlockEntity extends AbstractEnergyCableBlockEntity {

    public ImprovedEnergyCableBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityRegistry.IMPROVED_ENERGY_CABLE, blockPos, blockState, SkyLifeEnergyCable.Tier.IMPROVED.getCapacity());
    }

    @Override
    public int getMaxInput(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return SkyLifeEnergyCable.Tier.IMPROVED.getMaxIO();
    }

    @Override
    public int getMaxOutput(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return SkyLifeEnergyCable.Tier.IMPROVED.getMaxIO();
    }

}

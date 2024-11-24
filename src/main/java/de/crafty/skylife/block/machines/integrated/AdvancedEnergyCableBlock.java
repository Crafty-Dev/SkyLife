package de.crafty.skylife.block.machines.integrated;

import com.mojang.serialization.MapCodec;
import de.crafty.lifecompat.api.energy.cable.AbstractEnergyCableBlockEntity;
import de.crafty.skylife.block.machines.SkyLifeEnergyCable;
import de.crafty.skylife.blockentities.machines.integrated.AdvancedEnergyCableBlockEntity;
import de.crafty.skylife.blockentities.machines.integrated.ImprovedEnergyCableBlockEntity;
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

public class AdvancedEnergyCableBlock extends SkyLifeEnergyCable {

    public static final MapCodec<AdvancedEnergyCableBlock> CODEC = simpleCodec(AdvancedEnergyCableBlock::new);

    public AdvancedEnergyCableBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new AdvancedEnergyCableBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, BlockEntityRegistry.ADVANCED_ENERGY_CABLE, AbstractEnergyCableBlockEntity::tick);
    }
}

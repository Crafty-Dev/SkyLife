package de.crafty.skylife.block.machines.integrated;

import com.mojang.serialization.MapCodec;
import de.crafty.lifecompat.energy.blockentity.SimpleEnergyStorageBlockEntity;
import de.crafty.lifecompat.util.EnergyUnitConverter;
import de.crafty.skylife.block.machines.SkyLifeEnergyStorageBlock;
import de.crafty.skylife.blockentities.machines.integrated.MediumCapacityVPStorageBlockEntity;
import de.crafty.skylife.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MediumCapacityVPStorageBlock extends SkyLifeEnergyStorageBlock {

    public static final MapCodec<MediumCapacityVPStorageBlock> CODEC = simpleCodec(MediumCapacityVPStorageBlock::new);

    public MediumCapacityVPStorageBlock(Properties properties) {
        super(properties, Tier.MEDIUM);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new MediumCapacityVPStorageBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, BlockEntityRegistry.MC_VP_STORAGE, SimpleEnergyStorageBlockEntity::tick);
    }
}
package de.crafty.skylife.block.machines;

import de.crafty.lifecompat.energy.block.BaseEnergyCable;
import de.crafty.lifecompat.util.EnergyUnitConverter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public abstract class SkyLifeEnergyCable extends BaseEnergyCable implements Equipable {

    public static final VoxelShape DEFAULT_SHAPE = Block.box(6.0D, 6.0D, 6.0D, 10.0D, 10.0D, 10.0D);

    public static final VoxelShape SHAPE_NORTH = Block.box(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 6.0D);
    public static final VoxelShape SHAPE_EAST = Block.box(10.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);
    public static final VoxelShape SHAPE_SOUTH = Block.box(6.0D, 6.0D, 10.0D, 10.0D, 10.0D, 16.0D);
    public static final VoxelShape SHAPE_WEST = Block.box(0.0D, 6.0D, 6.0D, 6.0D, 10.0D, 10.0D);

    public static final VoxelShape SHAPE_UP = Block.box(6.0D, 10.0D, 6.0D, 10.0D, 16.0D, 10.0D);
    public static final VoxelShape SHAPE_DOWN = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 6.0D, 10.0D);

    protected SkyLifeEnergyCable(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        VoxelShape shape = DEFAULT_SHAPE;

        if (blockState.getValue(NORTH) != ConnectionState.NONE)
            shape = Shapes.or(shape, SHAPE_NORTH);

        if (blockState.getValue(EAST) != ConnectionState.NONE)
            shape = Shapes.or(shape, SHAPE_EAST);

        if (blockState.getValue(SOUTH) != ConnectionState.NONE)
            shape = Shapes.or(shape, SHAPE_SOUTH);

        if (blockState.getValue(WEST) != ConnectionState.NONE)
            shape = Shapes.or(shape, SHAPE_WEST);

        if (blockState.getValue(UP) != ConnectionState.NONE)
            shape = Shapes.or(shape, SHAPE_UP);

        if (blockState.getValue(DOWN) != ConnectionState.NONE)
            shape = Shapes.or(shape, SHAPE_DOWN);

        return shape;
    }



    public enum Tier {
        BASIC(EnergyUnitConverter.kiloVP(4), 40),
        IMPROVED(EnergyUnitConverter.kiloVP(16), 320),
        ADVANCED(EnergyUnitConverter.kiloVP(64), 1280);

        final int capacity, maxIO;

        Tier(int capacity, int maxIO){
            this.capacity = capacity;
            this.maxIO = maxIO;
        }

        public int getCapacity() {
            return this.capacity;
        }

        public int getMaxIO() {
            return this.maxIO;
        }
    }


    @Override
    public @NotNull EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.HEAD;
    }
}

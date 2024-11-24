package de.crafty.skylife.block.fluid;

import com.mojang.serialization.MapCodec;
import de.crafty.lifecompat.api.fluid.FluidCompatibility;
import de.crafty.skylife.registry.FluidRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class OilCauldron extends AbstractCauldronBlock {

    public static final MapCodec<OilCauldron> CODEC = simpleCodec(OilCauldron::new);

    public OilCauldron(Properties properties) {
        super(properties, FluidCompatibility.getCauldronInteractionMap(FluidRegistry.OIL));
    }

    @Override
    protected MapCodec<? extends AbstractCauldronBlock> codec() {
        return CODEC;
    }

    @Override
    public boolean isFull(BlockState blockState) {
        return true;
    }

    @Override
    protected double getContentHeight(BlockState blockState) {
        return 0.9375D;
    }

    @Override
    protected void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        if(this.isEntityInsideContent(blockState, blockPos, entity))
            entity.makeStuckInBlock(blockState, new Vec3(0.75F, 0.75F, 0.75F));
    }
}

package de.crafty.skylife.block.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.phys.Vec3;

public class LiquidOilBlock extends LiquidBlock {


    public LiquidOilBlock(FlowingFluid flowingFluid, Properties properties) {
        super(flowingFluid, properties);
    }


    @Override
    protected void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        entity.makeStuckInBlock(blockState, new Vec3(0.75F, 0.75F, 0.75F));
    }
}
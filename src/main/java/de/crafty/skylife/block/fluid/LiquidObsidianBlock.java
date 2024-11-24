package de.crafty.skylife.block.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.phys.Vec3;

public class LiquidObsidianBlock extends LiquidBlock {


    public LiquidObsidianBlock(FlowingFluid flowingFluid, Properties properties) {
        super(flowingFluid, properties);
    }


    @Override
    protected void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        entity.makeStuckInBlock(blockState, new Vec3(0.5F, 0.5F, 0.5F));
    }
}

package de.crafty.skylife.logic;

import de.crafty.skylife.config.SkyLifeConfigs;
import de.crafty.skylife.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockMeltingLogic {


    public static void onMelting(ServerLevel world, BlockPos pos, BlockState oldState, BlockState newState) {

        if (BlockRegistry.getMeltingBlockList().containsKey(newState.getBlock()) && SkyLifeConfigs.BLOCK_MELTING.getHeatEfficiencyForBlock(newState.getBlock(), world.getBlockState(pos.below()), pos.below(), world) > 0.0F)
            world.setBlockAndUpdate(pos, BlockRegistry.getMeltingBlockList().get(newState.getBlock()).defaultBlockState());


        Block above = world.getBlockState(pos.above()).getBlock();

        if (BlockRegistry.getMeltingBlockList().containsKey(above) && SkyLifeConfigs.BLOCK_MELTING.getHeatEfficiencyForBlock(above, world.getBlockState(pos), pos, world) > 0.0F)
            world.setBlockAndUpdate(pos.above(), BlockRegistry.getMeltingBlockList().get(above).defaultBlockState());


    }

}

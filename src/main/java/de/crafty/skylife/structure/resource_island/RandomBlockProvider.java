package de.crafty.skylife.structure.resource_island;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public interface RandomBlockProvider {


    BlockState randomBlock(RandomSource randomSource);
}

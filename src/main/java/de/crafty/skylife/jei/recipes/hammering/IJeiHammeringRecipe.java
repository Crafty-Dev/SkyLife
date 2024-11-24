package de.crafty.skylife.jei.recipes.hammering;

import de.crafty.skylife.config.HammerConfig;
import net.minecraft.world.level.block.Block;

import java.util.List;

public interface IJeiHammeringRecipe {


    List<HammerConfig.HammerDrop> getResults();

    List<Block> getBlocks();

}

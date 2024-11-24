package de.crafty.skylife.jei.recipes.hammering;

import de.crafty.skylife.config.HammerConfig;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class HammeringRecipe implements IJeiHammeringRecipe {

    private final List<Block> blocks;
    private final List<HammerConfig.HammerDrop> drops;

    public HammeringRecipe(List<Block> blocks, List<HammerConfig.HammerDrop> drops){
        this.blocks = blocks;
        this.drops = drops;
    }

    @Override
    public List<HammerConfig.HammerDrop> getResults() {
        return this.drops;
    }

    @Override
    public List<Block> getBlocks() {
        return this.blocks;
    }
}

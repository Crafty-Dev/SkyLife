package de.crafty.skylife.jei.recipes.block_melting;

import de.crafty.skylife.config.BlockMeltingConfig;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class BlockMeltingRecipe implements IJeiBlockMeltingRecipe {

    private final Block meltable;
    private final Fluid meltingResult;
    private final BlockMeltingConfig.HeatSource heatSource;

    public BlockMeltingRecipe(Block meltable, Fluid meltingResult, BlockMeltingConfig.HeatSource heatSource){
        this.heatSource = heatSource;
        this.meltable = meltable;
        this.meltingResult = meltingResult;
    }

    @Override
    public Block getMeltable() {
        return this.meltable;
    }

    @Override
    public Fluid getMeltingResult() {
        return this.meltingResult;
    }

    @Override
    public Block getHeatSource() {
        return heatSource.heatBlock();
    }

    @Override
    public float getHeatEfficiency() {
        return heatSource.heatEfficiency();
    }

    @Override
    public ItemStack getRepresentable() {
        return this.heatSource.representable();
    }
}

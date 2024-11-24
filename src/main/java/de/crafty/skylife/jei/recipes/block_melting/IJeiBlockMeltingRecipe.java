package de.crafty.skylife.jei.recipes.block_melting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public interface IJeiBlockMeltingRecipe {

    Block getMeltable();

    Fluid getMeltingResult();

    Block getHeatSource();

    float getHeatEfficiency();

    ItemStack getRepresentable();

}

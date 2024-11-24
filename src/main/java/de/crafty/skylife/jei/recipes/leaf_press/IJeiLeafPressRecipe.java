package de.crafty.skylife.jei.recipes.leaf_press;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.List;

public interface IJeiLeafPressRecipe {


    List<Block> getInputs();

    ItemStack getOutput();

    float getAmount();

}

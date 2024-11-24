package de.crafty.skylife.jei.recipes.leaf_press;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class LeafPressRecipe implements IJeiLeafPressRecipe {

    private final List<Block> inputs;
    private final float amount;
    private final ItemStack output;

    public LeafPressRecipe(List<Block> inputs, float amount, ItemStack output){
      this.inputs = inputs;
      this.amount = amount;
      this.output = output;
    }

    @Override
    public List<Block> getInputs() {
        return this.inputs;
    }

    @Override
    public ItemStack getOutput() {
        return this.output;
    }

    @Override
    public float getAmount() {
        return this.amount;
    }
}

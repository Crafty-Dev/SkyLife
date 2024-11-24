package de.crafty.skylife.jei.recipes.fluid_conversion;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

import java.util.List;

public interface IJeiFluidConversionRecipe {


    Item getIngredient();

    Fluid getRequiredFluid();

    List<ItemStack> getResults();

}

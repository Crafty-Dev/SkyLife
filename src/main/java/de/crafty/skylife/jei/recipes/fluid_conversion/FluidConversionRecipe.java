package de.crafty.skylife.jei.recipes.fluid_conversion;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

import java.util.List;

public class FluidConversionRecipe implements IJeiFluidConversionRecipe {

    private final Item ingredient;
    private final Fluid requiredFluid;

    private final List<ItemStack> results;

    public FluidConversionRecipe(Item ingredient, Fluid requiredFluid, List<ItemStack> results){
        this.ingredient = ingredient;
        this.requiredFluid = requiredFluid;

        this.results = results;
    }

    @Override
    public Item getIngredient() {
        return this.ingredient;
    }

    @Override
    public Fluid getRequiredFluid() {
        return this.requiredFluid;
    }

    @Override
    public List<ItemStack> getResults() {
        return this.results;
    }
}

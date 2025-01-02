package de.crafty.skylife.eiv.recipes.fluid_conversion;

import de.crafty.eiv.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.api.recipe.IEivViewRecipe;
import de.crafty.eiv.extra.FluidStack;
import de.crafty.eiv.recipe.inventory.RecipeViewMenu;
import de.crafty.eiv.recipe.inventory.SlotContent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;

import java.util.List;

public class FluidConversionViewRecipe implements IEivViewRecipe {

    private final SlotContent ingredient, requiredFluid, results;

    private final SlotContent cauldron = SlotContent.of(Items.CAULDRON);

    public FluidConversionViewRecipe(Item ingredient, Fluid requiredFluid, List<ItemStack> results) {

        this.ingredient = SlotContent.of(ingredient);
        this.requiredFluid = SlotContent.of(new FluidStack(requiredFluid));

        this.results = SlotContent.of(results);
    }

    @Override
    public IEivRecipeViewType getViewType() {
        return FluidConversionViewType.INSTANCE;
    }

    @Override
    public void fillSlots(RecipeViewMenu.SlotFillContext slotFillContext) {

        slotFillContext.fillSlot(0, this.requiredFluid.next());
        slotFillContext.fillSlot(1, this.cauldron.next());
        slotFillContext.fillSlot(2, this.ingredient.next());

        slotFillContext.fillSlot(3, this.results.next());
    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(this.ingredient, this.cauldron, this.requiredFluid);
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(this.results);
    }
}

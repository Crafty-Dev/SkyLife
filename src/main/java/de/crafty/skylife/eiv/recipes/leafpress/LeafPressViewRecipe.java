package de.crafty.skylife.eiv.recipes.leafpress;

import de.crafty.eiv.api.recipe.IEivRecipe;
import de.crafty.eiv.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.api.recipe.IEivViewRecipe;
import de.crafty.eiv.extra.FluidStack;
import de.crafty.eiv.recipe.inventory.RecipeViewMenu;
import de.crafty.eiv.recipe.inventory.SlotContent;
import de.crafty.skylife.registry.BlockRegistry;
import de.crafty.skylife.registry.ItemRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.List;

public class LeafPressViewRecipe implements IEivViewRecipe {

    private final SlotContent leafs, leafPress, fluidOutput, itemOutput;

    public LeafPressViewRecipe(List<Block> inputs, float amount, ItemStack output) {

        List<Item> items = new ArrayList<>();
        inputs.forEach(block -> items.add(block.asItem()));
        this.leafs = SlotContent.ofItemList(items);
        this.leafPress = SlotContent.of(BlockRegistry.LEAF_PRESS.asItem());

        this.fluidOutput = SlotContent.of(new FluidStack(Fluids.WATER, Math.round(amount * FluidStack.AMOUNT_FULL)));
        this.itemOutput = SlotContent.of(output);
    }

    @Override
    public IEivRecipeViewType getViewType() {
        return LeafPressViewType.INSTANCE;
    }

    @Override
    public void fillSlots(RecipeViewMenu.SlotFillContext slotFillContext) {

        slotFillContext.fillSlot(0, this.leafs.next());
        slotFillContext.fillSlot(1, this.leafPress.next());
        slotFillContext.fillSlot(2, this.itemOutput.next());
        slotFillContext.fillSlot(3, this.fluidOutput.next());
    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(this.leafs);
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(this.fluidOutput, this.itemOutput);
    }
}

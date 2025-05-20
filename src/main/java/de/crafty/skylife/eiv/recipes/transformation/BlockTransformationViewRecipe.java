package de.crafty.skylife.eiv.recipes.transformation;

import de.crafty.eiv.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.api.recipe.IEivViewRecipe;
import de.crafty.eiv.recipe.inventory.RecipeViewMenu;
import de.crafty.eiv.recipe.inventory.SlotContent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class BlockTransformationViewRecipe implements IEivViewRecipe {

    private final SlotContent base, converter, result;

    public BlockTransformationViewRecipe(Block base, ItemStack converter, Block result) {
        this.base = SlotContent.of(base.asItem());
        this.converter = SlotContent.of(converter);
        this.result = SlotContent.of(result.asItem());
    }

    @Override
    public IEivRecipeViewType getViewType() {
        return BlockTransformationViewType.INSTANCE;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {

        slotFillContext.bindSlot(0, this.base);
        slotFillContext.bindSlot(1, this.converter);
        slotFillContext.bindSlot(2, this.result);

    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(this.base, this.converter);
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(this.result);
    }
}

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
    public void fillSlots(RecipeViewMenu.SlotFillContext slotFillContext) {

        slotFillContext.fillSlot(0, this.base.next());
        slotFillContext.fillSlot(1, this.converter.next());
        slotFillContext.fillSlot(2, this.result.next());

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

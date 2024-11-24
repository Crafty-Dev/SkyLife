package de.crafty.skylife.jei.category;

import de.crafty.skylife.SkyLife;
import de.crafty.skylife.registry.BlockRegistry;
import de.crafty.skylife.jei.SkyLifeRecipeTypes;
import de.crafty.skylife.jei.recipes.leaf_press.IJeiLeafPressRecipe;
import de.crafty.skylife.util.SkyLifeConstants;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.List;

public class LeafPressRecipeCategory implements IRecipeCategory<IJeiLeafPressRecipe> {

    private final IDrawable background;
    private final IDrawable icon;

    public LeafPressRecipeCategory(IGuiHelper guiHelper){

        this.background = guiHelper.drawableBuilder(SkyLife.JEI_RECIPE_GUI, 0, 180, 116, 54).setTextureSize(512, 512).build();
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(BlockRegistry.LEAF_PRESS));
    }

    @Override
    public RecipeType<IJeiLeafPressRecipe> getRecipeType() {
        return SkyLifeRecipeTypes.LEAF_PRESS;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.jei.category.leaf_press");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, IJeiLeafPressRecipe recipe, IFocusGroup focuses) {

        List<ItemStack> inputs = new ArrayList<>();
        recipe.getInputs().forEach(item -> inputs.add(new ItemStack(item)));

        builder.addSlot(RecipeIngredientRole.INPUT, 9, 1).addItemStacks(inputs);
        builder.addSlot(RecipeIngredientRole.INPUT, 9, 37).addItemStack(new ItemStack(BlockRegistry.LEAF_PRESS));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 77, 19).addFluidStack(Fluids.WATER, SkyLifeConstants.FABRIC_BUCKET_VOLUME).addTooltipCallback((recipeSlotView, tooltip) -> {
            tooltip.add(Component.translatable("block.skylife.leaf_press.water_amount").append(": ").append(String.valueOf(recipe.getAmount())).withStyle(ChatFormatting.GRAY));
        });
        builder.addSlot(RecipeIngredientRole.OUTPUT, 99, 23).addItemStack(recipe.getOutput());
    }

}

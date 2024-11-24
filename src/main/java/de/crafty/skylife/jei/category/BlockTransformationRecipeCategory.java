package de.crafty.skylife.jei.category;

import de.crafty.skylife.SkyLife;
import de.crafty.skylife.jei.SkyLifeRecipeTypes;
import de.crafty.skylife.jei.recipes.block_transformation.IJeiBlockTransformationRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class BlockTransformationRecipeCategory implements IRecipeCategory<IJeiBlockTransformationRecipe> {

    private final IDrawable background;
    private final IDrawable icon;

    public BlockTransformationRecipeCategory(IGuiHelper guiHelper){
        this.background = guiHelper.drawableBuilder(SkyLife.JEI_RECIPE_GUI, 0, 36, 118, 36).setTextureSize(512, 512).build();
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(Blocks.STONE));
    }

    @Override
    public RecipeType<IJeiBlockTransformationRecipe> getRecipeType() {
        return SkyLifeRecipeTypes.BLOCK_TRANSFORMATION;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.jei.category.block_transformation");
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
    public void setRecipe(IRecipeLayoutBuilder builder, IJeiBlockTransformationRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 18 + 1).addItemStack(new ItemStack(recipe.getBase()));
        builder.addSlot(RecipeIngredientRole.INPUT, 51, 1).addItemStack(recipe.getConverter());


        builder.addSlot(RecipeIngredientRole.OUTPUT, 101, 18 + 1).addItemStack(new ItemStack(recipe.getResult()));
    }
}

package de.crafty.skylife.jei.category;

import de.crafty.skylife.SkyLife;
import de.crafty.skylife.jei.SkyLifeRecipeTypes;
import de.crafty.skylife.jei.recipes.fluid_conversion.IJeiFluidConversionRecipe;
import de.crafty.skylife.registry.BlockRegistry;
import de.crafty.skylife.util.SkyLifeConstants;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

;

public class FluidConversionRecipeCategory implements IRecipeCategory<IJeiFluidConversionRecipe> {


    private final IDrawable background;
    private final IDrawable icon;


    public FluidConversionRecipeCategory(IGuiHelper guiHelper){
        this.background = guiHelper.drawableBuilder(SkyLife.JEI_RECIPE_GUI, 0, 0, 133, 36).setTextureSize(512, 512).build();
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(Items.LAVA_BUCKET));
    }

    @Override
    public RecipeType<IJeiFluidConversionRecipe> getRecipeType() {
        return SkyLifeRecipeTypes.FLUID_CONVERSION;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.jei.category.fluid_conversion");
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
    public void setRecipe(IRecipeLayoutBuilder builder, IJeiFluidConversionRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 79, 1).addItemStack(new ItemStack(recipe.getIngredient()));

        builder.addSlot(RecipeIngredientRole.INPUT, 1, 18 + 1).addFluidStack(recipe.getRequiredFluid(), SkyLifeConstants.FABRIC_BUCKET_VOLUME);
        builder.addSlot(RecipeIngredientRole.INPUT, 50, 18 + 1).addItemStack(new ItemStack(Items.CAULDRON));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 116, 18 + 1).addItemStacks(recipe.getResults());

    }

}

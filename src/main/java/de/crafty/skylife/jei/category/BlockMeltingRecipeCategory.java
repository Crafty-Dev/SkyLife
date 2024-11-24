package de.crafty.skylife.jei.category;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import de.crafty.skylife.SkyLife;
import de.crafty.skylife.blockentities.MeltingBlockEntity;
import de.crafty.skylife.jei.SkyLifeRecipeTypes;
import de.crafty.skylife.jei.recipes.block_melting.IJeiBlockMeltingRecipe;
import de.crafty.skylife.mixin.world.level.block.LiquidBlockAccessor;
import de.crafty.skylife.util.SkyLifeConstants;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluids;

public class BlockMeltingRecipeCategory implements IRecipeCategory<IJeiBlockMeltingRecipe> {

    private final LoadingCache<Integer, IDrawableAnimated> cachedFlames;

    private final IDrawable icon;
    private final IDrawable background;

    public BlockMeltingRecipeCategory(IGuiHelper guiHelper){

        this.background = guiHelper.drawableBuilder(SkyLife.JEI_RECIPE_GUI, 0, 72, 98, 54).setTextureSize(512, 512).build();

        this.icon = guiHelper.createDrawableItemStack(new ItemStack(Items.COBBLESTONE));

        this.cachedFlames = CacheBuilder.newBuilder()
                .maximumSize(25)
                .build(new CacheLoader<>() {
                    @Override
                    public IDrawableAnimated load(Integer burnTime) {
                        return guiHelper.drawableBuilder(SkyLife.JEI_RECIPE_GUI, 98, 72, 14, 14)
                                .setTextureSize(512, 512)
                                .buildAnimated(burnTime, IDrawableAnimated.StartDirection.BOTTOM, false);
                    }
                });
    }

    @Override
    public RecipeType<IJeiBlockMeltingRecipe> getRecipeType() {
        return SkyLifeRecipeTypes.BLOCK_MELTING;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.jei.category.cobblestone_melting");
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
    public void setRecipe(IRecipeLayoutBuilder builder, IJeiBlockMeltingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 9, 1).addItemStack(new ItemStack(recipe.getMeltable()));


        if(recipe.getHeatSource() instanceof LiquidBlock liquidBlock){
            builder.addSlot(RecipeIngredientRole.INPUT, 9, 37).addFluidStack(((LiquidBlockAccessor)liquidBlock).getFluid().getFlowing(), SkyLifeConstants.FABRIC_BUCKET_VOLUME);
        }else
            builder.addSlot(RecipeIngredientRole.INPUT, 9, 37).addItemStack(recipe.getRepresentable());


        builder.addSlot(RecipeIngredientRole.OUTPUT, 77, 19).addFluidStack(recipe.getMeltingResult(), SkyLifeConstants.FABRIC_BUCKET_VOLUME);
    }

    @Override
    public void draw(IJeiBlockMeltingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        int meltingTime = (int) (MeltingBlockEntity.BASE_MELTING_TIME / (4.0F * recipe.getHeatEfficiency()));
        IDrawableAnimated flame = this.cachedFlames.getUnchecked(meltingTime);
        flame.draw(guiGraphics, 9, 20);

        //Draw Heat Efficiency
        Font font = Minecraft.getInstance().font;

        Component heatEfficiency = Component.literal(recipe.getHeatEfficiency() + "x");
        guiGraphics.drawString(font, heatEfficiency, this.getWidth() - font.width(heatEfficiency), 45, 0xFF808080, false);

        Component heatSource = recipe.getHeatSource().getName();
        guiGraphics.drawString(font, heatSource, this.getWidth() - font.width(heatSource), 1, 0xFF808080, false);
    }
}

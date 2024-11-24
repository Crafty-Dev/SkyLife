package de.crafty.skylife.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import de.crafty.skylife.SkyLife;
import de.crafty.skylife.registry.TagRegistry;
import de.crafty.skylife.jei.SkyLifeRecipeTypes;
import de.crafty.skylife.jei.recipes.resource_sheeps.IJeiResourceSheepRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.text.DecimalFormat;

public class ResourceSheepRecipeCategory implements IRecipeCategory<IJeiResourceSheepRecipe> {

    private final IDrawable background;
    private final IDrawable icon;

    private final IDrawableStatic static_hearts;
    private final IDrawableAnimated animated_hearts;

    public ResourceSheepRecipeCategory(IGuiHelper guiHelper){

        this.background = guiHelper.drawableBuilder(SkyLife.JEI_RECIPE_GUI, 0, 234, 132, 89).setTextureSize(512, 512).build();
        this.icon = guiHelper.drawableBuilder(SkyLife.JEI_RECIPE_GUI, 14, 323, 16, 16).setTextureSize(512, 512).build();

        this.static_hearts = guiHelper.drawableBuilder(SkyLife.JEI_RECIPE_GUI, 0, 323, 14, 12).setTextureSize(512, 512).build();
        this.animated_hearts = guiHelper.createAnimatedDrawable(this.static_hearts, 75, IDrawableAnimated.StartDirection.BOTTOM, false);
    }

    @Override
    public RecipeType<IJeiResourceSheepRecipe> getRecipeType() {
        return SkyLifeRecipeTypes.RESOURCE_SHEEP;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.jei.category.resource_sheep");
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
    public void setRecipe(IRecipeLayoutBuilder builder, IJeiResourceSheepRecipe recipe, IFocusGroup focuses) {

        builder.addSlot(RecipeIngredientRole.INPUT, 115, 17).addItemStacks(recipe.getWheat());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 115, 37).addItemStacks(recipe.getDrops());

    }

    @Override
    public void draw(IJeiResourceSheepRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {

        this.animated_hearts.draw(guiGraphics, 26, 19);
        this.animated_hearts.draw(guiGraphics, 26, 19 + 26);
        this.animated_hearts.draw(guiGraphics, 26, 19 + 26 * 2);

        Font font = Minecraft.getInstance().font;

        Component name = Component.translatable("entity.skylife." + recipe.getSheepName().toLowerCase() + "_sheep");


        this.drawCenteredString(guiGraphics, font, name, this.getWidth() / 2.0F, 0, 1.0F, 0xFF808080);


        Component wheat = Component.translatable("gui.jei.category.resource_sheep.wheat").append(":");
        Component drop = Component.translatable("gui.jei.category.resource_sheep.drop").append(":");

        this.drawString(guiGraphics, font, wheat, this.getWidth() - 18 - 4 - font.width(wheat), 21, 1.0F, 0xFF808080);
        this.drawString(guiGraphics, font, drop, this.getWidth() - 18 - 4 - font.width(drop), 41, 1.0F, 0xFF808080);


        float chance = recipe.getChance();

        DecimalFormat formatter = new DecimalFormat("#.##");

        Component comp = Component.literal(formatter.format(chance * 100.0F) + "%");
        Component comp1 = Component.literal(formatter.format(chance * 1.25F * 100.0F) + "%");
        Component comp2 = Component.literal(formatter.format(1.0F * 100.0F) + "%");


        this.drawCenteredString(guiGraphics, font, comp, 32, 34, 0.5F, 0xFF808080);
        this.drawCenteredString(guiGraphics, font, comp1, 32, 34 + 26, 0.5F, 0xFF808080);
        this.drawCenteredString(guiGraphics, font, comp2, 32, 34 + 26 * 2, 0.5F, 0xFF808080);


        //Dimension required
        Component label = Component.translatable("gui.jei.category.resource_sheep.dimension").withStyle(ChatFormatting.UNDERLINE);
        Component dim = Component.translatable("gui.jei.category.resource_sheep.dimension.any").withStyle(ChatFormatting.DARK_GRAY);

        if(recipe.getWheat().getFirst().is(TagRegistry.NETHER_WHEAT))
            dim = Component.translatable("gui.jei.category.resource_sheep.dimension.nether").withStyle(ChatFormatting.RED);

        this.drawCenteredString(guiGraphics, font, label, 98.5F, 64, 0.75F, 0xFF808080);
        this.drawCenteredString(guiGraphics, font, dim, 98.5F, 64 + font.lineHeight, 0.75F, 0xFF808080);
    
    }

    private void drawCenteredString(GuiGraphics guiGraphics, Font font, Component text, float x, float y, float scale, int color){
        this.drawString(guiGraphics, font, text, x - font.width(text) / 2.0F * scale, y, scale, color);
    }

    private void drawString(GuiGraphics guiGraphics, Font font, Component text, float x, float y, float scale, int color){
        
        PoseStack stack = guiGraphics.pose();
        
        stack.pushPose();
        stack.translate(x, y, 0.0F);
        stack.pushPose();
        stack.scale(scale, scale, 1.0F);
        guiGraphics.drawString(font, text, 0, 0, color, false);
        stack.popPose();
        stack.popPose();
    }
}

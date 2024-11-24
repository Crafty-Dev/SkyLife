package de.crafty.skylife.jei.category;

import de.crafty.skylife.SkyLife;
import de.crafty.skylife.config.HammerConfig;
import de.crafty.skylife.registry.ItemRegistry;
import de.crafty.skylife.jei.SkyLifeRecipeTypes;
import de.crafty.skylife.jei.recipes.hammering.IJeiHammeringRecipe;
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
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

public class HammeringRecipeCategory implements IRecipeCategory<IJeiHammeringRecipe> {


    private final IDrawable background;
    private final IDrawable icon;

    public HammeringRecipeCategory(IGuiHelper guiHelper) {

        this.background = guiHelper.drawableBuilder(SkyLife.JEI_RECIPE_GUI, 0, 126, 162, 54).setTextureSize(512, 512).build();
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ItemRegistry.IRON_HAMMER));

    }


    @Override
    public RecipeType<IJeiHammeringRecipe> getRecipeType() {
        return SkyLifeRecipeTypes.HAMMERING;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.jei.category.hammering");
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
    public void setRecipe(IRecipeLayoutBuilder builder, IJeiHammeringRecipe recipe, IFocusGroup focuses) {

        builder.addSlot(RecipeIngredientRole.INPUT, 55, 1).addItemStacks(List.of(
                new ItemStack(ItemRegistry.WOODEN_HAMMER),
                new ItemStack(ItemRegistry.STONE_HAMMER),
                new ItemStack(ItemRegistry.IRON_HAMMER),
                new ItemStack(ItemRegistry.GOLDEN_HAMMER),
                new ItemStack(ItemRegistry.DIAMOND_HAMMER),
                new ItemStack(ItemRegistry.NETHERITE_HAMMER)
        ));

        List<Block> blocks = recipe.getBlocks();
        List<ItemStack> inputs = new ArrayList<>();
        blocks.forEach(block -> inputs.add(new ItemStack(block)));

        builder.addSlot(RecipeIngredientRole.INPUT, 91, 1).addItemStacks(inputs);

        for (int i = 0; i < recipe.getResults().size(); i++) {
            HammerConfig.HammerDrop drop = recipe.getResults().get(i);
            builder.addSlot(RecipeIngredientRole.OUTPUT, 1 + i * 18, 37)
                    .addItemStack(drop == null ? ItemStack.EMPTY : new ItemStack(drop.item()))
                    .addTooltipCallback((recipeSlotView, tooltip) -> {
                        tooltip.add(Component.translatable("skylife.hammerdrop.chance").append(": ").withStyle(ChatFormatting.GRAY).append(Component.literal(((int) (drop.chance() * 100)) + "%").withStyle(ChatFormatting.DARK_PURPLE)));
                        tooltip.add(Component.translatable("skylife.hammerdrop.amount").append(": ").withStyle(ChatFormatting.GRAY).append(Component.literal(drop.min() + "-" + drop.max())).withStyle(ChatFormatting.GRAY));
                    });
        }

    }

}

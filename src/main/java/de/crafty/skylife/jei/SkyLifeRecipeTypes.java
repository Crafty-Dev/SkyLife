package de.crafty.skylife.jei;

import de.crafty.skylife.SkyLife;
import de.crafty.skylife.jei.recipes.block_transformation.IJeiBlockTransformationRecipe;
import de.crafty.skylife.jei.recipes.block_melting.IJeiBlockMeltingRecipe;
import de.crafty.skylife.jei.recipes.hammering.IJeiHammeringRecipe;
import de.crafty.skylife.jei.recipes.fluid_conversion.IJeiFluidConversionRecipe;
import de.crafty.skylife.jei.recipes.leaf_press.IJeiLeafPressRecipe;
import de.crafty.skylife.jei.recipes.resource_sheeps.IJeiResourceSheepRecipe;
import mezz.jei.api.recipe.RecipeType;

public class SkyLifeRecipeTypes {

    public static final RecipeType<IJeiFluidConversionRecipe> FLUID_CONVERSION = RecipeType.create(SkyLife.MODID, "lava_conversion", IJeiFluidConversionRecipe.class);
    public static final RecipeType<IJeiBlockTransformationRecipe> BLOCK_TRANSFORMATION = RecipeType.create(SkyLife.MODID, "block_transformation", IJeiBlockTransformationRecipe.class);

    public static final RecipeType<IJeiBlockMeltingRecipe> BLOCK_MELTING = RecipeType.create(SkyLife.MODID, "cobblestone_melting", IJeiBlockMeltingRecipe.class);

    public static final RecipeType<IJeiHammeringRecipe> HAMMERING = RecipeType.create(SkyLife.MODID, "hammering", IJeiHammeringRecipe.class);

    public static final RecipeType<IJeiLeafPressRecipe> LEAF_PRESS = RecipeType.create(SkyLife.MODID, "leaf_press", IJeiLeafPressRecipe.class);

    public static final RecipeType<IJeiResourceSheepRecipe> RESOURCE_SHEEP = RecipeType.create(SkyLife.MODID, "resource_sheep", IJeiResourceSheepRecipe.class);


}

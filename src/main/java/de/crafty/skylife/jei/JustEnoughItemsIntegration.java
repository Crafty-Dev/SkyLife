package de.crafty.skylife.jei;

import de.crafty.skylife.SkyLife;
import de.crafty.skylife.jei.category.*;
import de.crafty.skylife.jei.recipes.SkyLifeRecipeMaker;
import de.crafty.skylife.registry.BlockRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class JustEnoughItemsIntegration implements IModPlugin {

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "jei");
    }


    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(SkyLifeRecipeTypes.FLUID_CONVERSION, SkyLifeRecipeMaker.getLavaConversionRecipes());
        registration.addRecipes(SkyLifeRecipeTypes.BLOCK_TRANSFORMATION, SkyLifeRecipeMaker.getBlockTransformationRecipes());
        registration.addRecipes(SkyLifeRecipeTypes.BLOCK_MELTING, SkyLifeRecipeMaker.getBlockMeltingRecipes());
        registration.addRecipes(SkyLifeRecipeTypes.HAMMERING, SkyLifeRecipeMaker.getHammeringRecipes());
        registration.addRecipes(SkyLifeRecipeTypes.LEAF_PRESS, SkyLifeRecipeMaker.getLeafPressRecipes());
        registration.addRecipes(SkyLifeRecipeTypes.RESOURCE_SHEEP, SkyLifeRecipeMaker.getResourceSheepRecipes());
    }


    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {

        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();

        registration.addRecipeCategories(new FluidConversionRecipeCategory(guiHelper));
        registration.addRecipeCategories(new BlockTransformationRecipeCategory(guiHelper));
        registration.addRecipeCategories(new BlockMeltingRecipeCategory(guiHelper));
        registration.addRecipeCategories(new HammeringRecipeCategory(guiHelper));
        registration.addRecipeCategories(new LeafPressRecipeCategory(guiHelper));
        registration.addRecipeCategories(new ResourceSheepRecipeCategory(guiHelper));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(Blocks.CAULDRON), SkyLifeRecipeTypes.FLUID_CONVERSION);
        registration.addRecipeCatalyst(new ItemStack(BlockRegistry.SOLID_FLUID_MERGER), SkyLifeRecipeTypes.FLUID_CONVERSION);

        registration.addRecipeCatalyst(new ItemStack(BlockRegistry.BLOCK_MELTER), SkyLifeRecipeTypes.BLOCK_MELTING);

        registration.addRecipeCatalyst(new ItemStack(BlockRegistry.BLOCK_BREAKER), SkyLifeRecipeTypes.HAMMERING);

        registration.addRecipeCatalyst(new ItemStack(BlockRegistry.LEAF_PRESS), SkyLifeRecipeTypes.LEAF_PRESS);
    }
}

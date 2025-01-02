package de.crafty.skylife.eiv;

import de.crafty.eiv.api.IExtendedItemViewIntegration;
import de.crafty.eiv.api.recipe.IEivRecipe;
import de.crafty.eiv.api.recipe.IEivViewRecipe;
import de.crafty.eiv.api.recipe.ItemViewRecipes;
import de.crafty.skylife.SkyLife;
import de.crafty.skylife.block.LeafPressBlock;
import de.crafty.skylife.config.HammerConfig;
import de.crafty.skylife.config.SkyLifeConfigs;
import de.crafty.skylife.eiv.recipes.fluid_conversion.FluidConversionServerRecipe;
import de.crafty.skylife.eiv.recipes.fluid_conversion.FluidConversionViewRecipe;
import de.crafty.skylife.eiv.recipes.fluid_conversion.FluidConversionViewType;
import de.crafty.skylife.eiv.recipes.hammering.HammeringServerRecipe;
import de.crafty.skylife.eiv.recipes.hammering.HammeringViewRecipe;
import de.crafty.skylife.eiv.recipes.hammering.HammeringViewType;
import de.crafty.skylife.eiv.recipes.leafpress.LeafPressServerRecipe;
import de.crafty.skylife.eiv.recipes.leafpress.LeafPressViewRecipe;
import de.crafty.skylife.eiv.recipes.leafpress.LeafPressViewType;
import de.crafty.skylife.eiv.recipes.loot_gem.LootGemServerRecipe;
import de.crafty.skylife.eiv.recipes.loot_gem.LootGemViewRecipe;
import de.crafty.skylife.eiv.recipes.melting.BlockMeltingServerRecipe;
import de.crafty.skylife.eiv.recipes.melting.BlockMeltingViewRecipe;
import de.crafty.skylife.eiv.recipes.melting.BlockMeltingViewType;
import de.crafty.skylife.eiv.recipes.oil_processing.OilProcessingServerRecipe;
import de.crafty.skylife.eiv.recipes.oil_processing.OilProcessingViewRecipe;
import de.crafty.skylife.eiv.recipes.oil_processing.OilProcessingViewType;
import de.crafty.skylife.eiv.recipes.sheeps.ResourceSheepServerRecipe;
import de.crafty.skylife.eiv.recipes.sheeps.ResourceSheepViewRecipe;
import de.crafty.skylife.eiv.recipes.sheeps.ResourceSheepViewType;
import de.crafty.skylife.eiv.recipes.transformation.BlockTransformationServerRecipe;
import de.crafty.skylife.eiv.recipes.transformation.BlockTransformationViewRecipe;
import de.crafty.skylife.eiv.recipes.transformation.BlockTransformationViewType;
import de.crafty.skylife.entity.ResourceSheepEntity;
import de.crafty.skylife.item.LootGemItem;
import de.crafty.skylife.jei.recipes.block_melting.BlockMeltingRecipe;
import de.crafty.skylife.jei.recipes.block_transformation.BlockTransformationRecipe;
import de.crafty.skylife.jei.recipes.fluid_conversion.FluidConversionRecipe;
import de.crafty.skylife.jei.recipes.fluid_conversion.IJeiFluidConversionRecipe;
import de.crafty.skylife.jei.recipes.leaf_press.LeafPressRecipe;
import de.crafty.skylife.jei.recipes.resource_sheeps.ResourceSheepRecipe;
import de.crafty.skylife.registry.BlockRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EivIntegration implements IExtendedItemViewIntegration {


    public static final ResourceLocation SKYLIFE_VIEW_ICONS = ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "textures/gui/skylife_view_icons.png");

    @Override
    public void onIntegrationInitialize() {

        ItemViewRecipes.INSTANCE.addModRecipeProvider(list -> {

            HashMap<List<HammerConfig.HammerDrop>, List<Block>> groups = new HashMap<>();

            SkyLifeConfigs.HAMMER.getDrops().forEach((block, hammerDrops) -> {
                if (groups.containsKey(hammerDrops)) {
                    groups.get(hammerDrops).add(block);
                    return;
                }
                ArrayList<Block> group = new ArrayList<>();
                group.add(block);
                groups.put(hammerDrops, group);
            });

            groups.forEach((hammerDrops, blocks) -> {
                list.add(new HammeringServerRecipe(blocks, hammerDrops));
            });

        });

        ItemViewRecipes.INSTANCE.addModRecipeProvider(list -> {

            SkyLifeConfigs.FLUID_CONVERSION.getConversions().forEach((input, fluidDrops) -> {
                list.add(new FluidConversionServerRecipe(input, fluidDrops));
            });

        });

        ItemViewRecipes.INSTANCE.addModRecipeProvider(list -> {
            list.add(new LeafPressServerRecipe(LeafPressBlock.VALID_LEAVES, 0.25f, new ItemStack(BlockRegistry.DRIED_LEAVES)));
        });

        ItemViewRecipes.INSTANCE.addModRecipeProvider(list -> {

            SkyLifeConfigs.BLOCK_MELTING.getMeltables().forEach((meltable, meltingRecipe) -> {
                meltingRecipe.heatSources().forEach(heatSource -> {
                    list.add(new BlockMeltingServerRecipe(meltable, meltingRecipe.meltingResult(), heatSource));
                });
            });

        });

        ItemViewRecipes.INSTANCE.addModRecipeProvider(list -> {

            list.add(new OilProcessingServerRecipe(
                    SkyLifeConfigs.OIL_PROCESSING.getNoPI_requiredLiquid(),
                    ItemStack.EMPTY,
                    new ItemStack(SkyLifeConfigs.OIL_PROCESSING.getNoPI_result(), SkyLifeConfigs.OIL_PROCESSING.getNoPI_count()),
                    SkyLifeConfigs.OIL_PROCESSING.getNoPI_processingTime()
            ));

            SkyLifeConfigs.OIL_PROCESSING.getProcessingRecipes().forEach((item, processingRecipe) -> {
                list.add(new OilProcessingServerRecipe(processingRecipe.liquidAmount(), new ItemStack(item), new ItemStack(processingRecipe.output(), processingRecipe.outputCount()), processingRecipe.processingTime()));
            });

        });

        ItemViewRecipes.INSTANCE.addModRecipeProvider(list -> {
            for (ResourceSheepEntity.Type type : ResourceSheepEntity.Type.values()) {
                list.add(new ResourceSheepServerRecipe(type));
            }
        });

        ItemViewRecipes.INSTANCE.addModRecipeProvider(list -> {

            SkyLifeConfigs.BLOCK_TRANSFORMATION.getTransformations().forEach((converter, blockTransformations) -> {
                blockTransformations.forEach(blockTransformation -> {
                    list.add(new BlockTransformationServerRecipe(blockTransformation.block(), blockTransformation.representable() == null ? new ItemStack(converter) : blockTransformation.representable(), blockTransformation.result().getBlock()));
                });
            });

        });

        ItemViewRecipes.INSTANCE.addModRecipeProvider(list -> {

            SkyLifeConfigs.LOOT_GEM.getGems().forEach((item, lootContent) -> {
                list.add(new LootGemServerRecipe(item, lootContent));
            });

        });


        ItemViewRecipes.INSTANCE.registerModRecipeWrapper(HammeringServerRecipe.TYPE, iEivServerModRecipe -> {
            HammeringServerRecipe recipe = (HammeringServerRecipe) iEivServerModRecipe;
            return List.of(new HammeringViewRecipe(recipe.getBlocks(), recipe.getHammerDrops()));
        });

        ItemViewRecipes.INSTANCE.registerModRecipeWrapper(FluidConversionServerRecipe.TYPE, iEivServerModRecipe -> {
            FluidConversionServerRecipe recipe = (FluidConversionServerRecipe) iEivServerModRecipe;
            List<FluidConversionViewRecipe> viewRecipes = new ArrayList<>();

            HashMap<Fluid, List<ItemStack>> items = new HashMap<>();

            recipe.getDrops().forEach(lavaDrop -> {
                for (int i = lavaDrop.min(); i <= lavaDrop.max(); i++) {
                    List<ItemStack> itemDrops = items.getOrDefault(lavaDrop.requiredFluid(), new ArrayList<>());
                    itemDrops.add(new ItemStack(lavaDrop.output(), i));
                    items.put(lavaDrop.requiredFluid(), itemDrops);
                }
            });

            items.forEach((fluid, itemStacks) -> {
                viewRecipes.add(new FluidConversionViewRecipe(recipe.getIngredient(), fluid, itemStacks));
            });

            return viewRecipes;
        });

        ItemViewRecipes.INSTANCE.registerModRecipeWrapper(LeafPressServerRecipe.TYPE, iEivServerModRecipe -> {
            LeafPressServerRecipe recipe = (LeafPressServerRecipe) iEivServerModRecipe;

            return List.of(new LeafPressViewRecipe(recipe.getInputs(), recipe.getAmount(), recipe.getOutput()));
        });

        ItemViewRecipes.INSTANCE.registerModRecipeWrapper(BlockMeltingServerRecipe.TYPE, iEivServerModRecipe -> {
            BlockMeltingServerRecipe recipe = (BlockMeltingServerRecipe) iEivServerModRecipe;
            return List.of(new BlockMeltingViewRecipe(recipe.getMeltable(), recipe.getLiquid(), recipe.getHeatSource()));
        });

        ItemViewRecipes.INSTANCE.registerModRecipeWrapper(OilProcessingServerRecipe.TYPE, iEivServerModRecipe -> {
            OilProcessingServerRecipe recipe = (OilProcessingServerRecipe) iEivServerModRecipe;

            return List.of(new OilProcessingViewRecipe(recipe.getLiquidAmount(), recipe.getProcessingItem(), recipe.getResult(), recipe.getProcessingTime()));

        });

        ItemViewRecipes.INSTANCE.registerModRecipeWrapper(ResourceSheepServerRecipe.TYPE, iEivServerModRecipe -> {
            ResourceSheepServerRecipe recipe = (ResourceSheepServerRecipe) iEivServerModRecipe;
            return List.of(new ResourceSheepViewRecipe(recipe.getSheepType()));
        });

        ItemViewRecipes.INSTANCE.registerModRecipeWrapper(BlockTransformationServerRecipe.TYPE, iEivServerModRecipe -> {
            BlockTransformationServerRecipe recipe = (BlockTransformationServerRecipe) iEivServerModRecipe;

            return List.of(new BlockTransformationViewRecipe(recipe.getBase(), recipe.getConverter(), recipe.getResult()));

        });

        ItemViewRecipes.INSTANCE.registerModRecipeWrapper(LootGemServerRecipe.TYPE, iEivServerModRecipe -> {
            LootGemServerRecipe recipe = (LootGemServerRecipe) iEivServerModRecipe;
            List<LootGemViewRecipe> viewRecipes = new ArrayList<>();

            List<LootGemItem.LootEntry> entries = new ArrayList<>(recipe.getLoot().lootEntries());

            while (!entries.isEmpty()) {
                List<LootGemItem.LootEntry> list = new ArrayList<>();
                for(int i = 0; i < Math.min(entries.size(), 27); i++){
                    list.add(entries.get(i));
                }

                entries.removeAll(list);
                viewRecipes.add(new LootGemViewRecipe(recipe.getGem(), new LootGemItem.LootContent(list, recipe.getLoot().entityChances())));
            }

            return viewRecipes;
        });
    }
}

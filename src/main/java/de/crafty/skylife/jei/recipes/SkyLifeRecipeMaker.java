package de.crafty.skylife.jei.recipes;

import de.crafty.skylife.block.LeafPressBlock;
import de.crafty.skylife.config.HammerConfig;
import de.crafty.skylife.config.SkyLifeConfigs;
import de.crafty.skylife.entity.ResourceSheepEntity;
import de.crafty.skylife.registry.BlockRegistry;
import de.crafty.skylife.jei.recipes.block_melting.BlockMeltingRecipe;
import de.crafty.skylife.jei.recipes.block_melting.IJeiBlockMeltingRecipe;
import de.crafty.skylife.jei.recipes.block_transformation.BlockTransformationRecipe;
import de.crafty.skylife.jei.recipes.block_transformation.IJeiBlockTransformationRecipe;
import de.crafty.skylife.jei.recipes.hammering.HammeringRecipe;
import de.crafty.skylife.jei.recipes.hammering.IJeiHammeringRecipe;
import de.crafty.skylife.jei.recipes.fluid_conversion.IJeiFluidConversionRecipe;
import de.crafty.skylife.jei.recipes.fluid_conversion.FluidConversionRecipe;
import de.crafty.skylife.jei.recipes.leaf_press.IJeiLeafPressRecipe;
import de.crafty.skylife.jei.recipes.leaf_press.LeafPressRecipe;
import de.crafty.skylife.jei.recipes.resource_sheeps.IJeiResourceSheepRecipe;
import de.crafty.skylife.jei.recipes.resource_sheeps.ResourceSheepRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SkyLifeRecipeMaker {


    public static List<IJeiBlockTransformationRecipe> getBlockTransformationRecipes(){

        List<IJeiBlockTransformationRecipe> list = new ArrayList<>();

        SkyLifeConfigs.BLOCK_TRANSFORMATION.getTransformations().forEach((converter, blockTransformations) -> {
            blockTransformations.forEach(blockTransformation -> {
                list.add(new BlockTransformationRecipe(blockTransformation.block(), blockTransformation.representable() == null ? new ItemStack(converter) : blockTransformation.representable(), blockTransformation.result().getBlock()));
            });
        });


        return list;
    }

    public static List<IJeiFluidConversionRecipe> getLavaConversionRecipes(){

        List<IJeiFluidConversionRecipe> list = new ArrayList<>();

        SkyLifeConfigs.FLUID_CONVERSION.getConversions().forEach((input, fluidDrops) -> {

            HashMap<Fluid, List<ItemStack>> items = new HashMap<>();

            fluidDrops.forEach(lavaDrop -> {
                for(int i = lavaDrop.min(); i <= lavaDrop.max(); i++){
                    List<ItemStack> itemDrops = items.getOrDefault(lavaDrop.requiredFluid(), new ArrayList<>());
                    itemDrops.add(new ItemStack(lavaDrop.output(), i));
                    items.put(lavaDrop.requiredFluid(), itemDrops);
                }
            });

            items.forEach((fluid, itemStacks) -> {
                list.add(new FluidConversionRecipe(input, fluid, itemStacks));
            });

        });

        return list;
    }

    public static List<IJeiBlockMeltingRecipe> getBlockMeltingRecipes(){

        List<IJeiBlockMeltingRecipe> list = new ArrayList<>();

        SkyLifeConfigs.BLOCK_MELTING.getMeltables().forEach((meltable, meltingRecipe) -> {
            meltingRecipe.heatSources().forEach(heatSource -> {
                list.add(new BlockMeltingRecipe(meltable, meltingRecipe.meltingResult(), heatSource));
            });
        });

        return list;
    }

    public static List<IJeiHammeringRecipe> getHammeringRecipes(){

        List<IJeiHammeringRecipe> list = new ArrayList<>();

        HashMap<List<HammerConfig.HammerDrop>, List<Block>> groups = new HashMap<>();

        SkyLifeConfigs.HAMMER.getDrops().forEach((block, hammerDrops) -> {
            if(groups.containsKey(hammerDrops)){
                groups.get(hammerDrops).add(block);
                return;
            }
            ArrayList<Block> group = new ArrayList<>();
            group.add(block);
            groups.put(hammerDrops, group);
        });

        groups.forEach((hammerDrops, blocks) -> {
            list.add(new HammeringRecipe(blocks, hammerDrops));
        });

        return list;
    }

    //TODO: Create rework of Leaf Press (Fluid Capability, Recipes, etc...)
    public static List<IJeiLeafPressRecipe> getLeafPressRecipes(){

        List<IJeiLeafPressRecipe> list = new ArrayList<>();

        list.add(new LeafPressRecipe(LeafPressBlock.VALID_LEAVES, 0.25f, new ItemStack(BlockRegistry.DRIED_LEAVES)));

        return list;
    }

    public static List<IJeiResourceSheepRecipe> getResourceSheepRecipes(){

        List<IJeiResourceSheepRecipe> list = new ArrayList<>();

        for(ResourceSheepEntity.Type type : ResourceSheepEntity.Type.values()){
            list.add(new ResourceSheepRecipe(type));
        }

        return list;
    }
}

package de.crafty.skylife.jei.recipes.block_transformation;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class BlockTransformationRecipe implements IJeiBlockTransformationRecipe {

    private final Block base;
    private final ItemStack converter;
    private final Block result;

    public BlockTransformationRecipe(Block base, ItemStack converter, Block result){
        this.base = base;
        this.converter = converter;
        this.result = result;

    }

    @Override
    public Block getBase() {
        return this.base;
    }

    @Override
    public ItemStack getConverter() {
        return this.converter;
    }

    @Override
    public Block getResult() {
        return this.result;
    }
}

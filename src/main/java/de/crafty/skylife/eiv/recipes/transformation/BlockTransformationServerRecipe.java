package de.crafty.skylife.eiv.recipes.transformation;

import de.crafty.eiv.api.recipe.IEivServerModRecipe;
import de.crafty.eiv.api.recipe.ModRecipeType;
import de.crafty.eiv.recipe.util.EivTagUtil;
import de.crafty.skylife.SkyLife;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class BlockTransformationServerRecipe implements IEivServerModRecipe {

    public static final ModRecipeType<BlockTransformationServerRecipe> TYPE = ModRecipeType.register(
            ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "block_transformation"),
            () -> new BlockTransformationServerRecipe(null, ItemStack.EMPTY, null)
    );

    private Block base;
    private ItemStack converter;
    private Block result;

    public BlockTransformationServerRecipe(Block base, ItemStack converter, Block result) {
        this.base = base;
        this.converter = converter;
        this.result = result;
    }

    public Block getBase() {
        return this.base;
    }

    public ItemStack getConverter() {
        return this.converter;
    }

    public Block getResult() {
        return this.result;
    }

    @Override
    public void writeToTag(CompoundTag compoundTag) {

        compoundTag.putString("base", EivTagUtil.blockToString(this.base));
        compoundTag.put("converter", EivTagUtil.encodeItemStack(this.converter));
        compoundTag.putString("result", EivTagUtil.blockToString(this.result));

    }

    @Override
    public void loadFromTag(CompoundTag compoundTag) {

        this.base = EivTagUtil.blockFromString(compoundTag.getString("base"));
        this.converter = EivTagUtil.decodeItemStack(compoundTag.getCompound("converter"));
        this.result = EivTagUtil.blockFromString(compoundTag.getString("result"));

    }

    @Override
    public ModRecipeType<? extends IEivServerModRecipe> getRecipeType() {
        return TYPE;
    }
}

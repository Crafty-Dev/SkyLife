package de.crafty.skylife.eiv.recipes.leafpress;

import de.crafty.eiv.api.recipe.IEivServerModRecipe;
import de.crafty.eiv.api.recipe.ModRecipeType;
import de.crafty.eiv.recipe.util.EivTagUtil;
import de.crafty.skylife.SkyLife;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class LeafPressServerRecipe implements IEivServerModRecipe {

    public static final ModRecipeType<LeafPressServerRecipe> TYPE = ModRecipeType.register(
            ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "leaf_press"),
            () -> new LeafPressServerRecipe(List.of(), 0, ItemStack.EMPTY)
    );

    private List<Block> inputs;
    private float amount;
    private ItemStack output;

    public LeafPressServerRecipe(List<Block> inputs, float amount, ItemStack output) {
        this.inputs = inputs;
        this.amount = amount;
        this.output = output;
    }


    public List<Block> getInputs() {
        return this.inputs;
    }

    public float getAmount() {
        return this.amount;
    }

    public ItemStack getOutput() {
        return this.output;
    }

    @Override
    public void writeToTag(CompoundTag compoundTag) {

        compoundTag.put("inputs", EivTagUtil.createBlockList(this.inputs));
        compoundTag.putFloat("amount", this.amount);
        compoundTag.put("output", EivTagUtil.encodeItemStack(this.output));
    }

    @Override
    public void loadFromTag(CompoundTag compoundTag) {

        this.inputs = EivTagUtil.reconstructBlockList(compoundTag, "inputs");
        this.amount = compoundTag.getFloat("amount");
        this.output = EivTagUtil.decodeItemStack(compoundTag.getCompound("output"));
    }

    @Override
    public ModRecipeType<? extends IEivServerModRecipe> getRecipeType() {
        return TYPE;
    }
}

package de.crafty.skylife.eiv.recipes.oil_processing;

import de.crafty.eiv.api.recipe.IEivServerModRecipe;
import de.crafty.eiv.api.recipe.ModRecipeType;
import de.crafty.eiv.recipe.util.EivTagUtil;
import de.crafty.skylife.SkyLife;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class OilProcessingServerRecipe implements IEivServerModRecipe {

    public static final ModRecipeType<OilProcessingServerRecipe> TYPE = ModRecipeType.register(
            ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "oil_processing"),
            () -> new OilProcessingServerRecipe(0, ItemStack.EMPTY, ItemStack.EMPTY, 0)
    );

    private int liquidAmount;
    private ItemStack processingItem;
    private ItemStack result;
    private int processingTime;

    public OilProcessingServerRecipe(int liquidAmount, ItemStack processingItem, ItemStack result, int processingTime) {
        this.liquidAmount = liquidAmount;
        this.processingItem = processingItem;
        this.result = result;
        this.processingTime = processingTime;
    }

    public int getLiquidAmount() {
        return this.liquidAmount;
    }

    public int getProcessingTime() {
        return this.processingTime;
    }

    public ItemStack getProcessingItem() {
        return this.processingItem;
    }

    public ItemStack getResult() {
        return this.result;
    }

    @Override
    public void writeToTag(CompoundTag compoundTag) {

        compoundTag.putInt("liquidAmount", this.liquidAmount);
        compoundTag.putInt("processingTime", this.processingTime);

        compoundTag.put("processingItem", EivTagUtil.encodeItemStack(this.processingItem));
        compoundTag.put("result", EivTagUtil.encodeItemStack(this.result));
    }

    @Override
    public void loadFromTag(CompoundTag compoundTag) {

        this.liquidAmount = compoundTag.getInt("liquidAmount");
        this.processingTime = compoundTag.getInt("processingTime");

        this.processingItem = EivTagUtil.decodeItemStack(compoundTag.getCompound("processingItem"));
        this.result = EivTagUtil.decodeItemStack(compoundTag.getCompound("result"));

    }

    @Override
    public ModRecipeType<? extends IEivServerModRecipe> getRecipeType() {
        return TYPE;
    }
}

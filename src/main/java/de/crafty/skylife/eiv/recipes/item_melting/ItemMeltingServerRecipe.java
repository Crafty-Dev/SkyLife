package de.crafty.skylife.eiv.recipes.item_melting;

import de.crafty.eiv.api.recipe.IEivServerModRecipe;
import de.crafty.eiv.api.recipe.ModRecipeType;
import de.crafty.eiv.recipe.util.EivTagUtil;
import de.crafty.skylife.SkyLife;
import de.crafty.skylife.config.ItemMeltingConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

public class ItemMeltingServerRecipe implements IEivServerModRecipe {

    public static final ModRecipeType<ItemMeltingServerRecipe> TYPE = ModRecipeType.register(
            ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "item_melting"),
            () -> new ItemMeltingServerRecipe(null, null)
    );

    private Item meltable;
    private ItemMeltingConfig.MeltingResult meltingResult;

    public ItemMeltingServerRecipe(Item meltable, ItemMeltingConfig.MeltingResult meltingResult){
        this.meltable = meltable;
        this.meltingResult = meltingResult;
    }

    public Item getMeltable() {
        return this.meltable;
    }

    public ItemMeltingConfig.MeltingResult getMeltingResult() {
        return this.meltingResult;
    }

    @Override
    public void writeToTag(CompoundTag compoundTag) {

        compoundTag.putString("meltable", EivTagUtil.itemToString(this.meltable));

        CompoundTag resultTag = new CompoundTag();
        resultTag.putString("fluid", EivTagUtil.fluidToString(this.meltingResult.fluid()));
        resultTag.putInt("amount", this.meltingResult.amount());
        resultTag.putInt("meltingTime", this.meltingResult.meltingTime());
        compoundTag.put("result", resultTag);
    }

    @Override
    public void loadFromTag(CompoundTag compoundTag) {

        this.meltable = EivTagUtil.itemFromString(compoundTag.getString("meltable"));

        CompoundTag resultTag = compoundTag.getCompound("result");
        Fluid fluid = EivTagUtil.fluidFromString(resultTag.getString("fluid"));
        int amount = resultTag.getInt("amount");
        int meltingTime = resultTag.getInt("meltingTime");
        this.meltingResult = new ItemMeltingConfig.MeltingResult(fluid, amount, meltingTime);

    }

    @Override
    public ModRecipeType<? extends IEivServerModRecipe> getRecipeType() {
        return TYPE;
    }
}

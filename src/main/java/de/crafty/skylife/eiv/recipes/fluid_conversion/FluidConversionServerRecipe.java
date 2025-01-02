package de.crafty.skylife.eiv.recipes.fluid_conversion;

import com.mojang.serialization.DataResult;
import de.crafty.eiv.api.recipe.IEivServerModRecipe;
import de.crafty.eiv.api.recipe.ModRecipeType;
import de.crafty.eiv.recipe.util.EivTagUtil;
import de.crafty.skylife.SkyLife;
import de.crafty.skylife.config.FluidConversionConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

import java.util.List;

public class FluidConversionServerRecipe implements IEivServerModRecipe {

    public static final ModRecipeType<FluidConversionServerRecipe> TYPE = ModRecipeType.register(
            ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "fluid_conversion"),
            () -> new FluidConversionServerRecipe(null, List.of())
    );

    private Item ingredient;
    private List<FluidConversionConfig.FluidDrop> drops;

    public FluidConversionServerRecipe(Item ingredient, List<FluidConversionConfig.FluidDrop> fluidDrops) {
        this.ingredient = ingredient;
        this.drops = fluidDrops;
    }

    public Item getIngredient() {
        return this.ingredient;
    }

    public List<FluidConversionConfig.FluidDrop> getDrops() {
        return this.drops;
    }

    @Override
    public void writeToTag(CompoundTag compoundTag) {

        compoundTag.putString("ingredient", EivTagUtil.itemToString(this.ingredient));

        compoundTag.put("drops", EivTagUtil.writeList(this.drops, (fluidDrop, tag) -> {
            tag.putString("requiredFluid", EivTagUtil.fluidToString(fluidDrop.requiredFluid()));
            tag.putString("output", EivTagUtil.itemToString(fluidDrop.output()));
            tag.putFloat("chance", fluidDrop.chance());
            tag.putInt("min", fluidDrop.min());
            tag.putInt("max", fluidDrop.max());
            tag.putFloat("bonusChance", fluidDrop.bonusChance());
            tag.putBoolean("dropSeperate", fluidDrop.dropSeperate());
            return tag;
        }));

    }

    @Override
    public void loadFromTag(CompoundTag compoundTag) {

        this.ingredient = EivTagUtil.itemFromString(compoundTag.getString("ingredient"));

        this.drops = EivTagUtil.readList(compoundTag, "drops", tag -> {
            return new FluidConversionConfig.FluidDrop(
                    EivTagUtil.fluidFromString(tag.getString("requiredFluid")),
                    EivTagUtil.itemFromString(tag.getString("output")),
                    tag.getFloat("chance"),
                    tag.getInt("min"),
                    tag.getInt("max"),
                    tag.getFloat("bonusChance"),
                    tag.getBoolean("dropSeperate")
            );
        });

    }

    @Override
    public ModRecipeType<? extends IEivServerModRecipe> getRecipeType() {
        return TYPE;
    }
}

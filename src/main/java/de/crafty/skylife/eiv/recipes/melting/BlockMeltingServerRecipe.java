package de.crafty.skylife.eiv.recipes.melting;

import com.google.gson.JsonParser;
import de.crafty.eiv.api.recipe.IEivServerModRecipe;
import de.crafty.eiv.api.recipe.ModRecipeType;
import de.crafty.eiv.recipe.util.EivTagUtil;
import de.crafty.skylife.SkyLife;
import de.crafty.skylife.config.BlockMeltingConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.List;

public class BlockMeltingServerRecipe implements IEivServerModRecipe {

    public static final ModRecipeType<BlockMeltingServerRecipe> TYPE = ModRecipeType.register(
            ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "block_melting"),
            () -> new BlockMeltingServerRecipe(null, null, null)
    );

    private Block meltable;
    private Fluid liquid;
    private BlockMeltingConfig.HeatSource heatSource;

    public BlockMeltingServerRecipe(Block meltable, Fluid moltenLiquid, BlockMeltingConfig.HeatSource heatSource) {
        this.meltable = meltable;
        this.liquid = moltenLiquid;
        this.heatSource = heatSource;
    }

    public Block getMeltable() {
        return this.meltable;
    }

    public Fluid getLiquid() {
        return this.liquid;
    }

    public BlockMeltingConfig.HeatSource getHeatSource() {
        return this.heatSource;
    }

    @Override
    public void writeToTag(CompoundTag compoundTag) {

        compoundTag.putString("meltable", EivTagUtil.blockToString(this.meltable));
        compoundTag.putString("fluid", EivTagUtil.fluidToString(this.liquid));

        CompoundTag sourceTag = new CompoundTag();
        sourceTag.putString("heatBlock", EivTagUtil.blockToString(this.heatSource.heatBlock()));
        sourceTag.put("conditions", EivTagUtil.writeList(this.heatSource.conditions(), (blockMeltingCondition, tag) -> {
            tag.putString("encoded", blockMeltingCondition.encode().toString());
            return tag;
        }));

        sourceTag.putFloat("efficiency", this.heatSource.heatEfficiency());
        sourceTag.put("representable", EivTagUtil.encodeItemStack(this.heatSource.representable()));

        compoundTag.put("heatSource", sourceTag);
    }

    @Override
    public void loadFromTag(CompoundTag compoundTag) {

        this.meltable = EivTagUtil.blockFromString(compoundTag.getString("meltable"));
        this.liquid = EivTagUtil.fluidFromString(compoundTag.getString("fluid"));

        CompoundTag sourceTag = compoundTag.getCompound("heatSource");

        Block block = EivTagUtil.blockFromString(sourceTag.getString("heatBlock"));
        List<BlockMeltingConfig.BlockMeltingCondition> conditions = EivTagUtil.readList(sourceTag, "conditions", tag -> {
           return BlockMeltingConfig.BlockMeltingCondition.decodeCondition(JsonParser.parseString(tag.getString("encoded")).getAsJsonObject());
        });
        float efficiency = sourceTag.getFloat("efficiency");
        ItemStack representable = EivTagUtil.decodeItemStack(sourceTag.getCompound("representable"));
        this.heatSource = new BlockMeltingConfig.HeatSource(block, conditions, efficiency, representable);
    }

    @Override
    public ModRecipeType<? extends IEivServerModRecipe> getRecipeType() {
        return TYPE;
    }
}

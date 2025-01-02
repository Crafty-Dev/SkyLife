package de.crafty.skylife.eiv.recipes.hammering;

import de.crafty.eiv.api.recipe.IEivServerModRecipe;
import de.crafty.eiv.api.recipe.ModRecipeType;
import de.crafty.eiv.recipe.util.EivTagUtil;
import de.crafty.skylife.SkyLife;
import de.crafty.skylife.config.HammerConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class HammeringServerRecipe implements IEivServerModRecipe {

    public static final ModRecipeType<HammeringServerRecipe> TYPE = ModRecipeType.register(
            ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "hammering"),
            () -> new HammeringServerRecipe(List.of(), List.of())
    );

    private List<Block> blocks;
    private List<HammerConfig.HammerDrop> hammerDrops;

    public HammeringServerRecipe(List<Block> blocks, List<HammerConfig.HammerDrop> hammerDrops) {
        this.blocks = blocks;
        this.hammerDrops = hammerDrops;
    }

    public List<Block> getBlocks() {
        return this.blocks;
    }

    public List<HammerConfig.HammerDrop> getHammerDrops() {
        return this.hammerDrops;
    }

    @Override
    public void writeToTag(CompoundTag compoundTag) {

        ListTag blocks = EivTagUtil.createBlockList(this.blocks);
        compoundTag.put("blocks", blocks);

        ListTag hammerDrops = EivTagUtil.writeList(this.hammerDrops, (hammerDrop, tag) -> {
            tag.putString("item", EivTagUtil.itemToString(hammerDrop.item()));
            tag.putInt("min", hammerDrop.min());
            tag.putInt("max", hammerDrop.max());
            tag.putFloat("chance", hammerDrop.chance());
            tag.putFloat("bonusChance", hammerDrop.bonusChance());
            return tag;
        });
        compoundTag.put("hammerDrops", hammerDrops);
    }

    @Override
    public void loadFromTag(CompoundTag compoundTag) {

        this.blocks = EivTagUtil.reconstructBlockList(compoundTag, "blocks");

        this.hammerDrops = EivTagUtil.readList(compoundTag, "hammerDrops", tag -> new HammerConfig.HammerDrop(
                EivTagUtil.itemFromString(tag.getString("item")),
                tag.getFloat("chance"),
                tag.getInt("min"),
                tag.getInt("max"),
                tag.getFloat("bonusChance")
        ));

    }

    @Override
    public ModRecipeType<? extends IEivServerModRecipe> getRecipeType() {
        return TYPE;
    }
}

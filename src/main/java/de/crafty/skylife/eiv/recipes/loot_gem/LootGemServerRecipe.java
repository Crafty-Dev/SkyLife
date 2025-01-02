package de.crafty.skylife.eiv.recipes.loot_gem;

import de.crafty.eiv.api.recipe.IEivServerModRecipe;
import de.crafty.eiv.api.recipe.ModRecipeType;
import de.crafty.eiv.recipe.util.EivTagUtil;
import de.crafty.skylife.SkyLife;
import de.crafty.skylife.item.LootGemItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class LootGemServerRecipe implements IEivServerModRecipe {

    public static final ModRecipeType<LootGemServerRecipe> TYPE = ModRecipeType.register(
            ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "loot_gem"),
            () -> new LootGemServerRecipe(null, null)
    );


    private Item gem;
    private LootGemItem.LootContent loot;

    public LootGemServerRecipe(Item gem, LootGemItem.LootContent loot) {
        this.gem = gem;
        this.loot = loot;
    }

    public Item getGem() {
        return this.gem;
    }

    public LootGemItem.LootContent getLoot() {
        return this.loot;
    }

    @Override
    public void writeToTag(CompoundTag compoundTag) {

        compoundTag.putString("gem", EivTagUtil.itemToString(this.gem));

        ListTag contentTag = EivTagUtil.writeList(this.loot.lootEntries(), (lootEntry, encoded) -> {
            encoded.put("stack", ItemStack.CODEC.encode(lootEntry.loot(), NbtOps.INSTANCE, new CompoundTag()).getOrThrow());
            encoded.putInt("weight", lootEntry.weight());
            encoded.putInt("min", lootEntry.min());
            encoded.putInt("max", lootEntry.max());

            return encoded;
        });

        compoundTag.put("content", contentTag);

        CompoundTag entityChanceTag = new CompoundTag();
        this.loot.entityChances().forEach((entityType, aFloat) -> {
            entityChanceTag.putFloat(EntityType.getKey(entityType).toString(), aFloat);
        });

        compoundTag.put("entityChances", entityChanceTag);
    }

    @Override
    public void loadFromTag(CompoundTag compoundTag) {
        System.out.println("Received: " + compoundTag);

        this.gem = EivTagUtil.itemFromString(compoundTag.getString("gem"));

        List<LootGemItem.LootEntry> lootEntries = EivTagUtil.readList(compoundTag, "content", encoded -> {
            ItemStack lootStack = ItemStack.CODEC.decode(NbtOps.INSTANCE, encoded.getCompound("stack")).getOrThrow().getFirst();
            int weight = encoded.getInt("weight");
            int min = encoded.getInt("min");
            int max = encoded.getInt("max");

            return new LootGemItem.LootEntry(lootStack, weight, min, max);
        });

        HashMap<EntityType<?>, Float> entityChances = new LinkedHashMap<>();
        CompoundTag entityChanceTag = compoundTag.getCompound("entityChances");

        entityChanceTag.getAllKeys().forEach(entityTypeId -> {
            EntityType<?> entityType = EntityType.byString(entityTypeId).orElseThrow();

            float chance = entityChanceTag.getFloat(entityTypeId);
            entityChances.put(entityType, chance);
        });

        this.loot = new LootGemItem.LootContent(lootEntries, entityChances);
    }

    @Override
    public ModRecipeType<? extends IEivServerModRecipe> getRecipeType() {
        return TYPE;
    }
}

package de.crafty.skylife.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import de.crafty.skylife.item.LootGemItem;
import de.crafty.skylife.registry.BlockRegistry;
import de.crafty.skylife.registry.ItemRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.OminousBottleAmplifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class LootGemConfig extends AbstractSkyLifeConfig {

    private final HashMap<Item, LootGemItem.LootContent> gems = new LinkedHashMap<>();

    protected LootGemConfig() {
        super("lootGems");
    }

    public HashMap<Item, LootGemItem.LootContent> getGems() {
        return this.gems;
    }

    public LootGemItem.LootContent forItem(Item item) {
        return this.gems.getOrDefault(item, null);
    }

    public HashMap<Item, LootGemItem.LootContent> getPossibleGems(EntityType<?> entityType) {
        HashMap<Item, LootGemItem.LootContent> possibleGems = new HashMap<>();
        this.gems.forEach((item, lootContent) -> {
            if (!lootContent.entityChances().containsKey(entityType))
                return;

            possibleGems.put(item, lootContent);
        });

        return possibleGems;
    }

    @Override
    protected void setDefaults() {
        this.registerDefaults();
        this.encodeLoot();
    }


    @Override
    public void load() {
        super.load();

        this.decodeLoot();
    }


    private void encodeLoot() {

        this.gems.forEach((gem, lootContent) -> {
            String itemId = BuiltInRegistries.ITEM.getKey(gem).toString();
            JsonObject contentJson = new JsonObject();

            JsonArray lootJson = new JsonArray();
            lootContent.lootEntries().forEach(entry -> {
                JsonObject lootEntry = new JsonObject();
                lootEntry.add("stack", ItemStack.CODEC.encode(entry.loot(), JsonOps.INSTANCE, new JsonObject()).getOrThrow());
                lootEntry.addProperty("weight", entry.weight());
                lootEntry.addProperty("min", entry.min());
                lootEntry.addProperty("max", entry.max());
                lootJson.add(lootEntry);
            });

            JsonObject entityJson = new JsonObject();
            lootContent.entityChances().forEach((entityType, aFloat) -> {
                entityJson.addProperty(EntityType.getKey(entityType).toString(), aFloat);
            });

            contentJson.add("loot", lootJson);
            contentJson.add("entityChances", entityJson);

            this.data().add(itemId, contentJson);
        });

    }

    private void decodeLoot() {

        this.data().keySet().forEach(gemId -> {
            Item gem = BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(gemId));

            JsonArray lootArray = this.data().getAsJsonObject(gemId).getAsJsonArray("loot");
            List<LootGemItem.LootEntry> lootEntries = new ArrayList<>();

            lootArray.forEach(encodedLootEntry -> {
                JsonObject lootEntryJson = encodedLootEntry.getAsJsonObject();
                ItemStack stack = ItemStack.CODEC.decode(JsonOps.INSTANCE, lootEntryJson.getAsJsonObject("stack")).getOrThrow().getFirst();
                int weight = lootEntryJson.get("weight").getAsInt();
                int min = lootEntryJson.get("min").getAsInt();
                int max = lootEntryJson.get("max").getAsInt();
                lootEntries.add(new LootGemItem.LootEntry(stack, weight, min, max));
            });

            JsonObject entityJson = this.data().getAsJsonObject(gemId).getAsJsonObject("entityChances");
            HashMap<EntityType<?>, Float> entityChances = new HashMap<>();

            entityJson.keySet().forEach(entityId -> {
                EntityType<?> entity = EntityType.byString(entityId).orElseThrow(() -> new JsonParseException("Unknown entity type " + entityId));
                float chance = entityJson.get(entityId).getAsFloat();
                entityChances.put(entity, chance);
            });

            this.gems.put(gem, new LootGemItem.LootContent(lootEntries, entityChances));
        });

    }


    private void registerDefaults() {
        this.registerLootContent(ItemRegistry.STONY_LOOT_GEM, new LootGemItem.LootContent.Builder()
                .addEntry(Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, 1)
                .addEntry(Items.COBBLESTONE, 3, 10, 64)
                .addEntry(Items.STONE, 3, 5, 32)
                .addEntry(ItemRegistry.STONE_PIECE, 3, 16, 48)
                .addEntry(ItemRegistry.COBBLESTONE_ENRICHED_WHEAT, 2, 1, 3)
                .addEntry(Items.STONE_SWORD, 2)
                .addEntry(Items.STONE_PICKAXE, 2)
                .addEntry(Items.STONE_AXE, 2)
                .addEntry(Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE, 1)
                .addEntry(Items.STONE_SHOVEL, 2)
                .addEntry(Items.STONE_HOE, 2)
                .addEntry(ItemRegistry.STONE_HAMMER, 2)
                .addEntry(Items.STONE_BUTTON, 2, 2, 4)
                .addEntry(Items.STONE_PRESSURE_PLATE, 2, 1, 2)
                .fromEntity(EntityType.SKELETON, 0.1F)
                .fromEntity(EntityType.ZOMBIE, 0.1F)
                .addEntry(Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, 1)
                .fromEntity(EntityType.SPIDER, 0.15F)
                .fromEntity(EntityType.CREEPER, 0.2F)
                .build()
        );

        this.registerLootContent(ItemRegistry.SANDY_LOOT_GEM, new LootGemItem.LootContent.Builder()
                .addEntry(Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, 1)
                .addEntry(Items.SAND, 1, 12, 36)
                .addEntry(Items.RED_SAND, 2, 8, 24)
                .addEntry(Items.SANDSTONE, 2, 4, 12)
                .addEntry(Items.SOUL_SAND, 2, 1, 2)
                .addEntry(Items.DEAD_BUSH, 3, 1, 4)
                .addEntry(Items.CACTUS, 2, 1, 3)
                .fromEntity(EntityType.HUSK, 0.5F)
                .build()
        );

        this.registerLootContent(ItemRegistry.NETHER_LOOT_GEM, new LootGemItem.LootContent.Builder()
                .addEntry(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, 1)
                .addEntry(Items.NETHERRACK, 3, 5, 12)
                .addEntry(Items.SOUL_SAND, 4, 1, 18)
                .addEntry(Items.WARPED_FUNGUS, 4, 1, 2)
                .addEntry(Items.WARPED_HYPHAE, 3, 1, 5)
                .addEntry(Items.CRIMSON_HYPHAE, 3, 1, 4)
                .addEntry(Items.CRIMSON_STEM, 3, 1, 7)
                .addEntry(Items.WARPED_STEM, 3, 1, 7)
                .addEntry(ItemRegistry.NETHERRACK_PIECE, 3, 7, 23)
                .addEntry(Items.CRIMSON_FUNGUS, 4, 1, 2)
                .addEntry(Items.WARPED_FUNGUS_ON_A_STICK, 2)
                .addEntry(ItemRegistry.NETHERRACK_ENRICHED_WHEAT, 1, 1, 3)
                .addEntry(Items.IRON_INGOT, 2, 1, 6)
                .addEntry(Items.GOLD_INGOT, 2, 1, 4)
                .addEntry(Items.NETHER_SPROUTS, 3, 1, 5)
                .addEntry(Items.CRIMSON_ROOTS, 3, 1, 3)
                .addEntry(Items.WARPED_ROOTS, 3, 1, 2)
                .addEntry(Items.SOUL_TORCH, 4, 1, 4)
                .addEntry(Items.NETHER_QUARTZ_ORE, 2)
                .addEntry(Items.WEEPING_VINES, 4, 1, 4)
                .addEntry(Items.TWISTING_VINES, 3, 1, 2)
                .fromEntity(EntityType.ZOMBIFIED_PIGLIN, 0.25F)
                .fromEntity(EntityType.BLAZE, 0.35F)
                .fromEntity(EntityType.WITHER_SKELETON, 0.4F)
                .build()
        );

        this.registerLootContent(ItemRegistry.ANCIENT_LOOT_GEM, new LootGemItem.LootContent.Builder()
                .addEntry(Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, 2)
                .addEntry(Items.NETHERRACK, 2, 24, 56)
                .addEntry(ItemRegistry.NETHERRACK_PIECE, 3, 32, 64)
                .addEntry(Items.SOUL_SAND, 3, 8, 24)
                .addEntry(Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, 1)
                .addEntry(Items.BASALT, 3, 2, 16)
                .addEntry(ItemRegistry.NETHERITE_ENRICHED_WHEAT, 1, 1, 2)
                .addEntry(Items.OBSIDIAN, 1, 1, 8)
                .fromEntity(EntityType.GHAST, 0.1F)
                .fromEntity(EntityType.WITHER_SKELETON, 0.05F)
                .fromEntity(EntityType.BLAZE, 0.05F)
                .build()
        );

        this.registerLootContent(ItemRegistry.FISHY_LOOT_GEM, new LootGemItem.LootContent.Builder()
                .addEntry(Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, 1)
                .addEntry(Items.PRISMARINE_SHARD, 2, 1, 3)
                .addEntry(Items.PRISMARINE_CRYSTALS, 2, 1, 5)
                .addEntry(Items.SEA_LANTERN, 2, 1, 2)
                .addEntry(Items.PRISMARINE, 3, 1, 2)
                .addEntry(Items.PRISMARINE_BRICKS, 3, 1, 2)
                .addEntry(Items.WATER_BUCKET, 2)
                .addEntry(ItemRegistry.WOODEN_WATER_BUCKET, 2)
                .addEntry(Items.GRAVEL, 4, 2, 8)
                .addEntry(Items.SAND, 4, 2, 8)
                .addEntry(Items.SPONGE, 1, 1, 2)
                .addEntry(Items.SEA_PICKLE, 3, 1, 4)
                .addEntry(Items.TUBE_CORAL, 3, 1, 3)
                .addEntry(Items.TUBE_CORAL_BLOCK, 2, 1, 2)
                .addEntry(Items.BRAIN_CORAL, 3, 1, 3)
                .addEntry(Items.BRAIN_CORAL_BLOCK, 2, 1, 2)
                .addEntry(Items.BUBBLE_CORAL, 3, 1, 3)
                .addEntry(Items.BUBBLE_CORAL_BLOCK, 2, 1, 2)
                .addEntry(Items.FIRE_CORAL, 3, 1, 3)
                .addEntry(Items.FIRE_CORAL_BLOCK, 2, 1, 2)
                .addEntry(Items.HORN_CORAL, 3, 1, 3)
                .addEntry(Items.HORN_CORAL_BLOCK, 2, 1, 2)
                .fromEntity(EntityType.COD, 0.65F)
                .fromEntity(EntityType.SALMON, 0.7F)
                .fromEntity(EntityType.SQUID, 0.75F)
                .fromEntity(EntityType.GLOW_SQUID, 0.8F)
                .build()
        );

        this.registerLootContent(ItemRegistry.DIRTY_LOOT_GEM, new LootGemItem.LootContent.Builder()
                .addEntry(Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE, 1)
                .addEntry(Items.DIRT, 4, 3, 9)
                .addEntry(Items.WHEAT_SEEDS, 4, 2, 6)
                .addEntry(Items.OAK_SAPLING, 3, 1, 2)
                .addEntry(Items.OAK_PLANKS, 3, 4, 14)
                .addEntry(Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE, 1)
                .addEntry(Items.COARSE_DIRT, 3, 2, 7)
                .addEntry(Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE, 1)
                .addEntry(Items.OAK_LOG, 5, 4, 12)
                .addEntry(Items.ROOTED_DIRT, 2, 2, 5)
                .addEntry(Items.WHEAT, 4, 1, 6)
                .addEntry(Items.LADDER, 5, 3, 12)
                .addEntry(ItemRegistry.DIRT_ENRICHED_WHEAT, 3, 2, 4)
                .addEntry(Items.TORCH, 4, 6, 18)
                .addEntry(Items.HAY_BLOCK, 3, 2, 4)
                .fromEntity(EntityType.CHICKEN, 0.5F)
                .fromEntity(EntityType.PIG, 0.5F)
                .addEntry(Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, 1)
                .fromEntity(EntityType.COW, 0.5F)
                .fromEntity(EntityType.SHEEP, 0.5F)
                .build()
        );

        this.registerLootContent(ItemRegistry.EVIL_LOOT_GEM, new LootGemItem.LootContent.Builder()
                .addEntry(Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE, 2)
                .addEntry(Items.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE, 1)
                .addEntry(Items.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE, 1)
                .addEntry(Items.EMERALD, 5, 1, 9)
                .addEntry(Items.EMERALD_BLOCK, 5, 1, 2)
                .addEntry(this.createOmniousBottle(0), 4)
                .addEntry(this.createOmniousBottle(1), 4)
                .addEntry(this.createOmniousBottle(2), 3)
                .addEntry(this.createOmniousBottle(3), 3)
                .addEntry(this.createOmniousBottle(4), 2)
                .addEntry(Items.ARROW, 5, 1, 24)
                .addEntry(Items.BREAD, 5, 2, 12)
                .addEntry(Items.CROSSBOW, 3)
                .addEntry(Items.IRON_AXE, 3)
                .addEntry(Items.TOTEM_OF_UNDYING, 2)
                .addEntry(Items.SADDLE, 3)
                .addEntry(Items.BELL, 3)
                .fromEntity(EntityType.PILLAGER, 0.55F)
                .fromEntity(EntityType.RAVAGER, 0.8F)
                .fromEntity(EntityType.VINDICATOR, 0.6F)
                .fromEntity(EntityType.EVOKER, 0.7F)
                .build()
        );

        this.registerLootContent(ItemRegistry.END_LOOT_GEM, new LootGemItem.LootContent.Builder()
                .addEntry(Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, 1)
                .addEntry(Items.END_STONE, 4, 4, 12)
                .addEntry(Items.END_STONE_BRICKS, 4, 2, 8)
                .addEntry(Items.ENDER_EYE, 3, 1, 2)
                .addEntry(Items.ENDER_PEARL, 3, 1, 4)
                .addEntry(ItemRegistry.END_NETHERITE_ORE_DUST, 2, 1, 2)
                .addEntry(BlockRegistry.END_DIAMOND_ORE.asItem(), 2)
                .addEntry(BlockRegistry.END_NETHERITE_ORE.asItem(), 2)
                .addEntry(BlockRegistry.END_PORTAL_FRAME.asItem(), 1)
                .fromEntity(EntityType.ENDERMAN, 0.15F)
                .fromEntity(EntityType.ENDERMITE, 0.4F)
                .build()
        );

        this.registerLootContent(ItemRegistry.SHULKY_LOOT_GEM, new LootGemItem.LootContent.Builder()
                .addEntry(Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, 1)
                .addEntry(Items.SHULKER_SHELL, 2, 1, 2)
                .addEntry(Items.END_ROD, 3, 2, 4)
                .addEntry(Items.PURPUR_BLOCK, 3, 5, 17)
                .addEntry(Items.PURPUR_PILLAR, 3, 5, 17)
                .addEntry(Items.SHULKER_BOX, 1)
                .addEntry(Items.BREWING_STAND, 3)
                .addEntry(Items.OBSIDIAN, 3, 1, 3)
                .addEntry(Items.ENDER_PEARL, 4, 1, 16)
                .addEntry(Items.DIAMOND, 3, 1, 3)
                .addEntry(Items.GOLD_INGOT, 3, 1, 6)
                .addEntry(Items.IRON_INGOT, 3, 1, 12)
                .addEntry(this.createPotion(Potions.STRONG_HEALING), 1)
                .addEntry(Items.DRAGON_HEAD, 2)
                .addEntry(Items.CHORUS_FRUIT, 4, 1, 4)
                .fromEntity(EntityType.SHULKER, 0.3F)
                .build()
        );

        this.registerLootContent(ItemRegistry.TERRIFYING_LOOT_GEM, new LootGemItem.LootContent.Builder()
                .addEntry(Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, 5)
                .addEntry(Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, 1)
                .addEntry(Items.NETHER_STAR, 1)
                .addEntry(Items.RESIN_CLUMP, 20, 4, 16)
                .addEntry(Items.RESIN_BRICK, 18, 4, 16)
                .addEntry(Items.EXPERIENCE_BOTTLE, 10, 16, 64)
                .addEntry(Items.DIAMOND_BLOCK, 8, 2, 10)
                .addEntry(Items.NETHERITE_INGOT, 5)
                .fromEntity(EntityType.ENDER_DRAGON, 1.0F)
                .fromEntity(EntityType.WITHER, 1.0F)
                .build()
        );

    }


    private ItemStack createOmniousBottle(int omniousAmplifier) {
        ItemStack stack = new ItemStack(Items.OMINOUS_BOTTLE);
        stack.set(DataComponents.OMINOUS_BOTTLE_AMPLIFIER, new OminousBottleAmplifier(omniousAmplifier));
        return stack;
    }

    private ItemStack createPotion(Holder<Potion> potion) {
        return PotionContents.createItemStack(Items.POTION, potion);
    }

    private void registerLootContent(Item gem, LootGemItem.LootContent lootContent) {
        this.gems.put(gem, lootContent);
    }
}

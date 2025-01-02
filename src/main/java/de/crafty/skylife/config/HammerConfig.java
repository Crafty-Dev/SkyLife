package de.crafty.skylife.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.crafty.skylife.registry.ItemRegistry;

import java.util.*;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class HammerConfig extends AbstractSkyLifeConfig {

    private LinkedHashMap<Block, List<HammerDrop>> drops = new LinkedHashMap<>();

    protected HammerConfig() {
        super("hammer");
    }


    @Override
    protected void setDefaults() {

        //Generate default Hammer Recipes
        this.registerDefaultRecipes();
        //Recipe encoding in Data-JsonObject
        this.encodeRecipes();
        //At which hammer-tier items should start dropping together
        this.data().addProperty("precisionDropTier", "GOLD".toLowerCase());
    }

    @Override
    public void load() {
        super.load();
        this.decodeRecipes();
    }

    public ToolMaterial getPrecisionDropTier() {
        String tierString = this.data().get("precisionDropTier").getAsString().toUpperCase();
        return switch (tierString) {
            case "STONE" -> ToolMaterial.STONE;
            case "IRON" -> ToolMaterial.IRON;
            case "GOLD" -> ToolMaterial.GOLD;
            case "DIAMOND" -> ToolMaterial.DIAMOND;
            case "NETHERITE" -> ToolMaterial.NETHERITE;
            default -> ToolMaterial.WOOD;
        };
    }

    private void decodeRecipes() {

        LinkedHashMap<Block, List<HammerDrop>> hammerDrops = new LinkedHashMap<>();

        JsonArray dropConfig = this.data().getAsJsonArray("dropConfig");
        dropConfig.forEach(e -> {
            JsonObject singleConfig = e.getAsJsonObject();
            JsonArray blockGroup = singleConfig.getAsJsonArray("blockGroup");
            JsonArray outputs = singleConfig.getAsJsonArray("outputs");

            List<Block> blocks = new ArrayList<>();
            List<HammerDrop> drops = new ArrayList<>();

            blockGroup.forEach(e1 -> {
                String id = e1.getAsString();
                blocks.add(BuiltInRegistries.BLOCK.getValue(ResourceLocation.tryParse(id)));
            });
            outputs.forEach(e1 -> {
                JsonObject output = e1.getAsJsonObject();
                Item item = BuiltInRegistries.ITEM.getValue(ResourceLocation.tryParse(output.get("item").getAsString()));
                float chance = output.get("chance").getAsFloat();
                int min = output.get("min").getAsInt();
                int max = output.get("max").getAsInt();
                float bonusChance = output.get("bonusChance").getAsFloat();
                drops.add(new HammerDrop(item, chance, min, max, bonusChance));
            });
            blocks.forEach(block -> hammerDrops.put(block, drops));
        });
        this.drops = hammerDrops;

    }

    private void encodeRecipes() {

        JsonArray dropConfig = new JsonArray();

        //Summarize Blocks with same drops
        LinkedHashMap<List<HammerDrop>, List<Block>> compressed = new LinkedHashMap<>();
        this.drops.forEach((block, hammerDrops) -> {
            if (compressed.containsKey(hammerDrops)) {
                compressed.get(hammerDrops).add(block);
                return;
            }
            List<Block> list = new ArrayList<>();
            list.add(block);
            compressed.put(hammerDrops, list);
        });

        //Write Recipes to Json Object
        compressed.forEach((hammerDrops, blocks) -> {
            JsonArray blockGroup = new JsonArray();
            JsonArray outputs = new JsonArray();

            blocks.forEach(block -> blockGroup.add(BuiltInRegistries.BLOCK.wrapAsHolder(block).getRegisteredName()));
            hammerDrops.forEach(hammerDrop -> {
                JsonObject output = new JsonObject();
                output.addProperty("item", BuiltInRegistries.ITEM.wrapAsHolder(hammerDrop.item()).getRegisteredName());
                output.addProperty("chance", hammerDrop.chance());
                output.addProperty("min", hammerDrop.min());
                output.addProperty("max", hammerDrop.max());
                output.addProperty("bonusChance", hammerDrop.bonusChance());
                outputs.add(output);
            });

            JsonObject singleConfig = new JsonObject();
            singleConfig.add("blockGroup", blockGroup);
            singleConfig.add("outputs", outputs);

            dropConfig.add(singleConfig);
        });

        this.data().add("dropConfig", dropConfig);
    }

    public LinkedHashMap<Block, List<HammerDrop>> getDrops() {
        return drops;
    }

    private void registerDrops(Block block, HammerDrop... drops) {
        this.drops.put(block, List.of(drops));
    }

    private void registerDrops(List<Block> blocks, HammerDrop... drops) {
        blocks.forEach(block -> this.drops.put(block, List.of(drops)));
    }

    public record HammerDrop(Item item, float chance, int min, int max, float bonusChance) {
    }

    public void registerDefaultRecipes() {

        this.registerDrops(Blocks.MUD,
                new HammerDrop(Items.KELP, 0.5F, 1, 2, 0.75F),
                new HammerDrop(Items.SEA_PICKLE, 0.2F, 1, 3, 0.25F)
        );

        this.registerDrops(Blocks.ANDESITE, new HammerDrop(Items.TUFF, 1.0F, 1, 1, 0.0F));

        this.registerDrops(Blocks.DIRT,
                new HammerDrop(ItemRegistry.STONE_PIECE, 1.0F, 2, 5, 0.65F)
        );

        this.registerDrops(List.of(Blocks.COBBLESTONE),
                new HammerDrop(ItemRegistry.COAL_ORE_DUST, 0.5F, 1, 3, 0.5F),
                new HammerDrop(ItemRegistry.IRON_ORE_DUST, 0.25F, 1, 2, 0.25F),
                new HammerDrop(ItemRegistry.COPPER_ORE_DUST, 0.25F, 1, 2, 0.25F),
                new HammerDrop(ItemRegistry.STONE_PIECE, 1.0F, 2, 5, 0.4F)
        );


        this.registerDrops(Blocks.STONE,
                new HammerDrop(ItemRegistry.COAL_ORE_DUST, 0.65F, 1, 4, 0.5F),
                new HammerDrop(ItemRegistry.COPPER_ORE_DUST, 0.4F, 1, 3, 0.4F),
                new HammerDrop(ItemRegistry.IRON_ORE_DUST, 0.35F, 1, 2, 0.5F),
                new HammerDrop(ItemRegistry.GOLD_ORE_DUST, 0.1F, 1, 2, 0.2F),
                new HammerDrop(ItemRegistry.REDSTONE_ORE_DUST, 0.5F, 1, 4, 0.5F),
                new HammerDrop(ItemRegistry.LAPIS_ORE_DUST, 0.4F, 1, 3, 0.4F),
                new HammerDrop(ItemRegistry.STONE_PIECE, 1.0F, 2, 5, 0.5F)
        );

        this.registerDrops(Blocks.DEEPSLATE,
                new HammerDrop(ItemRegistry.IRON_ORE_DUST, 0.75F, 1, 3, 0.65F),
                new HammerDrop(ItemRegistry.REDSTONE_ORE_DUST, 0.65F, 1, 4, 0.5F),
                new HammerDrop(ItemRegistry.LAPIS_ORE_DUST, 0.5F, 1, 4, 0.5F),
                new HammerDrop(ItemRegistry.DIAMOND_ORE_DUST, 0.4F, 1, 2, 0.35F),
                new HammerDrop(ItemRegistry.EMERALD_ORE_DUST, 0.5F, 1, 3, 0.2F),
                new HammerDrop(ItemRegistry.GOLD_ORE_DUST, 0.25F, 1, 2, 0.4F)

        );


        this.registerDrops(Blocks.NETHERRACK,
                new HammerDrop(ItemRegistry.GLOWSTONE_ORE_DUST, 0.65F, 1, 3, 0.5F),
                new HammerDrop(ItemRegistry.NETHERITE_ORE_DUST, 0.05F, 1, 1, 0.0F),
                new HammerDrop(ItemRegistry.NETHERRACK_PIECE, 0.75F, 1, 2, 0.5F)
        );


        this.registerDrops(List.of(
                Blocks.OAK_LOG,
                Blocks.BIRCH_LOG,
                Blocks.SPRUCE_LOG,
                Blocks.DARK_OAK_LOG,
                Blocks.ACACIA_LOG,
                Blocks.JUNGLE_LOG,
                Blocks.MANGROVE_LOG,
                Blocks.CHERRY_LOG,
                Blocks.PALE_OAK_LOG,
                Blocks.OAK_WOOD,
                Blocks.BIRCH_WOOD,
                Blocks.SPRUCE_WOOD,
                Blocks.DARK_OAK_WOOD,
                Blocks.ACACIA_WOOD,
                Blocks.JUNGLE_WOOD,
                Blocks.MANGROVE_WOOD,
                Blocks.CHERRY_WOOD,
                Blocks.PALE_OAK_WOOD
        ), new HammerDrop(ItemRegistry.WOOD_DUST, 1.0F, 2, 4, 0.5F));

        this.registerDrops(List.of(
                Blocks.OAK_PLANKS,
                Blocks.BIRCH_PLANKS,
                Blocks.SPRUCE_PLANKS,
                Blocks.DARK_OAK_PLANKS,
                Blocks.ACACIA_PLANKS,
                Blocks.JUNGLE_PLANKS,
                Blocks.MANGROVE_PLANKS,
                Blocks.CHERRY_PLANKS,
                Blocks.PALE_OAK_PLANKS
        ), new HammerDrop(ItemRegistry.WOOD_DUST, 1.0F, 1, 3, 0.5F));

        this.registerDrops(Blocks.ROOTED_DIRT,
                new HammerDrop(Items.WHEAT_SEEDS, 0.5F, 1, 3, 0.35F),
                new HammerDrop(Items.BEETROOT_SEEDS, 0.3F, 1, 2, 0.35F),
                new HammerDrop(Items.PUMPKIN_SEEDS, 0.15F, 1, 1, 0.0F),
                new HammerDrop(Items.MELON_SEEDS, 0.15F, 1, 1, 0.0F),
                new HammerDrop(Items.POTATO, 0.05F, 1, 2, 0.1F),
                new HammerDrop(Items.CARROT, 0.05F, 1, 2, 0.1F)
        );


        this.registerDrops(Blocks.PODZOL,
                new HammerDrop(Items.RED_MUSHROOM, 0.2F, 1, 2, 0.35F),
                new HammerDrop(Items.BROWN_MUSHROOM, 0.2F, 1, 2, 0.35F)
        );

        this.registerDrops(List.of(Blocks.SAND, Blocks.RED_SAND),
                new HammerDrop(Items.SUGAR_CANE, 0.1F, 1, 2, 0.1F),
                new HammerDrop(Items.CACTUS, 0.1F, 1, 2, 0.1F)
        );

        this.registerDrops(Blocks.SOUL_SAND,
                new HammerDrop(Items.CRIMSON_FUNGUS, 0.25F, 1, 1, 0.0F),
                new HammerDrop(Items.WARPED_FUNGUS, 0.25F, 1, 1, 0.0F)
        );
    }
}

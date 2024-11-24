package de.crafty.skylife.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.crafty.skylife.registry.FluidRegistry;
import de.crafty.skylife.registry.ItemRegistry;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class FluidConversionConfig extends AbstractSkyLifeConfig {

    private LinkedHashMap<Item, List<FluidDrop>> conversions = new LinkedHashMap<>();

    protected FluidConversionConfig() {
        super("fluidConversion");
    }

    public LinkedHashMap<Item, List<FluidDrop>> getConversions(){
        return this.conversions;
    }

    public List<FluidDrop> getDropsForItem(Item input, Fluid fluid) {
        return this.conversions.getOrDefault(input, List.of()).stream().filter(fluidDrop -> fluidDrop.requiredFluid() == fluid).toList();
    }

    @Override
    protected void setDefaults() {

        this.registerDefaultRecipes();
        this.encodeConversions();
    }

    @Override
    public void load() {
        super.load();

        this.decodeConversions();
    }

    private void encodeConversions() {
        this.conversions.forEach((input, lavaDrops) -> {
            String inputId = BuiltInRegistries.ITEM.wrapAsHolder(input).getRegisteredName();
            JsonArray dropConfigs = new JsonArray();
            lavaDrops.forEach(lavaDrop -> {
                JsonObject singleConfig = new JsonObject();
                singleConfig.addProperty("requiredFluid", BuiltInRegistries.FLUID.wrapAsHolder(lavaDrop.requiredFluid()).getRegisteredName());
                singleConfig.addProperty("output", BuiltInRegistries.ITEM.wrapAsHolder(lavaDrop.output()).getRegisteredName());
                singleConfig.addProperty("chance", lavaDrop.chance());
                singleConfig.addProperty("min", lavaDrop.min());
                singleConfig.addProperty("max", lavaDrop.max());
                singleConfig.addProperty("bonusChance", lavaDrop.bonusChance());
                singleConfig.addProperty("dropSeperate", lavaDrop.dropSeperate());
                dropConfigs.add(singleConfig);
            });
            this.data().add(inputId, dropConfigs);
        });
    }

    private void decodeConversions() {
        LinkedHashMap<Item, List<FluidDrop>> conversions = new LinkedHashMap<>();
        this.data().keySet().forEach(inputId -> {
            Item input = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(inputId));
            List<FluidDrop> drops = new LinkedList<>();
            this.data().getAsJsonArray(inputId).forEach(element -> {
                JsonObject singleConfig = element.getAsJsonObject();
                Fluid requiredFluid = BuiltInRegistries.FLUID.get(ResourceLocation.tryParse(singleConfig.get("requiredFluid").getAsString()));
                Item output = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(singleConfig.get("output").getAsString()));
                float chance = singleConfig.get("chance").getAsFloat();
                int min = singleConfig.get("min").getAsInt();
                int max = singleConfig.get("max").getAsInt();
                float bonusChance = singleConfig.get("bonusChance").getAsFloat();
                boolean dropSeperate = singleConfig.get("dropSeperate").getAsBoolean();
                drops.add(new FluidDrop(requiredFluid, output, chance, min, max, bonusChance, dropSeperate));
            });
            conversions.put(input, drops);
        });
        this.conversions = conversions;
    }


    public record FluidDrop(Fluid requiredFluid, Item output, float chance, int min, int max, float bonusChance, boolean dropSeperate) {
    }


    private void registerDefaultRecipes() {

        this.registerDrop(Items.COBBLESTONE, new FluidDrop(Fluids.LAVA, ItemRegistry.NETHERRACK_PIECE, 1.0F, 1, 3, 0.5F, true));
        this.registerDrop(Items.GUNPOWDER, new FluidDrop(Fluids.LAVA, Items.BLAZE_POWDER, 1.0F, 1, 1, 0.0F, false));
        this.registerDrop(ItemRegistry.BLAZE_ENRICHED_SEEDS, new FluidDrop(Fluids.LAVA, Items.NETHER_WART, 1.0F, 1, 1, 0.0F, false));
        this.registerDrop(Items.INK_SAC, new FluidDrop(Fluids.LAVA, Items.GLOW_INK_SAC, 1.0F, 1, 1, 0.0F, false));

        this.registerDrop(List.of(
                Items.SAND, Items.RED_SAND
        ), new FluidDrop(Fluids.LAVA, Items.SOUL_SAND, 1.0F, 1, 1, 0.0F, false));

        this.registerDrop(List.of(
                Items.DEEPSLATE, Items.COBBLED_DEEPSLATE
        ), new FluidDrop(Fluids.LAVA, Items.BASALT, 1.0F, 4, 4, 0.0F, true));


        this.registerDrop(Items.IRON_INGOT, new FluidDrop(FluidRegistry.MOLTEN_OBSIDIAN, ItemRegistry.STURDY_IRON, 1.0F, 1, 1, 0.0F, false));
        this.registerDrop(Items.DIAMOND, new FluidDrop(FluidRegistry.MOLTEN_OBSIDIAN, ItemRegistry.STURDY_DIAMOND, 1.0F, 1, 1, 0.0F, false));
    }

    //Idea: Merge drops together when calling registerDrops(); multiple times

    private void registerDrop(Item input, FluidDrop drop) {
        this.conversions.put(input, List.of(drop));
    }

    private void registerDrops(Item input, FluidDrop... drops) {
        this.conversions.put(input, List.of(drops));
    }

    private void registerDrop(List<Item> possibleInputs, FluidDrop drop) {
        possibleInputs.forEach(item -> this.conversions.put(item, List.of(drop)));
    }

    private void registerDrops(List<Item> possibleInputs, FluidDrop... drops) {
        possibleInputs.forEach(item -> this.conversions.put(item, List.of(drops)));
    }

}

package de.crafty.skylife.config;

import com.google.gson.JsonObject;
import de.crafty.lifecompat.util.EnergyUnitConverter;
import de.crafty.lifecompat.util.FluidUnitConverter;
import de.crafty.skylife.registry.ItemRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OilProcessingConfig extends AbstractSkyLifeConfig {

    private final HashMap<Item, ProcessingRecipe> recipes = new HashMap<>();
    private Item noPI_result = ItemRegistry.PLASTIC;

    protected OilProcessingConfig() {
        super("oilProcessing");
    }


    public HashMap<Item, ProcessingRecipe> getProcessingRecipes() {
        return this.recipes;
    }

    public ProcessingRecipe getProcessingRecipeFor(ItemStack stack) {
        if (stack.isEmpty())
            return new ProcessingRecipe(null, this.getNoPI_result(), this.getNoPI_count(), this.getNoPI_requiredLiquid(), this.getNoPI_processingTime());

        return this.recipes.getOrDefault(stack.getItem(), null);
    }

    //noPI = no Processing Item (When slot is empty)
    public Item getNoPI_result() {
        return this.noPI_result;
    }

    public int getNoPI_count() {
        return this.data().get("noPI_count").getAsInt();
    }

    public int getNoPI_requiredLiquid() {
        return this.data().get("noPI_requiredLiquid").getAsInt();
    }

    public int getNoPI_processingTime() {
        return this.data().get("noPI_processingTime").getAsInt();
    }

    @Override
    protected void setDefaults() {

        //Mode: Processing
        this.data().addProperty("noPI_result", BuiltInRegistries.ITEM.getKey(this.noPI_result).toString());
        this.data().addProperty("noPI_count", 1);
        this.data().addProperty("noPI_requiredLiquid", FluidUnitConverter.buckets(0.5F));
        this.data().addProperty("noPI_processingTime", 20 * 4);
        this.registerDefaultRecipes();

        this.encodeRecipes();
    }

    @Override
    public void load() {
        super.load();

        this.noPI_result = BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(this.data().get("noPI_result").getAsString()));
        this.decodeRecipes();
    }


    private void encodeRecipes() {

        JsonObject encodedRecipes = new JsonObject();

        this.recipes.forEach((item, processingRecipe) -> {

            JsonObject recipeJson = new JsonObject();
            recipeJson.addProperty("output", BuiltInRegistries.ITEM.getKey(processingRecipe.output()).toString());
            recipeJson.addProperty("outputCount", processingRecipe.outputCount());
            recipeJson.addProperty("requiredLiquid", processingRecipe.liquidAmount());
            recipeJson.addProperty("processingTime", processingRecipe.processingTime());

            encodedRecipes.add(BuiltInRegistries.ITEM.getKey(item).toString(), recipeJson);
        });

        this.data().add("recipes", encodedRecipes);
    }

    private void decodeRecipes() {
        this.recipes.clear();

        this.data().getAsJsonObject("recipes").keySet().forEach(key -> {

            Item item = BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(key));
            JsonObject recipeJson = this.data().getAsJsonObject("recipes").getAsJsonObject(key);

            this.recipes.put(item, new ProcessingRecipe(item,
                    BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(recipeJson.get("output").getAsString())),
                    recipeJson.get("outputCount").getAsInt(),
                    recipeJson.get("requiredLiquid").getAsInt(),
                    recipeJson.get("processingTime").getAsInt()
            ));
        });

    }

    private void registerDefaultRecipes() {
        this.registerRecipe(ItemRegistry.UPGRADE_MODULE_TEMPLATE, ItemRegistry.UPGRADE_MODULE, 1, FluidUnitConverter.buckets(4.0F), 20 * 60);
    }

    private void registerRecipe(Item processingItem, Item output, int outputCount, int liquidAmount, int processingTime) {
        this.recipes.put(processingItem, new ProcessingRecipe(processingItem, output, outputCount, liquidAmount, processingTime));
    }

    public record ProcessingRecipe(Item processingItem, Item output, int outputCount, int liquidAmount,
                                   int processingTime) {

    }
}

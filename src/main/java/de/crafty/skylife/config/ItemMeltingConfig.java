package de.crafty.skylife.config;

import com.google.gson.JsonObject;
import de.crafty.lifecompat.util.FluidUnitConverter;
import de.crafty.skylife.registry.FluidRegistry;
import de.crafty.skylife.registry.ItemRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class ItemMeltingConfig extends AbstractSkyLifeConfig {

    private final HashMap<Item, MeltingResult> meltingResults = new LinkedHashMap<>();

    protected ItemMeltingConfig() {
        super("itemMelting");
    }

    public HashMap<Item, MeltingResult> getMeltingResults() {
        return meltingResults;
    }

    public MeltingResult getMeltingResult(Item item) {
        return meltingResults.get(item);
    }

    @Override
    protected void setDefaults() {

        this.registerDefaultRecipes();
        this.encodeMeltingRecipes();
    }


    @Override
    public void load() {
        super.load();

        this.decodeMeltingRecipes();
    }

    private void encodeMeltingRecipes() {

        this.meltingResults.forEach((meltable, result) -> {

            JsonObject resultJson = new JsonObject();
            resultJson.addProperty("fluid", BuiltInRegistries.FLUID.getKey(result.fluid()).toString());
            resultJson.addProperty("amount", result.amount());
            resultJson.addProperty("meltingTime", result.meltingTime());
            this.data().add(BuiltInRegistries.ITEM.getKey(meltable).toString(), resultJson);

        });

    }

    private void decodeMeltingRecipes() {
        this.meltingResults.clear();

        this.data().keySet().forEach(itemId -> {
            Item item = BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(itemId));

            JsonObject resultJson = this.data().getAsJsonObject(itemId);
            Fluid fluid = BuiltInRegistries.FLUID.getValue(ResourceLocation.parse(resultJson.get("fluid").getAsString()));
            int amount = resultJson.get("amount").getAsInt();
            int meltingTime = resultJson.get("meltingTime").getAsInt();
            this.meltingResults.put(item, new MeltingResult(fluid, amount, meltingTime));
        });

    }


    private void registerDefaultRecipes() {

        this.registerMeltingRecipe(ItemRegistry.HARDENED_OIL_FRAGMENT, FluidRegistry.OIL, FluidUnitConverter.buckets(0.01F), 150);
        this.registerMeltingRecipe(ItemRegistry.STONE_PIECE, Fluids.LAVA, FluidUnitConverter.buckets(0.1F), 150);

    }

    private void registerMeltingRecipe(Item meltable, Fluid moltenResult, int fluidAmount, int meltingTime) {
        this.meltingResults.put(meltable, new MeltingResult(moltenResult, fluidAmount, meltingTime));
    }


    public record MeltingResult(Fluid fluid, int amount, int meltingTime) {

    }
}

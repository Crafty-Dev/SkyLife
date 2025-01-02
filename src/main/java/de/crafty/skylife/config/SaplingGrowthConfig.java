package de.crafty.skylife.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.crafty.skylife.config.AbstractSkyLifeConfig;
import java.util.LinkedList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class SaplingGrowthConfig extends AbstractSkyLifeConfig {

    private LinkedList<Block> excludedSaplings = new LinkedList<>();

    protected SaplingGrowthConfig() {
        super("saplingGrowth");
    }

    public LinkedList<Block> getExcludedSaplings() {
        return this.excludedSaplings;
    }

    public boolean sneakingWorks(){
        return this.data().get("sneakingWorks").getAsBoolean();
    }

    public boolean movingWorks(){
        return this.data().get("movingWorks").getAsBoolean();
    }

    public float getSneakChance(){
        return this.data().get("sneakChance").getAsFloat();
    }

    public float getMoveChance(){
        return this.data().get("moveChance").getAsFloat();
    }

    public int getWorkingRadius(){
        return this.data().get("workingRadius").getAsInt();
    }

    @Override
    protected void setDefaults() {

        this.data().addProperty("sneakingWorks", true);
        this.data().addProperty("movingWorks", true);
        this.data().addProperty("sneakChance", 0.1F);
        this.data().addProperty("moveChance", 0.2F);
        this.data().addProperty("workingRadius", 2);

        JsonArray excluded = new JsonArray();
        this.excludedSaplings.forEach(block -> excluded.add(BuiltInRegistries.BLOCK.wrapAsHolder(block).getRegisteredName()));
        this.data().add("excludedSaplings", excluded);

    }

    @Override
    public void load() {
        super.load();

        LinkedList<Block> excludedSaplings = new LinkedList<>();

        JsonArray excluded = this.data().getAsJsonArray("excludedSaplings");
        excluded.forEach(id -> {
            excludedSaplings.add(BuiltInRegistries.BLOCK.getValue(ResourceLocation.tryParse(id.getAsString())));
        });
        this.excludedSaplings = excludedSaplings;
    }
}

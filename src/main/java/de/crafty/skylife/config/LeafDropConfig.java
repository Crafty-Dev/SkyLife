package de.crafty.skylife.config;

import com.google.gson.JsonObject;

public class LeafDropConfig extends AbstractSkyLifeConfig {


    protected LeafDropConfig() {
        super("leafDrops");
    }


    @Override
    protected void setDefaults() {
        this.data().addProperty("min", 0);
        this.data().addProperty("max", 2);
    }


    public int getMin(){
        return this.data().get("min").getAsInt();
    }

    public int getMax(){
        return this.data().get("max").getAsInt();
    }


}

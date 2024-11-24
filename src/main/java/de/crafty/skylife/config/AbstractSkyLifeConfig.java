package de.crafty.skylife.config;

import com.google.gson.*;
import de.crafty.skylife.SkyLife;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public abstract class AbstractSkyLifeConfig {


    private final File file;
    private JsonObject data;

    protected AbstractSkyLifeConfig(String name){
        this.file = new File("config/skylife", name + ".json");
        this.data = new JsonObject();
    }

    protected void setDefaults(){

    }

    public JsonObject data() {
        return this.data;
    }

    public void load(){
        this.setDefaults();
        if(!this.file.exists()){
            this.save();
            return;
        }
        try {
            String content = FileUtils.readFileToString(this.file, StandardCharsets.UTF_8);
            this.data = JsonParser.parseString(content).getAsJsonObject();
        } catch (Exception e) {
            SkyLife.LOGGER.error("Failed to load {}", this.file.getName());
        }

    }

    public void save(){
        try {
            if(!this.file.exists())
                SkyLife.LOGGER.info("{} not present; Generating...", this.file.getName());

            FileUtils.writeStringToFile(this.file, new GsonBuilder().setPrettyPrinting().create().toJson(this.data), StandardCharsets.UTF_8);
        } catch (IOException e) {
            SkyLife.LOGGER.error("Failed to save {}", this.file.getName());
        }
    }

}

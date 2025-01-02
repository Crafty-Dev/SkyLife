package de.crafty.skylife;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraft.server.dedicated.DedicatedServerSettings;

import java.nio.file.Path;
import java.nio.file.Paths;

public class SkyLifeServer implements DedicatedServerModInitializer {

    private static SkyLifeServer instance;

    private DedicatedServerSettings serverSettings;

    @Override
    public void onInitializeServer() {
        instance = this;

        Path path2 = Paths.get("server.properties");
        this.serverSettings = new DedicatedServerSettings(path2);
        SkyLife.ISLAND_COUNT = this.serverSettings.getProperties().get("islandCount", 10);
        this.serverSettings.forceSave();
    }


    public static SkyLifeServer getInstance() {
        return instance;
    }

    public int getIslandCountOnServers() {
        return this.serverSettings.getProperties().get("islandCount", 10);
    }

}

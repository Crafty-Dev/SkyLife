package de.crafty.skylife.events.listener;

import de.crafty.lifecompat.api.event.EventListener;
import de.crafty.lifecompat.events.world.WorldStartupEvent;
import de.crafty.skylife.world.SpawnGenerator;

public class WorldStartUpListener implements EventListener<WorldStartupEvent.Callback> {


    @Override
    public void onEventCallback(WorldStartupEvent.Callback callback) {
        SpawnGenerator.start(callback.level(), callback.levelData());

    }
}

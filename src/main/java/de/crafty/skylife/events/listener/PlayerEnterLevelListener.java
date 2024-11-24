package de.crafty.skylife.events.listener;

import de.crafty.lifecompat.api.event.EventListener;
import de.crafty.lifecompat.events.player.PlayerEnterLevelEvent;
import de.crafty.skylife.advancements.SkyLifeCriteriaTriggers;
import de.crafty.skylife.world.SpawnGenerator;
import de.crafty.skylife.world.chunkgen.SkyLifeChunkGenOverworld;
import net.minecraft.server.level.ServerPlayer;

public class PlayerEnterLevelListener implements EventListener<PlayerEnterLevelEvent.Callback> {

    @Override
    public void onEventCallback(PlayerEnterLevelEvent.Callback callback) {
        if(callback.level().getChunkSource().getGenerator() instanceof SkyLifeChunkGenOverworld){
            ServerPlayer player = callback.player();

            SpawnGenerator.assignNextSpawn(callback.level(), player);
            SkyLifeCriteriaTriggers.ENTERED_SKYLIFE.trigger(player);
        }

    }
}

package de.crafty.skylife.events.listener;


import de.crafty.lifecompat.api.event.EventListener;
import de.crafty.lifecompat.events.player.PlayerDeathEvent;
import de.crafty.skylife.logic.GraveStoneLogic;

public class PlayerDeathListener implements EventListener<PlayerDeathEvent.Callback> {
    @Override
    public void onEventCallback(PlayerDeathEvent.Callback callback) {
        GraveStoneLogic.onPlayerDeath(callback.player());

    }
}

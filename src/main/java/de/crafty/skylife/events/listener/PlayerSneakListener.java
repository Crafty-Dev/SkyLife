package de.crafty.skylife.events.listener;

import de.crafty.lifecompat.api.event.EventListener;
import de.crafty.lifecompat.events.player.PlayerToggleSneakEvent;
import de.crafty.skylife.logic.SaplingGrowthLogic;
import de.crafty.skylife.registry.FluidRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Items;

public class PlayerSneakListener implements EventListener<PlayerToggleSneakEvent.Callback> {

    @Override
    public void onEventCallback(PlayerToggleSneakEvent.Callback callback) {
        SaplingGrowthLogic.onSaplingGrowthBySneaking(callback.player(), callback.level());

    }
}

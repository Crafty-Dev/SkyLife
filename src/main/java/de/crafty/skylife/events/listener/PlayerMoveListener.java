package de.crafty.skylife.events.listener;

import de.crafty.lifecompat.api.energy.cable.AbstractEnergyCableBlockEntity;
import de.crafty.lifecompat.api.energy.consumer.AbstractEnergyConsumer;
import de.crafty.lifecompat.api.event.EventListener;
import de.crafty.lifecompat.events.player.PlayerMoveEvent;
import de.crafty.skylife.logic.SaplingGrowthLogic;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

//TODO Fix Event that calls when player does not move
public class PlayerMoveListener implements EventListener<PlayerMoveEvent.Callback> {

    @Override
    public void onEventCallback(PlayerMoveEvent.Callback callback) {

        Vec3 prevPos = callback.prevPos();
        Vec3 pos = callback.pos();

        if(callback.player() == null)
            return;



        //callback.player().displayClientMessage(Component.literal("Buffer: " + consumer.getStoredEnergy()), true);
        //callback.player().displayClientMessage(Component.literal("Buffer: " + (cable.isBufferUnlocked() ? "Unlocked" : "Locked") + " [" + cable.getStoredEnergy() + "/" + cable.getCapacity() + "] " + "(last Update: " + cable.getLastTick() + ")"), true);

        SaplingGrowthLogic.onSaplingGrowthByMoving(callback.player(), callback.level(), prevPos.x(), prevPos.y(), prevPos.z(), pos.x(), pos.y(), pos.z());

    }
}

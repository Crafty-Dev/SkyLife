package de.crafty.skylife.events.listener;

import de.crafty.lifecompat.api.event.EventListener;
import de.crafty.lifecompat.events.block.BlockBreakEvent;
import de.crafty.skylife.logic.HammerLogic;
import de.crafty.skylife.logic.LeafDropLogic;

public class BlockBreakListener implements EventListener<BlockBreakEvent.Callback> {


    @Override
    public void onEventCallback(BlockBreakEvent.Callback callback) {
        HammerLogic.onBlockHammering(callback.getPlayer(), callback.getLevel(), callback.getBlockPos(), callback.getBlockState());
        LeafDropLogic.onLeafBreak(callback.getPlayer(), callback.getLevel(), callback.getBlockPos(), callback.getBlockState());
    }
}

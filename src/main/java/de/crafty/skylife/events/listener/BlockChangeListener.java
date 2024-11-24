package de.crafty.skylife.events.listener;

import de.crafty.lifecompat.api.event.EventListener;
import de.crafty.lifecompat.events.block.BlockChangeEvent;
import de.crafty.skylife.logic.BlockMeltingLogic;

public class BlockChangeListener implements EventListener<BlockChangeEvent.Callback> {


    @Override
    public void onEventCallback(BlockChangeEvent.Callback callback) {
        BlockMeltingLogic.onMelting(callback.level(), callback.pos(), callback.oldState(), callback.newState());
    }
}

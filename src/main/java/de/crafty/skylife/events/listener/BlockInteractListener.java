package de.crafty.skylife.events.listener;

import de.crafty.lifecompat.api.event.EventListener;
import de.crafty.lifecompat.events.block.BlockInteractEvent;
import de.crafty.skylife.logic.BlockTransformationLogic;

public class BlockInteractListener implements EventListener<BlockInteractEvent.Callback> {
    @Override
    public void onEventCallback(BlockInteractEvent.Callback callback) {
        callback.setActionResult(BlockTransformationLogic.onBlockTransformation(callback.getPlayer(), callback.getLevel(), callback.getHand(), callback.getBlockHitResult()) );
    }
}

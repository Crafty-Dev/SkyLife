package de.crafty.skylife.item;

import de.crafty.skylife.registry.TagRegistry;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Tier;

public class HammerItem extends DiggerItem {

    public HammerItem(Tier tier, Properties settings) {
        super(tier, TagRegistry.MINEABLE_WITH_HAMMER, settings);
    }

}

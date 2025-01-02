package de.crafty.skylife.item;

import de.crafty.skylife.registry.TagRegistry;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ToolMaterial;

public class HammerItem extends DiggerItem {

    private final ToolMaterial material;

    public HammerItem(ToolMaterial toolMaterial, float f, float g, Properties properties) {
        super(toolMaterial, TagRegistry.MINEABLE_WITH_HAMMER, f, g, properties);

        this.material = toolMaterial;
    }

    public ToolMaterial getMaterial() {
        return this.material;
    }
}

package de.crafty.skylife.item;

import de.crafty.skylife.block.machines.integrated.BriquetteGeneratorBlock;
import net.minecraft.world.item.Item;

public class BriquettItem extends Item {

    private final BriquetteGeneratorBlock.BriquetteType type;
    private final long burnTime;

    public BriquettItem(BriquetteGeneratorBlock.BriquetteType type, long burnTime, Properties properties) {
        super(properties);

        this.type = type;
        this.burnTime = burnTime;
    }

    public BriquetteGeneratorBlock.BriquetteType getType() {
        return type;
    }

    public long getBurnTime() {
        return this.burnTime;
    }
}

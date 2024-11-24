package de.crafty.skylife.item;

import de.crafty.skylife.entity.ResourceSheepEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

public class ResourceWheatItem extends Item {

    private final EntityType<? extends ResourceSheepEntity> sheep;
    private final float spawnChance;
    private final float bonusSpawnChance;

    public ResourceWheatItem(EntityType< ? extends ResourceSheepEntity> ressourceSheep, float spawnChance, float bonusSpawnChance) {
        super(new Item.Properties());

        this.sheep = ressourceSheep;
        this.spawnChance = spawnChance;
        this.bonusSpawnChance = bonusSpawnChance;
    }

    public EntityType<? extends ResourceSheepEntity> getSheepType() {
        return this.sheep;
    }

    public float getSpawnChance() {
        return this.getSpawnChance(false);
    }

    public float getSpawnChance(boolean bonus){
        return bonus ? this.spawnChance + this.bonusSpawnChance : this.spawnChance;

    }

    public float getBonusSpawnChance() {
        return this.bonusSpawnChance;
    }
}

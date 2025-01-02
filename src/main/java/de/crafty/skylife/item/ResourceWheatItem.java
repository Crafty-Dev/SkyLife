package de.crafty.skylife.item;

import de.crafty.skylife.entity.ResourceSheepEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

public class ResourceWheatItem extends Item {

    private final EntityType<? extends ResourceSheepEntity> sheep;
    private final float spawnChance;
    private final float bonusSpawnChance;

    private final DimensionCriteria requiredDimension;

    public ResourceWheatItem(EntityType< ? extends ResourceSheepEntity> ressourceSheep, float spawnChance, float bonusSpawnChance, Item.Properties properties){
        this(ressourceSheep, spawnChance, bonusSpawnChance, DimensionCriteria.ANY, properties);
    }

    public ResourceWheatItem(EntityType< ? extends ResourceSheepEntity> ressourceSheep, float spawnChance, float bonusSpawnChance, DimensionCriteria requiredDimension, Item.Properties properties) {
        super(properties);

        this.sheep = ressourceSheep;
        this.spawnChance = spawnChance;
        this.bonusSpawnChance = bonusSpawnChance;
        this.requiredDimension = requiredDimension;
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

    public DimensionCriteria getRequiredDimension() {
        return this.requiredDimension;
    }

    public float getBonusSpawnChance() {
        return this.bonusSpawnChance;
    }


    public enum DimensionCriteria {
        ANY,
        NETHER,
        END
    }
}

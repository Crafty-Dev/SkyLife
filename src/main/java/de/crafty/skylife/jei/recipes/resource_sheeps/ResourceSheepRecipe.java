package de.crafty.skylife.jei.recipes.resource_sheeps;

import de.crafty.skylife.entity.ResourceSheepEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ResourceSheepRecipe implements IJeiResourceSheepRecipe {

    private final ResourceSheepEntity.Type sheepType;


    public ResourceSheepRecipe(ResourceSheepEntity.Type sheepType){
        this.sheepType = sheepType;
    }

    @Override
    public float getStrength() {
        return this.sheepType.getStrength();
    }

    @Override
    public float getChance() {
        return this.sheepType.getBait().getSpawnChance();
    }

    @Override
    public List<ItemStack> getDrops() {
        return List.of(new ItemStack(this.sheepType.getResource()));
    }

    @Override
    public List<ItemStack> getWheat() {
        return List.of(new ItemStack(this.sheepType.getBait()));
    }

    @Override
    public String getSheepName() {
        return this.sheepType.name();
    }
}

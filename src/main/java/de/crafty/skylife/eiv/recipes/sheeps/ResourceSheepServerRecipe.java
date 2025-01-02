package de.crafty.skylife.eiv.recipes.sheeps;

import de.crafty.eiv.api.recipe.IEivServerModRecipe;
import de.crafty.eiv.api.recipe.ModRecipeType;
import de.crafty.skylife.SkyLife;
import de.crafty.skylife.entity.ResourceSheepEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class ResourceSheepServerRecipe implements IEivServerModRecipe {

    public static final ModRecipeType<ResourceSheepServerRecipe> TYPE = ModRecipeType.register(
            ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "resource_sheeps"),
            () -> new ResourceSheepServerRecipe(null)
    );

    private ResourceSheepEntity.Type sheepType;

    public ResourceSheepServerRecipe(ResourceSheepEntity.Type sheepType) {
        this.sheepType = sheepType;
    }

    public ResourceSheepEntity.Type getSheepType() {
        return this.sheepType;
    }

    @Override
    public void writeToTag(CompoundTag compoundTag) {
        compoundTag.putString("sheepType", sheepType.name().toLowerCase());
    }

    @Override
    public void loadFromTag(CompoundTag compoundTag) {
        this.sheepType = ResourceSheepEntity.Type.valueOf(compoundTag.getString("sheepType").toUpperCase());
    }

    @Override
    public ModRecipeType<? extends IEivServerModRecipe> getRecipeType() {
        return TYPE;
    }
}

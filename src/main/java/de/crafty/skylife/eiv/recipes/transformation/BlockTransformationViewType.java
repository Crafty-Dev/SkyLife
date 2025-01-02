package de.crafty.skylife.eiv.recipes.transformation;

import de.crafty.eiv.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.recipe.inventory.RecipeViewMenu;
import de.crafty.skylife.SkyLife;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BlockTransformationViewType implements IEivRecipeViewType {

    public static final BlockTransformationViewType INSTANCE = new BlockTransformationViewType();

    private static final ResourceLocation TRANSFORMATION_LOCATION = ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "textures/gui/view/block_transformation.png");

    @Override
    public Component getDisplayName() {
        return Component.translatable("view.skylife.type.block_transformation");
    }

    @Override
    public int getDisplayWidth() {
        return 118;
    }

    @Override
    public int getDisplayHeight() {
        return 36;
    }

    @Override
    public ResourceLocation getGuiTexture() {
        return TRANSFORMATION_LOCATION;
    }

    @Override
    public int getSlotCount() {
        return 3;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {
        //Block
        slotDefinition.addItemSlot(0, 1, 19);

        //Converter
        slotDefinition.addItemSlot(1, 51, 1);

        //Result
        slotDefinition.addItemSlot(2, 101, 19);
    }

    @Override
    public ResourceLocation getId() {
        return ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "block_transformation");
    }
}

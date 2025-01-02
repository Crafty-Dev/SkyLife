package de.crafty.skylife.eiv.recipes.fluid_conversion;

import de.crafty.eiv.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.recipe.inventory.RecipeViewMenu;
import de.crafty.skylife.SkyLife;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class FluidConversionViewType implements IEivRecipeViewType {

    public static final FluidConversionViewType INSTANCE = new FluidConversionViewType();

    private static final ResourceLocation CONVERSION_LOCATION = ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "textures/gui/view/fluid_conversion.png");

    @Override
    public Component getDisplayName() {
        return Component.translatable("view.skylife.type.fluid_conversion");
    }

    @Override
    public int getDisplayWidth() {
        return 133;
    }

    @Override
    public int getDisplayHeight() {
        return 36;
    }

    @Override
    public ResourceLocation getGuiTexture() {
        return CONVERSION_LOCATION;
    }

    @Override
    public int getSlotCount() {
        return 4;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {

        //Fluid
        slotDefinition.addItemSlot(0, 1, 19);
        //Cauldron
        slotDefinition.addItemSlot(1, 50, 19);
        //Ingredient
        slotDefinition.addItemSlot(2, 79, 1);

        //Output
        slotDefinition.addItemSlot(3, 116, 19);
    }

    @Override
    public ResourceLocation getId() {
        return ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "fluid_conversion");
    }
}

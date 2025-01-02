package de.crafty.skylife.eiv.recipes.hammering;

import de.crafty.eiv.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.recipe.inventory.RecipeViewMenu;
import de.crafty.eiv.recipe.inventory.RecipeViewScreen;
import de.crafty.skylife.SkyLife;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class HammeringViewType implements IEivRecipeViewType {

    public static final HammeringViewType INSTANCE = new HammeringViewType();

    private static final ResourceLocation HAMMERING_LOCATION = ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "textures/gui/view/hammering.png");

    @Override
    public Component getDisplayName() {
        return Component.translatable("view.skylife.type.hammering");
    }

    @Override
    public int getDisplayWidth() {
        return 162;
    }

    @Override
    public int getDisplayHeight() {
        return 54;
    }

    @Override
    public ResourceLocation getGuiTexture() {
        return HAMMERING_LOCATION;
    }

    @Override
    public int getSlotCount() {
        return 11;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {

        //Hammer slot
        slotDefinition.addItemSlot(0, 55, 1);

        //Block slot
        slotDefinition.addItemSlot(1, 91, 1);

        //Output slots
        for(int i = 0; i < 9; i++){
           slotDefinition.addItemSlot(2 + i, 1 + i * 18, 37);
        }
    }


    @Override
    public ResourceLocation getId() {
        return ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "hammering");
    }
}

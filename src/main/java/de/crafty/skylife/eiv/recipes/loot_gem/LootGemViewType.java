package de.crafty.skylife.eiv.recipes.loot_gem;

import de.crafty.eiv.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.recipe.inventory.RecipeViewMenu;
import de.crafty.skylife.SkyLife;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class LootGemViewType implements IEivRecipeViewType {

    public static final LootGemViewType INSTANCE = new LootGemViewType();

    private static final ResourceLocation GEM_LOCATION = ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "textures/gui/view/loot_gem.png");

    @Override
    public Component getDisplayName() {
        return Component.translatable("view.skylife.type.loot_gem");
    }

    @Override
    public int getDisplayWidth() {
        return 162;
    }

    @Override
    public int getDisplayHeight() {
        return 90;
    }

    @Override
    public ResourceLocation getGuiTexture() {
        return GEM_LOCATION;
    }

    @Override
    public int getSlotCount() {
        return 28;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {

        //Gem
        slotDefinition.addItemSlot(0, 73, 1);

        //Result Slots
        int i = 1;

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                slotDefinition.addItemSlot(i, 1 + x * 18, 37 + y * 18);
                i++;
            }
        }
    }

    @Override
    public ResourceLocation getId() {
        return ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "loot_gem");
    }
}

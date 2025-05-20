package de.crafty.skylife.eiv.recipes.oil_processing;

import de.crafty.eiv.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.recipe.inventory.RecipeViewMenu;
import de.crafty.skylife.SkyLife;
import de.crafty.skylife.registry.BlockRegistry;
import de.crafty.skylife.registry.ItemRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class OilProcessingViewType implements IEivRecipeViewType {

    public static final OilProcessingViewType INSTANCE = new OilProcessingViewType();

    private static final ResourceLocation PROCESSING_LOCATION = ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "textures/gui/view/oil_processing.png");

    @Override
    public Component getDisplayName() {
        return Component.translatable("view.skylife.type.oil_processing");
    }

    @Override
    public int getDisplayWidth() {
        return 62;
    }

    @Override
    public int getDisplayHeight() {
        return 60;
    }

    @Override
    public ResourceLocation getGuiTexture() {
        return PROCESSING_LOCATION;
    }

    @Override
    public int getSlotCount() {
        return 3;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {

        //Oil
        slotDefinition.addItemSlot(0, 1, 1);

        //Processing Item
        slotDefinition.addItemSlot(1, 1, 43);

        //Output
        slotDefinition.addItemSlot(2, 45, 25);

    }

    @Override
    public ResourceLocation getId() {
        return ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "oil_processing");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(ItemRegistry.OIL_BUCKET);
    }

    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(new ItemStack(BlockRegistry.OIL_PROCESSOR));
    }
}

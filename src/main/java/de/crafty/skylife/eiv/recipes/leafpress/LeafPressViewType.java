package de.crafty.skylife.eiv.recipes.leafpress;

import de.crafty.eiv.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.recipe.inventory.RecipeViewMenu;
import de.crafty.skylife.SkyLife;
import de.crafty.skylife.registry.BlockRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class LeafPressViewType implements IEivRecipeViewType {

    public static final LeafPressViewType INSTANCE = new LeafPressViewType();

    private static final ResourceLocation PRESS_LOCATION = ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "textures/gui/view/leafpress.png");

    @Override
    public Component getDisplayName() {
        return Component.translatable("view.skylife.type.leafpress");
    }

    @Override
    public int getDisplayWidth() {
        return 116;
    }

    @Override
    public int getDisplayHeight() {
        return 54;
    }

    @Override
    public ResourceLocation getGuiTexture() {
        return PRESS_LOCATION;
    }

    @Override
    public int getSlotCount() {
        return 4;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {

        //Leaf
        slotDefinition.addItemSlot(0, 9, 1);

        //Leafpress
        slotDefinition.addItemSlot(1, 9, 37);

        //Dried Leaf
        slotDefinition.addItemSlot(2, 77, 19);

        //Water
        slotDefinition.addItemSlot(3, 99, 23);

    }

    @Override
    public ResourceLocation getId() {
        return ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "leafpress");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(BlockRegistry.LEAF_PRESS);
    }

    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(new ItemStack(BlockRegistry.LEAF_PRESS));
    }
}

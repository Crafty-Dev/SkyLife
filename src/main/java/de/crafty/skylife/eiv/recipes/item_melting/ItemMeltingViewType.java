package de.crafty.skylife.eiv.recipes.item_melting;

import de.crafty.eiv.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.recipe.inventory.RecipeViewMenu;
import de.crafty.skylife.SkyLife;
import de.crafty.skylife.registry.BlockRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class ItemMeltingViewType implements IEivRecipeViewType {

    public static final ItemMeltingViewType INSTANCE = new ItemMeltingViewType();

    private static final ResourceLocation MELTING_LOCATION = ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "textures/gui/view/item_melting.png");

    @Override
    public Component getDisplayName() {
        return Component.translatable("view.skylife.type.item_melting");
    }

    @Override
    public int getDisplayWidth() {
        return 80;
    }

    @Override
    public int getDisplayHeight() {
        return 50;
    }

    @Override
    public ResourceLocation getGuiTexture() {
        return MELTING_LOCATION;
    }

    @Override
    public int getSlotCount() {
        return 2;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {

        //Meltable
        slotDefinition.addItemSlot(0, 1, 1);

        //Fluid
        slotDefinition.addItemSlot(1, 63, 15);
    }

    @Override
    public ResourceLocation getId() {
        return ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "item_melting");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(BlockRegistry.BLOCK_MELTER);
    }

    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(new ItemStack(BlockRegistry.BLOCK_MELTER));
    }
}

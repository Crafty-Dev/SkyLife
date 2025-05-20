package de.crafty.skylife.eiv.recipes.sheeps;

import de.crafty.eiv.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.recipe.inventory.RecipeViewMenu;
import de.crafty.skylife.SkyLife;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class ResourceSheepViewType implements IEivRecipeViewType {

    public static final ResourceSheepViewType INSTANCE = new ResourceSheepViewType();

    private static final ResourceLocation SHEEP_LOCATION = ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "textures/gui/view/sheeps.png");

    @Override
    public Component getDisplayName() {
        return Component.translatable("view.skylife.type.resource_sheeps");
    }

    @Override
    public int getDisplayWidth() {
        return 132;
    }

    @Override
    public int getDisplayHeight() {
        return 89;
    }

    @Override
    public ResourceLocation getGuiTexture() {
        return SHEEP_LOCATION;
    }

    @Override
    public int getSlotCount() {
        return 2;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {

        //Wheat
        slotDefinition.addItemSlot(0, 115, 17);

        //Drop
        slotDefinition.addItemSlot(1, 115, 37);

    }

    @Override
    public ResourceLocation getId() {
        return ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "resource_sheeps");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Items.WHITE_WOOL);
    }

    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(new ItemStack(Items.SHEEP_SPAWN_EGG));
    }
}

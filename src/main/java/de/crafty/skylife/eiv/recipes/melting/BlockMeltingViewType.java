package de.crafty.skylife.eiv.recipes.melting;

import de.crafty.eiv.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.recipe.inventory.RecipeViewMenu;
import de.crafty.skylife.SkyLife;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BlockMeltingViewType implements IEivRecipeViewType {

    public static final BlockMeltingViewType INSTANCE = new BlockMeltingViewType();

    private static final ResourceLocation MELTING_LOCATION = ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "textures/gui/view/melting.png");

    @Override
    public Component getDisplayName() {
        return Component.translatable("view.skylife.type.melting");
    }

    @Override
    public int getDisplayWidth() {
        return 98;
    }

    @Override
    public int getDisplayHeight() {
        return 54;
    }

    @Override
    public ResourceLocation getGuiTexture() {
        return MELTING_LOCATION;
    }

    @Override
    public int getSlotCount() {
        return 3;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {

        //Input
        slotDefinition.addItemSlot(0, 9, 1);

        //Heat source
        slotDefinition.addItemSlot(1, 9, 37);

        //Molten block
        slotDefinition.addItemSlot(2, 77, 19);
    }

    @Override
    public ResourceLocation getId() {
        return ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "melting");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Items.LAVA_BUCKET);
    }
}

package de.crafty.skylife.eiv.recipes.oil_processing;

import de.crafty.eiv.api.recipe.IEivRecipe;
import de.crafty.eiv.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.api.recipe.IEivViewRecipe;
import de.crafty.eiv.extra.FluidStack;
import de.crafty.eiv.recipe.inventory.RecipeViewMenu;
import de.crafty.eiv.recipe.inventory.RecipeViewScreen;
import de.crafty.eiv.recipe.inventory.SlotContent;
import de.crafty.eiv.recipe.rendering.AnimationTicker;
import de.crafty.skylife.SkyLife;
import de.crafty.skylife.SkyLifeClient;
import de.crafty.skylife.eiv.EivIntegration;
import de.crafty.skylife.registry.FluidRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class OilProcessingViewRecipe implements IEivViewRecipe {

    private final SlotContent oil, processingItem, result;

    private final AnimationTicker processingTicker;


    public OilProcessingViewRecipe(int liquidAmount, ItemStack processingItem, ItemStack result, int processingTime) {


        this.oil = SlotContent.of(new FluidStack(FluidRegistry.OIL, liquidAmount));
        this.processingItem = SlotContent.of(processingItem);

        this.result = SlotContent.of(result);

        int viewTime = processingTime / 4;
        this.processingTicker = AnimationTicker.create(ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "oil_processing_" + viewTime), viewTime);

    }

    @Override
    public IEivRecipeViewType getViewType() {
        return OilProcessingViewType.INSTANCE;
    }

    @Override
    public void fillSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        slotFillContext.fillSlot(0, this.oil.next());
        slotFillContext.fillSlot(1, this.processingItem.next());
        slotFillContext.fillSlot(2, this.result.next());
    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(this.oil, this.processingItem);
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(this.result);
    }

    @Override
    public List<AnimationTicker> getAnimationTickers() {
        return List.of(this.processingTicker);
    }

    @Override
    public void renderRecipe(RecipeViewScreen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {

        int progress = Math.round(this.processingTicker.getProgress() * 34);
        if(this.processingTicker.getDuration() <= 20)
            progress = Math.max(3, progress);

        guiGraphics.blit(RenderType::guiTextured, EivIntegration.SKYLIFE_VIEW_ICONS, 8, 20, 30, 0, progress, 21, 128, 128);

    }
}

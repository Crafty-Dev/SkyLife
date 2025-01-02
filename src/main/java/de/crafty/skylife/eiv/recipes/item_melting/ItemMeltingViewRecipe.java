package de.crafty.skylife.eiv.recipes.item_melting;

import de.crafty.eiv.BuiltInEivIntegration;
import de.crafty.eiv.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.api.recipe.IEivViewRecipe;
import de.crafty.eiv.extra.FluidStack;
import de.crafty.eiv.recipe.inventory.RecipeViewMenu;
import de.crafty.eiv.recipe.inventory.RecipeViewScreen;
import de.crafty.eiv.recipe.inventory.SlotContent;
import de.crafty.eiv.recipe.rendering.AnimationTicker;
import de.crafty.skylife.SkyLife;
import de.crafty.skylife.config.ItemMeltingConfig;
import de.crafty.skylife.eiv.EivIntegration;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.List;

public class ItemMeltingViewRecipe implements IEivViewRecipe {

    private final SlotContent meltable, result;
    private final ItemMeltingConfig.MeltingResult meltingResult;

    private final AnimationTicker meltingAnimationTicker;

    public ItemMeltingViewRecipe(Item meltable, ItemMeltingConfig.MeltingResult result) {
        this.meltable = SlotContent.of(meltable);
        this.result = SlotContent.of(new FluidStack(result.fluid(), result.amount()));

        this.meltingResult = result;

        this.meltingAnimationTicker = AnimationTicker.create(ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "item_melting_ticker"), 75);
    }

    @Override
    public IEivRecipeViewType getViewType() {
        return ItemMeltingViewType.INSTANCE;
    }

    @Override
    public void fillSlots(RecipeViewMenu.SlotFillContext slotFillContext) {

        slotFillContext.fillSlot(0, this.meltable.next());
        slotFillContext.fillSlot(1, this.result.next());

    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(this.meltable);
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(this.result);
    }

    @Override
    public List<AnimationTicker> getAnimationTickers() {
        return List.of(this.meltingAnimationTicker);
    }

    @Override
    public void renderRecipe(RecipeViewScreen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        int litProgress = Math.round(this.meltingAnimationTicker.getProgress() * 11.0F);

        guiGraphics.blit(RenderType::guiTextured, EivIntegration.SKYLIFE_VIEW_ICONS, 1, 19 + (11 - litProgress), 0.0F, 26 + (float)(11 - litProgress), 14, litProgress, 128, 128);

    }
}

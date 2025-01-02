package de.crafty.skylife.eiv.recipes.melting;

import de.crafty.eiv.BuiltInEivIntegration;
import de.crafty.eiv.api.recipe.IEivRecipe;
import de.crafty.eiv.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.api.recipe.IEivViewRecipe;
import de.crafty.eiv.extra.FluidStack;
import de.crafty.eiv.recipe.inventory.RecipeViewMenu;
import de.crafty.eiv.recipe.inventory.RecipeViewScreen;
import de.crafty.eiv.recipe.inventory.SlotContent;
import de.crafty.eiv.recipe.rendering.AnimationTicker;
import de.crafty.skylife.SkyLife;
import de.crafty.skylife.blockentities.MeltingBlockEntity;
import de.crafty.skylife.config.BlockMeltingConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.List;

public class BlockMeltingViewRecipe implements IEivViewRecipe {


    private final SlotContent meltable, moltenLiquid, heatSource;
    private final AnimationTicker meltingTicker;

    public BlockMeltingViewRecipe(Block meltable, Fluid moltenLiquid, BlockMeltingConfig.HeatSource heatSource) {
        this.meltable = SlotContent.of(meltable.asItem());
        this.heatSource = SlotContent.of(heatSource.representable());

        this.moltenLiquid = SlotContent.of(new FluidStack(moltenLiquid));

        int duration = (int) (MeltingBlockEntity.BASE_MELTING_TIME / (4.0F * heatSource.heatEfficiency()));
        this.meltingTicker = AnimationTicker.create(ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "melting_ticker_" + duration), duration);
    }

    @Override
    public IEivRecipeViewType getViewType() {
        return BlockMeltingViewType.INSTANCE;
    }

    @Override
    public void fillSlots(RecipeViewMenu.SlotFillContext slotFillContext) {

        slotFillContext.fillSlot(0, this.meltable.next());
        slotFillContext.fillSlot(1, this.heatSource.next());
        slotFillContext.fillSlot(2, this.moltenLiquid.next());
    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(this.meltable, this.heatSource);
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(this.moltenLiquid);
    }

    @Override
    public List<AnimationTicker> getAnimationTickers() {
        return List.of(this.meltingTicker);
    }

    @Override
    public void renderRecipe(RecipeViewScreen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        int litProgress = Math.round(this.meltingTicker.getProgress() * 14.0F);

        guiGraphics.blit(RenderType::guiTextured, BuiltInEivIntegration.WIDGETS, 10, 20 + (14 - litProgress), 0.0F, (float)(14 - litProgress), 14, litProgress, 128, 128);
    }
}

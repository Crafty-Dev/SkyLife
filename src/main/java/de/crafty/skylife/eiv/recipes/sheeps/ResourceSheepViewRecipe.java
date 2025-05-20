package de.crafty.skylife.eiv.recipes.sheeps;

import com.mojang.blaze3d.vertex.PoseStack;
import de.crafty.eiv.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.api.recipe.IEivViewRecipe;
import de.crafty.eiv.recipe.inventory.RecipeViewMenu;
import de.crafty.eiv.recipe.inventory.RecipeViewScreen;
import de.crafty.eiv.recipe.inventory.SlotContent;
import de.crafty.eiv.recipe.rendering.AnimationTicker;
import de.crafty.skylife.SkyLife;
import de.crafty.skylife.eiv.EivIntegration;
import de.crafty.skylife.entity.ResourceSheepEntity;
import de.crafty.skylife.item.ResourceWheatItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.text.DecimalFormat;
import java.util.List;

public class ResourceSheepViewRecipe implements IEivViewRecipe {

    private final ResourceSheepEntity.Type sheepType;
    private final SlotContent wheat, drop;

    private final AnimationTicker heartTicker;

    public ResourceSheepViewRecipe(ResourceSheepEntity.Type sheepType) {
        this.sheepType = sheepType;

        this.wheat = SlotContent.of(sheepType.getBait());
        this.drop = SlotContent.of(sheepType.getResource());

        this.heartTicker = AnimationTicker.create(ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "sheep_heart_ticker"), 75);
    }

    @Override
    public IEivRecipeViewType getViewType() {
        return ResourceSheepViewType.INSTANCE;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {

        slotFillContext.bindSlot(0, this.wheat);
        slotFillContext.bindSlot(1, this.drop);

    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(this.wheat);
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(this.drop);
    }

    @Override
    public List<AnimationTicker> getAnimationTickers() {
        return List.of(this.heartTicker);
    }

    @Override
    public void renderRecipe(RecipeViewScreen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {

        int heartProgress = Math.round(this.heartTicker.getProgress() * 12);

        guiGraphics.blit(RenderType::guiTextured, EivIntegration.SKYLIFE_VIEW_ICONS, 26, 19 + (12 - heartProgress), 0, 12 - heartProgress, 14, heartProgress, 128, 128);
        guiGraphics.blit(RenderType::guiTextured, EivIntegration.SKYLIFE_VIEW_ICONS, 26, 19 + (12 - heartProgress) + 26, 0, 12 - heartProgress, 14, heartProgress, 128, 128);
        guiGraphics.blit(RenderType::guiTextured, EivIntegration.SKYLIFE_VIEW_ICONS, 26, 19 + (12 - heartProgress) + 26 * 2, 0, 12 - heartProgress, 14, heartProgress, 128, 128);


        Component wheat = Component.translatable("gui.jei.category.resource_sheep.wheat").append(":");
        Component drop = Component.translatable("gui.jei.category.resource_sheep.drop").append(":");

        Font font = Minecraft.getInstance().font;

        this.drawString(guiGraphics, font, wheat, this.getViewType().getDisplayWidth() - 18 - 4 - font.width(wheat), 21, 1.0F, 0xFF808080);
        this.drawString(guiGraphics, font, drop, this.getViewType().getDisplayWidth() - 18 - 4 - font.width(drop), 41, 1.0F, 0xFF808080);


        float chance = this.sheepType.getBait().getSpawnChance();
        float bonusChance = this.sheepType.getBait().getSpawnChance(true);

        DecimalFormat formatter = new DecimalFormat("#.##");

        Component comp = Component.literal(formatter.format(chance * 100.0F) + "%");
        Component comp1 = Component.literal(formatter.format(bonusChance * 100.0F) + "%");
        Component comp2 = Component.literal(formatter.format(1.0F * 100.0F) + "%");


        this.drawCenteredString(guiGraphics, font, comp, 32, 34, 0.5F, 0xFF808080);
        this.drawCenteredString(guiGraphics, font, comp1, 32, 34 + 26, 0.5F, 0xFF808080);
        this.drawCenteredString(guiGraphics, font, comp2, 32, 34 + 26 * 2, 0.5F, 0xFF808080);


        //Dimension required
        Component label = Component.translatable("gui.jei.category.resource_sheep.dimension").withStyle(ChatFormatting.UNDERLINE);
        Component dim = Component.translatable("gui.jei.category.resource_sheep.dimension.any").withStyle(ChatFormatting.DARK_GRAY);

        if(this.wheat.getByIndex(this.wheat.index()).getItem() instanceof ResourceWheatItem resourceWheatItem){
            dim = Component.translatable("gui.jei.category.resource_sheep.dimension." + resourceWheatItem.getRequiredDimension().name().toLowerCase());

            switch (resourceWheatItem.getRequiredDimension()){
                case NETHER -> dim = dim.copy().withStyle(ChatFormatting.RED);
                case END -> dim = dim.copy().withStyle(ChatFormatting.BLUE);
                default -> dim = dim.copy().withStyle(ChatFormatting.DARK_GRAY);
            }

        }

        this.drawCenteredString(guiGraphics, font, label, 98.5F, 64, 0.75F, 0xFF808080);
        this.drawCenteredString(guiGraphics, font, dim, 98.5F, 64 + font.lineHeight, 0.75F, 0xFF808080);

    }

    private void drawCenteredString(GuiGraphics guiGraphics, Font font, Component text, float x, float y, float scale, int color){
        this.drawString(guiGraphics, font, text, x - font.width(text) / 2.0F * scale, y, scale, color);
    }

    private void drawString(GuiGraphics guiGraphics, Font font, Component text, float x, float y, float scale, int color){

        PoseStack stack = guiGraphics.pose();

        stack.pushPose();
        stack.translate(x, y, 0.0F);
        stack.pushPose();
        stack.scale(scale, scale, 1.0F);
        guiGraphics.drawString(font, text, 0, 0, color, false);
        stack.popPose();
        stack.popPose();
    }
}

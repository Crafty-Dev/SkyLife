package de.crafty.skylife.mixin.client.gui.screens;

import de.crafty.skylife.SkyLife;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class MixinTitleScreen extends Screen {

    protected MixinTitleScreen(Component component) {
        super(component);
    }


    @Inject(method = "render", at = @At("TAIL"))
    private void renderSkyLife(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci){

            guiGraphics.drawString(this.font, "SkyLife for MC " + SharedConstants.getCurrentVersion().getName(), 2, 3, -1);
    }
}

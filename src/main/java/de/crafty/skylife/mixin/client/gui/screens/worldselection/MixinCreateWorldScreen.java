package de.crafty.skylife.mixin.client.gui.screens.worldselection;


import de.crafty.skylife.SkyLifeClient;
import de.crafty.skylife.registry.WorldPresetKeys;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.SwitchGrid;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.function.Consumer;

@Mixin(CreateWorldScreen.WorldTab.class)
public abstract class MixinCreateWorldScreen extends GridLayoutTab {

    @Unique
    private GridLayout.RowHelper adder;

    @Unique
    private EditBox islandCountField;

    @Unique
    private StringWidget islandCountText;

    public MixinCreateWorldScreen(Component title) {
        super(title);
    }

    @Inject(method = "method_48676", at = @At("HEAD"))
    private void manageWidgets(WorldCreationUiState creator, CallbackInfo ci){
        if(creator.getWorldType().preset() != null && creator.getWorldType().preset().is(WorldPresetKeys.SKYLIFE)){
            this.islandCountText.visible = true;
            this.islandCountField.visible = true;
            return;
        }
        this.islandCountText.visible = false;
        this.islandCountField.visible = false;
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/layouts/GridLayout;createRowHelper(I)Lnet/minecraft/client/gui/layouts/GridLayout$RowHelper;"))
    private GridLayout.RowHelper getAdder(GridLayout instance, int columns) {
        adder = instance.createRowHelper(columns);
        return adder;
    }


    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/worldselection/SwitchGrid$Builder;build(Ljava/util/function/Consumer;)Lnet/minecraft/client/gui/screens/worldselection/SwitchGrid;"))
    private SwitchGrid addIslandWidget(SwitchGrid.Builder instance, Consumer<LayoutElement> widgetConsumer){
        SkyLifeClient skyClient = SkyLifeClient.getInstance();

        CreateWorldScreen screen = (CreateWorldScreen) Minecraft.getInstance().screen;
        if (screen == null)
            return null;

        islandCountText = new StringWidget(Component.translatable("selectLevel.islandCount"), Minecraft.getInstance().font);

        islandCountField = new EditBox(Minecraft.getInstance().font, 40, 20, Component.nullToEmpty(""));
        islandCountField.setValue(String.valueOf(skyClient.getCurrentIslandCount()));
        islandCountField.setResponder(s -> {

            if(s.isEmpty())
                return;

            ParsePosition parsePosition = new ParsePosition(0);
            NumberFormat.getInstance().parseObject(s, parsePosition);

            if(parsePosition.getIndex() != s.length() || s.length() > 2)
                islandCountField.setValue(String.valueOf(skyClient.getCurrentIslandCount()));
            else
                skyClient.setCurrentIslandCount(Integer.parseInt(s));

        });

        SwitchGrid grid = instance.build(widget -> adder.addChild(widget, 2));

        adder.addChild(islandCountText, 2);
        adder.addChild(islandCountField, 2);

        return grid;
    }
}

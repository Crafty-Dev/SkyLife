package de.crafty.skylife.mixin.server.commands;

import net.minecraft.core.BlockPos;
import net.minecraft.server.commands.PlaceCommand;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlaceCommand.class)
public abstract class MixinPlaceCommand {


    @Redirect(method = "method_43646", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isLoaded(Lnet/minecraft/core/BlockPos;)Z"))
    private static boolean applyChangedDimensionHeight(ServerLevel instance, BlockPos pos){

        int currentY = pos.getY();

        int minY = instance.getMinY();
        int maxY = instance.getMaxY();

        pos = currentY > maxY || currentY < minY ? new BlockPos(pos.getX(), minY, pos.getZ()) : pos;

        return instance.isLoaded(pos);
    }
}

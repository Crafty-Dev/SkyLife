package de.crafty.skylife.mixin.gametest.framework;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameTestInfo.class)
public abstract class MixinGameTestInfo {


    @Redirect(method = "method_54900", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isPositionEntityTicking(Lnet/minecraft/core/BlockPos;)Z"))
    private boolean applyChangedDimensionHeight(ServerLevel instance, BlockPos blockPos){

        int currentY = blockPos.getY();

        int minY = instance.getMinY();
        int maxY = instance.getMaxY();

        blockPos = currentY > maxY || currentY < minY ? new BlockPos(blockPos.getX(), minY, blockPos.getZ()) : blockPos;

        return instance.isPositionEntityTicking(blockPos);
    }

}

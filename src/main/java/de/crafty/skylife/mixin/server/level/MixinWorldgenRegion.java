package de.crafty.skylife.mixin.server.level;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldGenRegion.class)
public abstract class MixinWorldgenRegion {


    @Shadow @Final private ChunkAccess center;

    @Mutable
    @Shadow @Final private RandomSource random;

    @Shadow @Final private static ResourceLocation WORLDGEN_REGION_RANDOM;

    @Redirect(method = "<init>", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/levelgen/PositionalRandomFactory;at(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/util/RandomSource;"))
    private void applyChangedDimensionHeight(WorldGenRegion instance, RandomSource value){

        BlockPos blockPos = this.center.getPos().getWorldPosition();

        int currentY = blockPos.getY();

        int minY = instance.getMinY();
        int maxY = instance.getMaxY();

        blockPos = currentY > maxY || currentY < minY ? new BlockPos(blockPos.getX(), minY, blockPos.getZ()) : blockPos;

        this.random = instance.getLevel().getChunkSource().randomState().getOrCreateRandomFactory(WORLDGEN_REGION_RANDOM).at(blockPos);;
    }

}

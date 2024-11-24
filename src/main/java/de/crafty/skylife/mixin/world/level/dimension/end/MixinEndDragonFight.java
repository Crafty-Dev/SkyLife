package de.crafty.skylife.mixin.world.level.dimension.end;


import de.crafty.skylife.registry.BlockRegistry;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.dimension.end.EndDragonFight;

@Mixin(EndDragonFight.class)
public abstract class MixinEndDragonFight {



    @Shadow @Final private BlockPos origin;

    @Shadow private @Nullable BlockPos portalLocation;

    @Shadow @Final private ServerLevel level;

    @Inject(method = "setDragonKilled", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/dimension/end/EndDragonFight;dragonKilled:Z", shift = At.Shift.AFTER))
    private void spawnInfusedBedrock(EnderDragon dragon, CallbackInfo ci) {

        List<BlockPos> outlineBlocks = new ArrayList<>();


        BlockPos pos = this.portalLocation;
        if(pos == null)
            return;

        for (int xOff = -1; xOff <= 1; xOff++) {
            outlineBlocks.add(pos.offset(xOff, 0, 3));
            outlineBlocks.add(pos.offset(xOff, 0, -3));
        }

        for (int zOff = -1; zOff <= 1; zOff++) {
            outlineBlocks.add(pos.offset(3, 0, zOff));
            outlineBlocks.add(pos.offset(-3, 0, -zOff));
        }

        outlineBlocks.add(pos.offset(-2, 0, -2));
        outlineBlocks.add(pos.offset(2, 0, -2));
        outlineBlocks.add(pos.offset(-2, 0, 2));
        outlineBlocks.add(pos.offset(2, 0, 2));

        for (int i = 0; i < 3 + this.level.getRandom().nextInt(3); i++) {

            BlockPos p = outlineBlocks.get(this.level.getRandom().nextInt(outlineBlocks.size()));

            this.level.addDestroyBlockEffect(p, this.level.getBlockState(p));
            this.level.setBlock(p, BlockRegistry.DRAGON_INFUSED_BEDROCK.defaultBlockState(), 3);
            this.level.playLocalSound(p.getX(), p.getY(), p.getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 1.0F, 1.0F, false);
            outlineBlocks.remove(p);
        }

    }

}

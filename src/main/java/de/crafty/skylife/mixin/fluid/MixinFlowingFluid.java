package de.crafty.skylife.mixin.fluid;


import de.crafty.skylife.network.SkyLifeNetworkServer;
import de.crafty.skylife.network.payload.SkyLifeClientEventPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.EndPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowingFluid.class)
public abstract class MixinFlowingFluid extends Fluid {


    @Inject(method = "canHoldFluid", at = @At("HEAD"), cancellable = true)
    private void injectPortalWaterDestroyLogic(BlockGetter world, BlockPos pos, BlockState state, Fluid fluid, CallbackInfoReturnable<Boolean> cir) {
        if (state.getBlock() instanceof EndPortalBlock && world instanceof Level w && w.dimension() != Level.END && fluid.isSame(Fluids.LAVA))
            cir.setReturnValue(true);
    }

    @Inject(method = "spreadTo", at = @At("HEAD"))
    private void injectPortalWaterDestroyLogic(LevelAccessor level, BlockPos pos, BlockState state, Direction direction, FluidState fluidState, CallbackInfo ci){
        if(state.getBlock() instanceof EndPortalBlock && level instanceof ServerLevel){
            SkyLifeNetworkServer.sendUpdate(SkyLifeClientEventPayload.ClientEventType.END_PORTAL_DESTROYED, pos, level);
        }
    }

}

package de.crafty.skylife.mixin.world.level.levelgen;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.levelgen.PhantomSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PhantomSpawner.class)
public abstract class MixinPhantomSpawner implements CustomSpawner {



    //Prevents phantoms from spawning in the first 10 days of a player if the player haven't slept before
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isSpectator()Z"))
    private boolean avoidPhantomSpawn(ServerPlayer instance){
        return instance.isSpectator() || (instance.getStats().getValue(Stats.CUSTOM.get(Stats.PLAY_TIME)) <= 10 * 24000 && instance.getStats().getValue(Stats.CUSTOM.get(Stats.SLEEP_IN_BED)) == 0);
    }
}

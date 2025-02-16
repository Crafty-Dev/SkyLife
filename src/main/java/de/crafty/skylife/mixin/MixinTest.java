package de.crafty.skylife.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SkyRenderer.class)
public abstract class MixinTest {



}

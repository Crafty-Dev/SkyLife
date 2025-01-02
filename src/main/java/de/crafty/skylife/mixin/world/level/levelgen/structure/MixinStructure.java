package de.crafty.skylife.mixin.world.level.levelgen.structure;

import de.crafty.skylife.SkyLife;
import de.crafty.skylife.world.chunkgen.AbstractSkyLifeChunkGenerator;
import de.crafty.skylife.world.chunkgen.SkyLifeChunkGenOverworld;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureCheck;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Structure.class)
public abstract class MixinStructure {



    @Inject(method = "findValidGenerationPoint", at = @At("HEAD"), cancellable = true)
    private void preventStructureSpawnAtCenter(Structure.GenerationContext generationContext, CallbackInfoReturnable<Optional<Structure.GenerationStub>> cir){
        if(generationContext.chunkGenerator() instanceof SkyLifeChunkGenOverworld chunkGen && !chunkGen.isAllowedToSpawn(SkyLife.ISLAND_COUNT, generationContext.chunkPos()))
            cir.setReturnValue(Optional.empty());

    }
}

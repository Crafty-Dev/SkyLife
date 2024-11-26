package de.crafty.skylife.registry;

import com.mojang.serialization.MapCodec;
import de.crafty.skylife.SkyLife;
import de.crafty.skylife.structure.resource_island.ResourceIslandPiece;
import de.crafty.skylife.structure.resource_island.ResourceIslandStructure;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

import java.util.LinkedHashMap;
import java.util.function.Supplier;

public class StructureRegistry {

    private static final LinkedHashMap<ResourceLocation, StructureType<?>> STRUCTURES = new LinkedHashMap<>();


    public static final StructureType<ResourceIslandStructure> RESOURCE_ISLAND = register("resource_island", ResourceIslandStructure.CODEC);

    private static <S extends Structure> StructureType<S> register(String id, MapCodec<S> codec){
        STRUCTURES.put(ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, id), () -> (MapCodec<Structure>) codec);
        return () -> codec;
    }

    public static void perform(){
        STRUCTURES.forEach((resourceLocation, structureType) -> {
            Registry.register(BuiltInRegistries.STRUCTURE_TYPE, resourceLocation, structureType);
        });
    }


    public static class Pieces {

        private static final LinkedHashMap<ResourceLocation, StructurePieceType> PIECES = new LinkedHashMap<>();

        public static final StructurePieceType RESOURCE_ISLAND = register("resource_island", ResourceIslandPiece::new);

        private static StructurePieceType register(String id, StructurePieceType.StructureTemplateType piece) {
            PIECES.put(ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, id), piece);
            return piece;
        }

        public static void perform() {
            PIECES.forEach((resourceLocation, structurePieceType) -> {
                Registry.register(BuiltInRegistries.STRUCTURE_PIECE, resourceLocation, structurePieceType);
            });
        }

    }

}

package de.crafty.skylife.registry;

import de.crafty.skylife.SkyLife;
import java.util.LinkedHashMap;
import java.util.function.UnaryOperator;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.CustomData;

public class DataComponentTypeRegistry {

    private static final LinkedHashMap<ResourceLocation, DataComponentType<CustomData>> NBT_TAG_TYPES = new LinkedHashMap<>();

    public static final DataComponentType<CustomData> SAVED_ENTITY = registerNbt("stored_entity", builder -> builder.persistent(CustomData.CODEC));


    private static DataComponentType<CustomData> registerNbt(String id, UnaryOperator<DataComponentType.Builder<CustomData>> builderOperator) {
        DataComponentType<CustomData> componentType = builderOperator.apply(DataComponentType.builder()).build();
        NBT_TAG_TYPES.put(ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, id), componentType);
        return componentType;
    }

    public static void perform(){
        NBT_TAG_TYPES.forEach((identifier, nbtComponentComponentType) -> Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, identifier, nbtComponentComponentType));
    }
}

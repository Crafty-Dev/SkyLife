package de.crafty.skylife.registry;

import de.crafty.skylife.SkyLife;
import de.crafty.skylife.entity.ResourceSheepEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import java.util.LinkedHashMap;

public class EntityRegistry {

    private static final LinkedHashMap<ResourceLocation, EntityType<? extends LivingEntity>> LIVING_ENTITY_TYPES = new LinkedHashMap<>();
    private static final LinkedHashMap<ResourceLocation, AttributeSupplier> ATTRIBUTES = new LinkedHashMap<>();

    public static final EntityType<ResourceSheepEntity> COAL_SHEEP = registerResourceSheep("coal_sheep", ResourceSheepEntity.Type.COAL);
    public static final EntityType<ResourceSheepEntity> IRON_SHEEP = registerResourceSheep("iron_sheep", ResourceSheepEntity.Type.IRON);
    public static final EntityType<ResourceSheepEntity> COPPER_SHEEP = registerResourceSheep("copper_sheep", ResourceSheepEntity.Type.COPPER);
    public static final EntityType<ResourceSheepEntity> GOLD_SHEEP = registerResourceSheep("gold_sheep", ResourceSheepEntity.Type.GOLD);
    public static final EntityType<ResourceSheepEntity> LAPIS_SHEEP = registerResourceSheep("lapis_sheep", ResourceSheepEntity.Type.LAPIS);
    public static final EntityType<ResourceSheepEntity> REDSTONE_SHEEP = registerResourceSheep("redstone_sheep", ResourceSheepEntity.Type.REDSTONE);
    public static final EntityType<ResourceSheepEntity> DIAMOND_SHEEP = registerResourceSheep("diamond_sheep", ResourceSheepEntity.Type.DIAMOND);
    public static final EntityType<ResourceSheepEntity> EMERALD_SHEEP = registerResourceSheep("emerald_sheep", ResourceSheepEntity.Type.EMERALD);

    public static final EntityType<ResourceSheepEntity> QUARTZ_SHEEP = registerResourceSheep("quartz_sheep", ResourceSheepEntity.Type.QUARTZ);
    public static final EntityType<ResourceSheepEntity> NETHERITE_SHEEP = registerResourceSheep("netherite_sheep", ResourceSheepEntity.Type.NETHERITE);
    public static final EntityType<ResourceSheepEntity> GLOWSTONE_SHEEP = registerResourceSheep("glowstone_sheep", ResourceSheepEntity.Type.GLOWSTONE);

    public static final EntityType<ResourceSheepEntity> NETHERRACK_SHEEP = registerResourceSheep("netherrack_sheep", ResourceSheepEntity.Type.NETHERRACK);
    public static final EntityType<ResourceSheepEntity> COBBLESTONE_SHEEP = registerResourceSheep("cobblestone_sheep", ResourceSheepEntity.Type.COBBLESTONE);
    public static final EntityType<ResourceSheepEntity> DIRT_SHEEP = registerResourceSheep("dirt_sheep", ResourceSheepEntity.Type.DIRT);

    private static <T extends LivingEntity> EntityType<T> registerLivingEntity(String id, EntityType.Builder<T> builder, AttributeSupplier.Builder attributes) {
        EntityType<T> type = builder.build(id);
        LIVING_ENTITY_TYPES.put(ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, id), type);
        ATTRIBUTES.put(ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, id), attributes.build());
        return type;
    }

    private static EntityType<ResourceSheepEntity> registerResourceSheep(String id, ResourceSheepEntity.Type resourceType) {
        return registerLivingEntity(id, EntityType.Builder.<ResourceSheepEntity>of((entityType, world) -> new ResourceSheepEntity(entityType, world, resourceType), MobCategory.CREATURE).sized(0.9F, 1.3F).eyeHeight(1.235F).passengerAttachments(1.2375F).clientTrackingRange(10), ResourceSheepEntity.createSheepAttributes(resourceType));
    }

    public static LinkedHashMap<ResourceLocation, EntityType<? extends LivingEntity>> getLivingEntityTypes() {
        return LIVING_ENTITY_TYPES;
    }

    public static void perform() {
        LIVING_ENTITY_TYPES.forEach((identifier, entityType) -> Registry.register(BuiltInRegistries.ENTITY_TYPE, identifier, entityType));
        ATTRIBUTES.forEach((identifier, defaultAttributeContainer) -> FabricDefaultAttributeRegistry.register(LIVING_ENTITY_TYPES.get(identifier), defaultAttributeContainer));
    }

    public static class ModelLayers {

        //Entity
        public static final ModelLayerLocation RESOURCE_SHEEP = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "resourcesheep"), "main");
        public static final ModelLayerLocation RESOURCE_SHEEP_FUR = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "resourcesheep"), "fur");

        //BlockEntity
        public static final ModelLayerLocation ENERGY_STORAGE_CORE = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "energystorage"), "core");
        public static final ModelLayerLocation BLOCK_BREAKER_CHAIN = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "block_breaker"), "chain");
        public static final ModelLayerLocation OIL_PROCESSOR_BURNING_INDICATOR = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "oil_processor"), "burning_indicator");
        public static final ModelLayerLocation OIL_PROCESSOR_PROCESSING_INDICATOR = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "oil_processor"), "processing_indicator");


        public static final ModelLayerLocation FLUID_PIPE_DOWN_ARROW = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "fluid_pipe"), "down_arrow");
        public static final ModelLayerLocation FLUID_PIPE_UP_ARROW = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "fluid_pipe"), "up_arrow");
        public static final ModelLayerLocation FLUID_PIPE_INOUT = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "fluid_pipe"), "inout");


    }
}

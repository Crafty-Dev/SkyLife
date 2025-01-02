package de.crafty.skylife.loot;

import com.google.common.collect.Maps;
import de.crafty.skylife.registry.EntityRegistry;
import de.crafty.skylife.registry.ItemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class SkyLifeEntityLootProvider extends EntityLootSubProvider {

    protected final HolderLookup.Provider registries;
    private final FeatureFlagSet allowed;
    private final FeatureFlagSet required;
    private final Map<EntityType<?>, Map<ResourceKey<LootTable>, LootTable.Builder>> map = Maps.<EntityType<?>, Map<ResourceKey<LootTable>, LootTable.Builder>>newHashMap();

    public SkyLifeEntityLootProvider(HolderLookup.Provider provider) {
        this(FeatureFlags.REGISTRY.allFlags(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    public SkyLifeEntityLootProvider(FeatureFlagSet allowed, FeatureFlagSet required, HolderLookup.Provider provider) {
        super(allowed, required, provider);
        this.allowed = allowed;
        this.required = required;
        this.registries = provider;

    }

    @Override
    public void generate() {

        //Item Sheeps
        this.add(EntityRegistry.COAL_SHEEP, this.createResourceSheepTable(ItemRegistry.COAL_ORE_DUST));
        this.add(EntityRegistry.IRON_SHEEP, this.createResourceSheepTable(ItemRegistry.IRON_ORE_DUST));
        this.add(EntityRegistry.COPPER_SHEEP, this.createResourceSheepTable(ItemRegistry.COPPER_ORE_DUST));
        this.add(EntityRegistry.GOLD_SHEEP, this.createResourceSheepTable(ItemRegistry.GOLD_ORE_DUST));
        this.add(EntityRegistry.LAPIS_SHEEP, this.createResourceSheepTable(ItemRegistry.LAPIS_ORE_DUST));
        this.add(EntityRegistry.REDSTONE_SHEEP, this.createResourceSheepTable(ItemRegistry.REDSTONE_ORE_DUST));
        this.add(EntityRegistry.DIAMOND_SHEEP, this.createResourceSheepTable(ItemRegistry.DIAMOND_ORE_DUST));
        this.add(EntityRegistry.EMERALD_SHEEP, this.createResourceSheepTable(ItemRegistry.EMERALD_ORE_DUST));
        this.add(EntityRegistry.QUARTZ_SHEEP, this.createResourceSheepTable(ItemRegistry.QUARTZ_ORE_DUST));
        this.add(EntityRegistry.NETHERITE_SHEEP, this.createResourceSheepTable(ItemRegistry.NETHERITE_ORE_DUST));
        this.add(EntityRegistry.GLOWSTONE_SHEEP, this.createResourceSheepTable(ItemRegistry.GLOWSTONE_ORE_DUST));

        //Block Sheeps
        this.add(EntityRegistry.NETHERRACK_SHEEP, this.createResourceSheepTable(Items.NETHERRACK));
        this.add(EntityRegistry.COBBLESTONE_SHEEP, this.createResourceSheepTable(Items.COBBLESTONE));
        this.add(EntityRegistry.DIRT_SHEEP, this.createResourceSheepTable(Items.DIRT));
        this.add(EntityRegistry.OIL_SHEEP, this.createResourceSheepTable(ItemRegistry.HARDENED_OIL_FRAGMENT));

    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer) {
        this.generate();
        Set<ResourceKey<LootTable>> set = new HashSet<>();
        EntityRegistry.getLivingEntityTypes()
                .forEach(
                        (resourceLocation, entityType) -> {
                            if (entityType.isEnabled(this.allowed)) {
                                Optional<ResourceKey<LootTable>> optional = entityType.getDefaultLootTable();
                                if (optional.isPresent()) {
                                    Map<ResourceKey<LootTable>, LootTable.Builder> map = (Map<ResourceKey<LootTable>, LootTable.Builder>)this.map.remove(entityType);
                                    if (entityType.isEnabled(this.required) && (map == null || !map.containsKey(optional.get()))) {
                                        throw new IllegalStateException(String.format(Locale.ROOT, "Missing loottable '%s' for '%s'", optional.get(), resourceLocation));
                                    }

                                    if (map != null) {
                                        map.forEach((resourceKey, builder) -> {
                                            if (!set.add(resourceKey)) {
                                                throw new IllegalStateException(String.format(Locale.ROOT, "Duplicate loottable '%s' for '%s'", resourceKey, resourceLocation));
                                            } else {
                                                biConsumer.accept(resourceKey, builder);
                                            }
                                        });
                                    }
                                } else {
                                    Map<ResourceKey<LootTable>, LootTable.Builder> mapx = this.map.remove(entityType);
                                    if (mapx != null) {
                                        throw new IllegalStateException(
                                                String.format(
                                                        Locale.ROOT,
                                                        "Weird loottables '%s' for '%s', not a LivingEntity so should not have loot",
                                                        mapx.keySet().stream().map(resourceKey -> resourceKey.location().toString()).collect(Collectors.joining(",")),
                                                        resourceLocation
                                                )
                                        );
                                    }
                                }
                            }
                        }
                );
        if (!this.map.isEmpty()) {
            throw new IllegalStateException("Created loot tables for entities not supported by datapack: " + this.map.keySet());
        }
    }

    @Override
    protected void add(EntityType<?> entityType, ResourceKey<LootTable> resourceKey, LootTable.Builder builder) {
        this.map.computeIfAbsent(entityType, entityTypex -> new HashMap<>()).put(resourceKey, builder);
    }

    protected LootTable.Builder createResourceSheepTable(ItemLike resource) {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(resource)))
                .withPool(
                        LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1.0F))
                                .add(
                                        LootItem.lootTableItem(Items.MUTTON)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                                                .apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot()))
                                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                                )
                );
    }

}

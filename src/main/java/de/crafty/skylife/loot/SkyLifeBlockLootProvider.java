package de.crafty.skylife.loot;

import de.crafty.skylife.registry.BlockRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.BiConsumer;

public class SkyLifeBlockLootProvider extends BlockLootSubProvider {


    protected SkyLifeBlockLootProvider(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    public void generate() {
        HolderLookup.RegistryLookup<Enchantment> registryLookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);

        this.dropSelf(BlockRegistry.LEAF_PRESS);
        this.dropSelf(BlockRegistry.GHAST_BLOCK);
        this.dropSelf(BlockRegistry.PHANTOM_BLOCK);
        this.dropSelf(BlockRegistry.END_PORTAL_CORE);
        this.dropSelf(BlockRegistry.END_PORTAL_FRAME);
        this.dropSelf(BlockRegistry.MAGICAL_WORKBENCH);
        this.dropSelf(BlockRegistry.END_DIAMOND_ORE);
        this.dropSelf(BlockRegistry.END_NETHERITE_ORE);

        this.dropSelf(BlockRegistry.BRIQUETTE_GENERATOR);
        this.dropSelf(BlockRegistry.SOLAR_PANEL);
        this.dropSelf(BlockRegistry.BLOCK_BREAKER);
        this.dropSelf(BlockRegistry.FLUX_FURNACE);
        this.dropSelf(BlockRegistry.LC_VP_STORAGE);
        this.dropSelf(BlockRegistry.MC_VP_STORAGE);
        this.dropSelf(BlockRegistry.HC_VP_STORAGE);
        this.dropSelf(BlockRegistry.BASIC_ENERGY_CABLE);
        this.dropSelf(BlockRegistry.IMPROVED_ENERGY_CABLE);

        this.dropOther(BlockRegistry.MELTING_COBBLESTONE, Blocks.COBBLESTONE);
        this.dropOther(BlockRegistry.MELTING_STONE, Blocks.STONE);
        this.dropOther(BlockRegistry.MELTING_OBSIDIAN, Blocks.OBSIDIAN);

        this.dropOther(BlockRegistry.MOLTEN_OBSIDIAN_CAULDRON, Blocks.CAULDRON);
        this.dropOther(BlockRegistry.OIL_CAULDRON, Blocks.CAULDRON);



        this.add(BlockRegistry.DRIED_LEAVES, block -> this.createDriedLeavesTable(block, registryLookup));

    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer) {
        this.generate();

        Set<ResourceKey<LootTable>> set = new HashSet<>();

        for (Block block : BlockRegistry.getBlockList()) {
            if (!block.isEnabled(this.enabledFeatures))
                return;

            ResourceKey<LootTable> resourceKey = block.getLootTable();
            if (resourceKey != BuiltInLootTables.EMPTY && set.add(resourceKey)) {
                LootTable.Builder builder = this.map.remove(resourceKey);
                if (builder == null) {
                    throw new IllegalStateException(
                            String.format(Locale.ROOT, "Missing loottable '%s' for '%s'", resourceKey.location(), BuiltInRegistries.BLOCK.getKey(block))
                    );
                }

                biConsumer.accept(resourceKey, builder);
            }

        }

        if (!this.map.isEmpty()) {
            throw new IllegalStateException("Created block loot tables for non-blocks: " + this.map.keySet());
        }
    }

    private LootTable.Builder createDriedLeavesTable(Block block, HolderLookup.RegistryLookup<Enchantment> registryLookup) {
        return this.createSilkTouchOrShearsDispatchTable(block,
                this.applyExplosionCondition(block, LootItem.lootTableItem(block))
        ).withPool(
                LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(this.doesNotHaveShearsOrSilkTouch()
                        )
                        .add(this.applyExplosionDecay(block, LootItem.lootTableItem(Items.STICK)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                                ).when(BonusLevelTableCondition.bonusLevelFlatChance(registryLookup.getOrThrow(Enchantments.FORTUNE), NORMAL_LEAVES_SAPLING_CHANCES))
                        )
        );
    }
}

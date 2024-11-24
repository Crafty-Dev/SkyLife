package de.crafty.skylife.advancements.sub;

import de.crafty.skylife.SkyLife;
import de.crafty.skylife.registry.ItemRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.RecipeCraftedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.data.recipes.packs.VanillaRecipeProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class SkyLifeHammeringAdvancementProvider implements AdvancementSubProvider {

    @Override
    public void generate(HolderLookup.Provider provider, Consumer<AdvancementHolder> consumer) {
        AdvancementHolder root = Advancement.Builder.advancement()
                .display(
                        ItemRegistry.WOODEN_HAMMER,
                        Component.translatable("advancements.hammering.root.title"),
                        Component.translatable("advancements.hammering.root.description"),
                        ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "textures/gui/advancements/backgrounds/hammering.png"),
                        AdvancementType.TASK,
                        false,
                        true,
                        true
                )
                .addCriterion("crafted_wooden_hammer", RecipeCraftedTrigger.TriggerInstance.craftedItem(ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, VanillaRecipeProvider.getItemName(ItemRegistry.WOODEN_HAMMER))))
                .save(consumer, "hammering/root");

        AdvancementHolder stone_hammer = Advancement.Builder.advancement().parent(root)
                .display(
                        ItemRegistry.STONE_HAMMER,
                        Component.translatable("advancements.hammering.stone.title"),
                        Component.translatable("advancements.hammering.stone.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("crafted_stone_hammer", RecipeCraftedTrigger.TriggerInstance.craftedItem(ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, VanillaRecipeProvider.getItemName(ItemRegistry.STONE_HAMMER))))
                .save(consumer, "hammering/stone_hammer");

        AdvancementHolder iron_hammer = Advancement.Builder.advancement().parent(stone_hammer)
                .display(
                        ItemRegistry.IRON_HAMMER,
                        Component.translatable("advancements.hammering.iron.title"),
                        Component.translatable("advancements.hammering.iron.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("crafted_iron_hammer", RecipeCraftedTrigger.TriggerInstance.craftedItem(ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, VanillaRecipeProvider.getItemName(ItemRegistry.IRON_HAMMER))))
                .save(consumer, "hammering/iron_hammer");

        AdvancementHolder gold_hammer = Advancement.Builder.advancement().parent(iron_hammer)
                .display(
                        ItemRegistry.GOLDEN_HAMMER,
                        Component.translatable("advancements.hammering.gold.title"),
                        Component.translatable("advancements.hammering.gold.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("crafted_gold_hammer", RecipeCraftedTrigger.TriggerInstance.craftedItem(ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, VanillaRecipeProvider.getItemName(ItemRegistry.GOLDEN_HAMMER))))
                .save(consumer, "hammering/gold_hammer");

        AdvancementHolder diamond_hammer = Advancement.Builder.advancement().parent(gold_hammer)
                .display(
                        ItemRegistry.DIAMOND_HAMMER,
                        Component.translatable("advancements.hammering.diamond.title"),
                        Component.translatable("advancements.hammering.diamond.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("crafted_diamond_hammer", RecipeCraftedTrigger.TriggerInstance.craftedItem(ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, VanillaRecipeProvider.getItemName(ItemRegistry.DIAMOND_HAMMER))))
                .save(consumer, "hammering/diamond_hammer");

        AdvancementHolder netherite_hammer = Advancement.Builder.advancement().parent(diamond_hammer)
                .display(
                        ItemRegistry.NETHERITE_HAMMER,
                        Component.translatable("advancements.hammering.netherite.title"),
                        Component.translatable("advancements.hammering.netherite.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("crafted_netherite_hammer", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.NETHERITE_HAMMER))
                .save(consumer, "hammering/netherite_hammer");
    }
}

package de.crafty.skylife.advancements.sub;

import de.crafty.skylife.SkyLife;
import de.crafty.skylife.advancements.criterion.SkyLifeJoinTrigger;
import de.crafty.skylife.registry.BlockRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Consumer;

public class SkyLifeStoryAdvancementProvider implements AdvancementSubProvider {


    @Override
    public void generate(HolderLookup.Provider provider, Consumer<AdvancementHolder> consumer) {
        AdvancementHolder root = Advancement.Builder.advancement()
                .display(
                        Blocks.OAK_SAPLING,
                        Component.translatable("advancements.skylife.root.title"),
                        Component.translatable("advancements.skylife.root.description"),
                        ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "textures/gui/advancements/backgrounds/skylife.png"),
                        AdvancementType.TASK,
                        false,
                        true,
                        true
                )
                .requirements(AdvancementRequirements.Strategy.AND)
                .addCriterion("skylife_entered", SkyLifeJoinTrigger.TriggerInstance.hasEnteredSkylife())
                .addCriterion("first_step", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(ItemTags.LOGS)))
                .save(consumer, "skylife/root");

        AdvancementHolder leaf_press = Advancement.Builder.advancement().parent(root)
                .display(
                        BlockRegistry.LEAF_PRESS,
                        Component.translatable("advancements.skylife.leaf_press.title"),
                        Component.translatable("advancements.skylife.leaf_press.description"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("got_leafpress", InventoryChangeTrigger.TriggerInstance.hasItems(BlockRegistry.LEAF_PRESS))
                .save(consumer, "skylife/leaf_press");
    }
}

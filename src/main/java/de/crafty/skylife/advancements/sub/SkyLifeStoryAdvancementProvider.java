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

    }
}

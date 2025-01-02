package de.crafty.skylife.item;

import de.crafty.skylife.SkyLife;
import de.crafty.skylife.config.SkyLifeConfigs;
import de.crafty.skylife.network.SkyLifeNetworkServer;
import de.crafty.skylife.network.payload.SkyLifeClientEventPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class LootGemItem extends Item {

    public LootGemItem(Properties properties) {
        super(properties);
    }


    @Override
    public @NotNull InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack stack = player.getItemInHand(interactionHand);

        LootContent lootContent = SkyLifeConfigs.LOOT_GEM.forItem(this);
        if (lootContent == null)
            return InteractionResult.PASS;


        WeightedRandomList<LootEntry> weightedRandomList = WeightedRandomList.create(lootContent.lootEntries());

        Optional<LootEntry> optional = weightedRandomList.getRandom(level.getRandom());
        if (optional.isEmpty())
            return InteractionResult.PASS;

        LootEntry entry = optional.get();
        ItemStack lootStack = entry.loot().copy();

        player.awardStat(Stats.ITEM_USED.get(this));

        if (!level.isClientSide()) {
            lootStack.setCount(level.getRandom().nextInt(entry.max() - entry.min() + 1) + entry.min());

            if (stack.getCount() == 1 && !player.isCreative())
                player.setItemInHand(interactionHand, ItemStack.EMPTY);
            else {
                stack.consume(1, player);
            }
            player.getInventory().placeItemBackInInventory(lootStack);

            if (entry.loot().getRarity() != Rarity.COMMON)
                SkyLifeNetworkServer.sendUpdate(SkyLifeClientEventPayload.ClientEventType.LOOT_GEM_RARE, player.blockPosition(), level);
            else
                SkyLifeNetworkServer.sendUpdate(SkyLifeClientEventPayload.ClientEventType.LOOT_GEM_NORMAL, player.blockPosition(), level);

        }


        return InteractionResult.SUCCESS;
    }


    public static void checkGemLoot(ServerLevel serverLevel, LivingEntity entity, DamageSource source) {

        int lootingLevel = source.getWeaponItem() == null ? 0 :  EnchantmentHelper.getItemEnchantmentLevel(serverLevel.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.LOOTING), source.getWeaponItem());

        SkyLifeConfigs.LOOT_GEM.getPossibleGems(entity.getType()).forEach((item, lootContent) -> {

            float bonusChance = 0.1F * lootingLevel;
            if (serverLevel.getRandom().nextFloat() < lootContent.entityChances.get(entity.getType()) + bonusChance) {
                ItemStack gemStack = new ItemStack(item);
                gemStack.setCount(Math.min(1 + serverLevel.getRandom().nextInt(lootingLevel + 1), 3));
                entity.spawnAtLocation(serverLevel, item);
            }

        });
    }

    public record LootContent(List<LootEntry> lootEntries, HashMap<EntityType<?>, Float> entityChances) {

        public static class Builder {

            private final List<LootEntry> lootEntries;
            private final HashMap<EntityType<?>, Float> entityChances;


            public Builder() {
                this.lootEntries = new ArrayList<>();
                this.entityChances = new HashMap<>();
            }

            public Builder addEntry(ItemStack stack, int weight) {
                this.lootEntries.add(new LootEntry(stack, weight, stack.getCount(), stack.getCount()));
                return this;
            }

            public Builder addEntry(ItemStack stack, int weight, int min, int max) {
                this.lootEntries.add(new LootEntry(stack, weight, min, max));
                return this;
            }

            public Builder addEntry(Item item, int weight) {
                this.lootEntries.add(new LootEntry(new ItemStack(item), weight, 1, 1));
                return this;
            }

            public Builder addEntry(Item item, int weight, int min, int max) {
                this.lootEntries.add(new LootEntry(new ItemStack(item), weight, min, max));
                return this;
            }

            public Builder fromEntity(EntityType<?> entityType, float chance) {
                this.entityChances.put(entityType, chance);
                return this;
            }

            public LootContent build() {
                return new LootContent(this.lootEntries, this.entityChances);
            }

        }

    }

    public record LootEntry(ItemStack loot, int weight, int min, int max) implements WeightedEntry {

        @Override
        public @NotNull Weight getWeight() {
            return Weight.of(this.weight);
        }
    }
}

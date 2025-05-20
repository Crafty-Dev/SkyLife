package de.crafty.skylife.eiv.recipes.loot_gem;

import de.crafty.eiv.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.api.recipe.IEivViewRecipe;
import de.crafty.eiv.recipe.inventory.RecipeViewMenu;
import de.crafty.eiv.recipe.inventory.SlotContent;
import de.crafty.skylife.item.LootGemItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.item.Item;

import java.text.DecimalFormat;
import java.util.*;

public class LootGemViewRecipe implements IEivViewRecipe {

    private final SlotContent gem;
    private final HashMap<LootGemItem.LootEntry, SlotContent> loot;
    private final LootGemItem.LootContent lootContent;

    public LootGemViewRecipe(Item gem, LootGemItem.LootContent loot) {
        this.gem = SlotContent.of(gem);

        HashMap<LootGemItem.LootEntry, SlotContent> map = new LinkedHashMap<>();
        loot.lootEntries().forEach(lootEntry -> {
            map.put(lootEntry, SlotContent.of(lootEntry.loot()));
        });

        this.lootContent = loot;
        this.loot = map;
    }

    @Override
    public IEivRecipeViewType getViewType() {
        return LootGemViewType.INSTANCE;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        slotFillContext.bindSlot(0, this.gem);
        slotFillContext.addAdditionalStackModifier(0, (itemStack, list) -> {
            list.add(Component.translatable("view.skylife.type.loot_gem.from").append(": ").withStyle(ChatFormatting.GRAY));
            list.add(Component.empty());
            this.lootContent.entityChances().forEach((entityType, aFloat) -> {
                DecimalFormat df = new DecimalFormat("###.#");
                list.add(Component.translatable(entityType.toString()).append(": ").withStyle(ChatFormatting.GRAY).append(Component.literal(df.format(aFloat * 100.0F)).append("%").withStyle(ChatFormatting.DARK_PURPLE)));
            });
            list.add(Component.empty());
            list.add(Component.translatable("view.skylife.type.loot_gem.looting").withStyle(ChatFormatting.GRAY).append(" = +").append(Component.literal("10%").withStyle(ChatFormatting.GOLD)));
        });

        int totalWeight = WeightedRandom.getTotalWeight(this.loot.keySet().stream().toList());

        for(int i = 0; i < this.loot.size(); i++){
            LootGemItem.LootEntry entry = this.loot.keySet().stream().toList().get(i);
            SlotContent content = this.loot.get(entry);

            float chance = entry.weight() / (float) totalWeight;
            DecimalFormat format = new DecimalFormat("###.##");
            String s = format.format(chance * 100.0F);

            slotFillContext.bindSlot(i + 1, content);
            slotFillContext.addAdditionalStackModifier(i + 1, (itemStack, list) -> {
                list.add(Component.translatable("vie.skylife.type.loot_gem.chance").append(": ").withStyle(ChatFormatting.GRAY).append(Component.literal(s).append("%").withStyle(ChatFormatting.DARK_PURPLE)));

                if(entry.min() == entry.max() && entry.min() == 1)
                    return;

                MutableComponent countComp = Component.literal(String.valueOf(entry.min())).withStyle(ChatFormatting.DARK_AQUA);
                if(entry.min() != entry.max())
                    countComp.append("-").append(String.valueOf(entry.max()));

                list.add(Component.translatable("view.skylife.type.loot_gem.amount").append(": ").withStyle(ChatFormatting.GRAY).append(countComp));
            });
        }
    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(this.gem);
    }

    @Override
    public List<SlotContent> getResults() {
        List<SlotContent> list = new ArrayList<>(this.loot.values().stream().toList());
        list.add(this.gem);
        return list;
    }
}

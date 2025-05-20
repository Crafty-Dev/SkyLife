package de.crafty.skylife.eiv.recipes.hammering;

import de.crafty.eiv.api.recipe.IEivRecipeViewType;
import de.crafty.eiv.api.recipe.IEivViewRecipe;
import de.crafty.eiv.recipe.inventory.RecipeViewMenu;
import de.crafty.eiv.recipe.inventory.SlotContent;
import de.crafty.skylife.config.HammerConfig;
import de.crafty.skylife.registry.ItemRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

public class HammeringViewRecipe implements IEivViewRecipe {

    private final SlotContent inputBlocks;

    private final List<HammerConfig.HammerDrop> hammerDrops;
    private final List<SlotContent> drops;

    private final SlotContent hammers = SlotContent.of(List.of(
            new ItemStack(ItemRegistry.WOODEN_HAMMER),
            new ItemStack(ItemRegistry.STONE_HAMMER),
            new ItemStack(ItemRegistry.IRON_HAMMER),
            new ItemStack(ItemRegistry.GOLDEN_HAMMER),
            new ItemStack(ItemRegistry.DIAMOND_HAMMER),
            new ItemStack(ItemRegistry.NETHERITE_HAMMER)
    ));

    public HammeringViewRecipe(List<Block> blocks, List<HammerConfig.HammerDrop> hammerDrops) {

        this.hammerDrops = hammerDrops;

        List<Item> items = new ArrayList<>();
        blocks.forEach(block -> {
            items.add(block.asItem());
        });
        this.inputBlocks = SlotContent.ofItemList(items);

        this.drops = new ArrayList<>();
        hammerDrops.forEach(hammerDrop -> {
           this.drops.add(SlotContent.of(hammerDrop.item()));
        });

    }

    @Override
    public IEivRecipeViewType getViewType() {
        return HammeringViewType.INSTANCE;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {

        //Add Hammers
        slotFillContext.bindSlot(0, this.hammers);

        slotFillContext.bindSlot(1, this.inputBlocks);

        for(int i = 0; i < this.drops.size(); i++){
            slotFillContext.bindSlot(2 + i, this.drops.get(i));

            int j = i;
            slotFillContext.addAdditionalStackModifier(2 + i, (itemStack, tooltip) -> {
                HammerConfig.HammerDrop drop = this.hammerDrops.get(j);
                tooltip.add(Component.translatable("skylife.hammerdrop.chance").append(": ").withStyle(ChatFormatting.GRAY).append(Component.literal(((int) (drop.chance() * 100)) + "%").withStyle(ChatFormatting.DARK_PURPLE)));
                tooltip.add(Component.translatable("skylife.hammerdrop.amount").append(": ").withStyle(ChatFormatting.GRAY).append(Component.literal(drop.min() + "-" + drop.max())).withStyle(ChatFormatting.GRAY));
            });
        }

    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(this.inputBlocks, this.hammers);
    }

    @Override
    public List<SlotContent> getResults() {
        return this.drops;
    }
}

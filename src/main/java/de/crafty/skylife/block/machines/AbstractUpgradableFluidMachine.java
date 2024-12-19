package de.crafty.skylife.block.machines;

import de.crafty.lifecompat.energy.block.BaseEnergyBlock;
import de.crafty.lifecompat.fluid.block.BaseFluidEnergyBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class AbstractUpgradableFluidMachine<S extends BlockEntity> extends BaseFluidEnergyBlock implements IUpgradableMachine<S> {


    protected AbstractUpgradableFluidMachine(Properties properties, Type energyBlockType, int capacity) {
        super(properties, energyBlockType, capacity);
    }


    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        ItemInteractionResult result = super.useItemOn(itemStack, blockState, level, blockPos, player, interactionHand, blockHitResult);
        if (result != ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION)
            return result;

        Class<S> beClass = this.getMachineBE();
        if (this.canApplyUpgrade(level, blockState, blockPos, itemStack) && beClass.isInstance(level.getBlockEntity(blockPos))) {
            if (level.isClientSide())
                return ItemInteractionResult.CONSUME;

            this.onUpgrade(level, blockState, blockPos, itemStack, beClass.cast(level.getBlockEntity(blockPos)));
            itemStack.shrink(1);
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    protected abstract Property<? extends Comparable<?>> getUpgradeProperty();


    @Override
    protected @NotNull List<ItemStack> getDrops(BlockState blockState, LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(blockState, builder);


        for (ItemStack drop : drops) {
            if (drop.is(this.asItem()))
                drop.update(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY, blockItemStateProperties -> {

                    blockItemStateProperties = blockItemStateProperties.with(this.getUpgradeProperty(), blockState);
                    return blockItemStateProperties;
                });
        }
        return drops;
    }


    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, tooltipContext, list, tooltipFlag);

        BlockItemStateProperties stateProps = itemStack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY);
        Comparable<?> prop = stateProps.get(this.getUpgradeProperty());

        if(prop == null)
            return;

        list.add(
                Component.translatable("skylife.machine.upgrade").append(": ").withStyle(ChatFormatting.GRAY)
                        .append(Component.literal(prop.toString()).withStyle(ChatFormatting.BLUE))
        );
    }
}

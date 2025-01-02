package de.crafty.skylife.blockentities.machines.integrated;

import de.crafty.lifecompat.api.energy.consumer.AbstractEnergyConsumer;
import de.crafty.skylife.block.machines.integrated.BlockBreakerBlock;
import de.crafty.skylife.inventory.BlockBreakerMenu;
import de.crafty.skylife.item.HammerItem;
import de.crafty.skylife.logic.HammerLogic;
import de.crafty.skylife.network.SkyLifeNetworkServer;
import de.crafty.skylife.network.payload.SkyLifeClientEventPayload;
import de.crafty.skylife.registry.BlockEntityRegistry;
import de.crafty.skylife.registry.BlockRegistry;
import de.crafty.skylife.registry.TagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class BlockBreakerBlockEntity extends AbstractEnergyConsumer implements Container, MenuProvider {

    private final NonNullList<ItemStack> items = NonNullList.withSize(19, ItemStack.EMPTY);


    private ToolType toolType;
    private float destroyProgress;
    private float lastSentProgress;
    private Block targetBlock;
    private int destroyTicks;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int dataSlot) {
            return switch (dataSlot) {
                case 0 -> BlockBreakerBlockEntity.this.getStoredEnergy();
                case 1 -> BlockBreakerBlockEntity.this.getEnergyCapacity();
                default -> 0;
            };
        }

        @Override
        public void set(int dataSlot, int amount) {
            switch (dataSlot) {
                case 0 -> BlockBreakerBlockEntity.this.setStoredEnergy(amount);
                case 1 -> BlockBreakerBlockEntity.this.setEnergyCapacity(amount);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public BlockBreakerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityRegistry.BLOCK_BREAKER, blockPos, blockState, BlockRegistry.BLOCK_BREAKER.getCapacity());

        this.toolType = ToolType.NONE;
        this.destroyProgress = -1;
        this.lastSentProgress = -1;
        this.destroyTicks = 0;
        this.targetBlock = Blocks.AIR;
    }

    @Override
    public boolean isAccepting(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    @Override
    public boolean isConsuming(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    @Override
    public int getMaxInput(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return 180;
    }

    //TODO Consumption display
    //TODO add running prop in lifecompat consumer
    @Override
    public int getConsumptionPerTick(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return this.isIdling() ? 10 : this.getToolType().getEnergyConsumption();
    }

    public float getDestroyProgress() {
        return this.destroyProgress;
    }

    public boolean isIdling() {
        return this.targetBlock == Blocks.AIR || this.targetBlock.defaultBlockState().liquid() || this.getToolType() == ToolType.NONE;
    }

    public Block getTargetBlock() {
        return this.targetBlock;
    }

    public BlockPos getTargetPos() {
        return this.getBlockPos().relative(this.getBlockState().getValue(BlockBreakerBlock.FACING));
    }

    public ToolType getToolType() {
        return this.toolType;
    }

    public void setTargetBlock(BlockState targetState) {
        this.targetBlock = targetState.getBlock();
        this.setDestroyProgress(-1.0F);
        this.setChanged();
    }

    public void increaseDestroyProgress(float destroyProgress) {
        this.destroyProgress += destroyProgress;
        this.lastSentProgress = destroyProgress;
        this.destroyTicks++;
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
    }

    //Used when block changed
    public void setDestroyProgress(float destroyProgress) {
        this.destroyProgress = destroyProgress;
        this.lastSentProgress = destroyProgress;
        this.destroyTicks = 0;
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
    }

    public void setToolType(ToolType toolType) {
        this.toolType = toolType;
        this.level.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(), GameEvent.Context.of(this.getBlockState()));
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
    }


    public ToolType getToolTypeOfTool(ItemStack stack) {
        if (stack.isEmpty())
            return ToolType.NONE;

        if(stack.is(TagRegistry.NETHERITE_TOOLS))
            return ToolType.NETHERITE;

        if(stack.is(TagRegistry.DIAMOND_TOOLS))
            return ToolType.DIAMOND;

        if(stack.is(TagRegistry.GOLD_TOOLS))
            return ToolType.GOLD;

        if(stack.is(TagRegistry.IRON_TOOLS))
            return ToolType.IRON;

        if(stack.is(TagRegistry.STONE_TOOLS))
            return ToolType.STONE;

        if(stack.is(TagRegistry.WOODEN_TOOLS))
            return ToolType.WOOD;

        return ToolType.NONE;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);

        tag.putString("toolType", this.toolType.name());
        tag.putFloat("destroyProgress", this.destroyProgress);
        tag.putFloat("lastSentProgress", this.lastSentProgress);
        tag.putString("targetBlock", BuiltInRegistries.BLOCK.getKey(this.targetBlock).toString());
        tag.putInt("destroyTicks", this.destroyTicks);

        ContainerHelper.saveAllItems(tag, this.items, provider);
    }

    @Override
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.loadAdditional(compoundTag, provider);

        this.toolType = ToolType.valueOf(compoundTag.getString("toolType"));
        this.destroyProgress = compoundTag.getFloat("destroyProgress");
        this.lastSentProgress = compoundTag.getFloat("lastSentProgress");
        this.targetBlock = BuiltInRegistries.BLOCK.getValue(ResourceLocation.tryParse(compoundTag.getString("targetBlock")));
        this.destroyTicks = compoundTag.getInt("destroyTicks");

        ContainerHelper.loadAllItems(compoundTag, this.items, provider);
    }


    public float getDestroyProgress(ServerLevel level, BlockPos blockPos) {
        ItemStack tool = this.getItem(0);
        BlockState state = level.getBlockState(blockPos);

        if (tool.isEmpty())
            return 0.0F;

        AtomicReference<Float> f = new AtomicReference<>(this.getItem(0).getDestroySpeed(state));

        if (f.get() > 1.0F) {
            EnchantmentHelper.forEachModifier(tool, EquipmentSlot.MAINHAND, (attributeHolder, attributeModifier) -> {
                if (attributeHolder.is(Attributes.MINING_EFFICIENCY))
                    f.set((float) (f.get() + attributeModifier.amount()));
            });
        }

        float blockBreakSpeed = state.getDestroySpeed(level, blockPos);
        if (blockBreakSpeed != -1.0F) {
            return f.get() / blockBreakSpeed / ((!state.requiresCorrectToolForDrops() || tool.isCorrectToolForDrops(state)) ? 30 : 100);
        }

        return 0.0F;
    }

    @Override
    protected void performAction(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        BlockPos targetPos = this.getTargetPos();
        if (serverLevel.getBlockState(targetPos).isAir())
            return;

        BlockState targetState = this.getLevel().getBlockState(targetPos);

        float destroyProgress = this.getDestroyProgress((ServerLevel) level, targetPos);

        if (destroyProgress != this.lastSentProgress)
            this.setDestroyProgress(destroyProgress);
        else
            this.increaseDestroyProgress(destroyProgress);

        if (this.destroyTicks % 4.0F == 0.0F && !this.isIdling())
            SkyLifeNetworkServer.sendUpdate(SkyLifeClientEventPayload.ClientEventType.BB_BLOCK_HIT, targetPos, serverLevel);


        if (this.getDestroyProgress() >= 1.0F) {
            ItemStack toolStack = this.getItem(0);
            boolean bl = level.removeBlock(targetPos, false);
            if (bl)
                targetState.getBlock().destroy(level, targetPos, targetState);
            level.levelEvent(2001, targetPos, Block.getId(targetState));
            this.setDestroyProgress(-1.0F);

            if (toolStack.isCorrectToolForDrops(targetState) || !targetState.requiresCorrectToolForDrops()) {

                if (toolStack.getItem() instanceof HammerItem) {
                    SkyLifeNetworkServer.sendUpdate(SkyLifeClientEventPayload.ClientEventType.BB_HAMMER_BLOCK, targetPos, serverLevel);
                    HammerLogic.handleEffects(serverLevel, null, targetPos, targetState);
                }

                List<ItemStack> drops = toolStack.getItem() instanceof HammerItem ? HammerLogic.getRandomDrop(targetBlock, serverLevel) : Block.getDrops(targetState, serverLevel, targetPos, level.getBlockEntity(targetPos), null, toolStack);
                drops.forEach(stack -> {

                    for (int i = 1; i < this.getContainerSize(); i++) {
                        ItemStack slotItem = this.getItem(i);

                        if (stack.isEmpty())
                            break;

                        if (slotItem.isEmpty()) {
                            this.setItem(i, stack);
                            break;
                        }

                        if (slotItem.is(stack.getItem()) && slotItem.getCount() < this.getMaxStackSize(slotItem)) {
                            int prevSlotCount = slotItem.getCount();
                            slotItem.setCount(Math.min(slotItem.getCount() + stack.getCount(), this.getMaxStackSize(slotItem)));
                            stack.setCount(stack.getCount() - (this.getMaxStackSize(slotItem) - prevSlotCount));
                        }
                    }
                });
            }

            Tool tool = toolStack.get(DataComponents.TOOL);
            if (tool != null) {
                if (targetState.getDestroySpeed(level, blockPos) != 0.0F && tool.damagePerBlock() > 0) {
                    toolStack.hurtAndBreak(tool.damagePerBlock(), (ServerLevel) level, null, item -> {
                    });
                    if (toolStack.isEmpty()) {
                        this.setToolType(ToolType.NONE);
                        SkyLifeNetworkServer.sendUpdate(SkyLifeClientEventPayload.ClientEventType.BB_ITEM_BREAK, blockPos, serverLevel);
                    }
                }
            }
        }

    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BlockBreakerBlockEntity blockBreakerBlockEntity) {
        if (level.isClientSide())
            return;

        BlockPos targetPos = blockPos.relative(blockState.getValue(BlockBreakerBlock.FACING));
        BlockState target = level.getBlockState(targetPos);

        if (target.getBlock() != blockBreakerBlockEntity.getTargetBlock())
            blockBreakerBlockEntity.setTargetBlock(target);

        blockBreakerBlockEntity.energyTick((ServerLevel) level, blockPos, blockState);
    }


    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);
        this.saveAdditional(tag, provider);
        return tag;
    }

    //----------- Container -----------

    @Override
    public int getContainerSize() {
        return 19;
    }

    @Override
    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    @Override
    public @NotNull ItemStack getItem(int i) {
        return this.items.get(i);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        ItemStack stack = ContainerHelper.removeItem(this.items, slot, amount);
        if (!stack.isEmpty())
            this.setChanged();

        if (slot == 0) {
            ToolType tt = this.getToolTypeOfTool(this.getItem(0));
            if (this.getToolType() != tt)
                this.setToolType(tt);
        }

        return stack;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.items, slot);
    }

    @Override
    public boolean canTakeItem(Container container, int slot, ItemStack itemStack) {
        return slot != 0;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack itemStack) {
        return slot == 0 && this.getToolTypeOfTool(itemStack) != ToolType.NONE;
    }

    @Override
    public void setItem(int slot, ItemStack itemStack) {
        this.items.set(slot, itemStack);
        itemStack.limitSize(this.getMaxStackSize(itemStack));
        this.setChanged();
        if (slot == 0) {
            ToolType tt = this.getToolTypeOfTool(itemStack);
            if (this.getToolType() != tt)
                this.setToolType(tt);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.skylife.block_breaker");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int inventoryId, Inventory inventory, Player player) {
        return new BlockBreakerMenu(inventoryId, inventory, this, this.dataAccess);
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput dataComponentInput) {
        dataComponentInput.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(this.items);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        builder.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.items));
    }

    public enum ToolType {
        NONE(10),
        WOOD(20),
        STONE(30),
        IRON(40),
        GOLD(45),
        DIAMOND(75),
        NETHERITE(100);

        final int energyConsumption;

        ToolType(int energyConsumption) {
            this.energyConsumption = energyConsumption;
        }

        public int getEnergyConsumption() {
            return this.energyConsumption;
        }
    }
}

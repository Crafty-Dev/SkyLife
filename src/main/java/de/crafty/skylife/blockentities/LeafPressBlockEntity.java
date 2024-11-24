package de.crafty.skylife.blockentities;

import de.crafty.skylife.registry.BlockEntityRegistry;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class LeafPressBlockEntity extends BlockEntity {


    private ItemStack content = ItemStack.EMPTY;

    public LeafPressBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.LEAF_PRESS, pos, state);
    }


    public ItemStack getContent() {
        return this.content;
    }

    public void setContent(ItemStack content) {
        this.content = content;
        this.setChanged();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registryLookup) {
        super.loadAdditional(tag, registryLookup);

        CompoundTag contentTag = tag.getCompound("content");
        this.content = ItemStack.parseOptional(registryLookup, contentTag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registryLookup) {
        super.saveAdditional(tag, registryLookup);

        Tag contentTag = this.content.saveOptional(registryLookup);
        tag.put("content", contentTag);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registryLookup);
        return tag;
    }

}

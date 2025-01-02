package de.crafty.skylife.blockentities;

import com.mojang.authlib.GameProfile;
import de.crafty.skylife.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GraveStoneBlockEntity extends BlockEntity {


    private GameProfile gameProfile;
    private List<ItemStack> items = new ArrayList<>();
    private long timestamp;

    public GraveStoneBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.GRAVE_STONE, pos, state);

        this.timestamp = 0;
    }

    public void setPlayerProfile(GameProfile gameProfile){
        this.gameProfile = gameProfile;

        this.setChanged();
    }

    public long getTimestamp(){
        return this.timestamp;
    }

    public void setTimestamp(long timestamp){
        this.timestamp = timestamp;
        this.setChanged();
    }


    public GameProfile getOwner(){
        return this.gameProfile;
    }

    public void setContent(List<ItemStack> content){
        this.items = content;
        this.setChanged();
    }

    public List<ItemStack> getContent(){
        return this.items;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registryLookup) {
        super.loadAdditional(tag, registryLookup);

        this.items.clear();

        ListTag listTag = tag.getList("items", ListTag.TAG_COMPOUND);
        for(int i = 0; i < listTag.size(); i++){
            CompoundTag compoundTag = listTag.getCompound(i);
            this.items.add(ItemStack.parse(registryLookup, compoundTag).get());
        }

        if(tag.contains("owner"))
            this.gameProfile = ExtraCodecs.GAME_PROFILE.decode(NbtOps.INSTANCE, tag.getCompound("owner")).getOrThrow().getFirst();

        this.timestamp = tag.getLong("timestamp");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registryLookup) {
        super.saveAdditional(tag, registryLookup);

        ListTag listTag = new ListTag();
        this.items.forEach(stack -> listTag.add(stack.save(registryLookup, new CompoundTag())));
        tag.put("items", listTag);

        if(this.gameProfile == null)
            return;

        CompoundTag encodedProfile = (CompoundTag) ExtraCodecs.GAME_PROFILE.encode(this.gameProfile, NbtOps.INSTANCE, new CompoundTag()).getOrThrow();
        tag.put("owner", encodedProfile);

        tag.putLong("timestamp", this.timestamp);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        CompoundTag tag = new CompoundTag();
        this.saveAdditional(tag, registryLookup);
        return tag;
    }
}

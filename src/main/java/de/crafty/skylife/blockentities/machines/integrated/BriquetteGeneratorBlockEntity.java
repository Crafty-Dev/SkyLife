package de.crafty.skylife.blockentities.machines.integrated;

import de.crafty.lifecompat.api.energy.provider.AbstractEnergyProvider;
import de.crafty.lifecompat.energy.block.BaseEnergyBlock;
import de.crafty.lifecompat.util.EnergyUnitConverter;
import de.crafty.skylife.block.machines.SkyLifeEnergyStorageBlock;
import de.crafty.skylife.block.machines.integrated.BlockBreakerBlock;
import de.crafty.skylife.block.machines.integrated.BriquetteGeneratorBlock;
import de.crafty.skylife.registry.BlockEntityRegistry;
import de.crafty.skylife.item.BriquettItem;
import de.crafty.skylife.network.SkyLifeNetworkServer;
import de.crafty.skylife.network.payload.SkyLifeClientEventPayload;
import de.crafty.skylife.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BriquetteGeneratorBlockEntity extends AbstractEnergyProvider {


    private ItemStack briquetteStack = ItemStack.EMPTY;
    private long currentTick = 0;

    public BriquetteGeneratorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityRegistry.BRIQUETTE_GENERATOR, blockPos, blockState, BlockRegistry.BRIQUETTE_GENERATOR.getCapacity());
    }

    public void setBriquett(ItemStack briquett) {
        this.briquetteStack = briquett;
        this.setChanged();
    }

    public ItemStack getBriquette() {
        return this.briquetteStack;
    }

    public BriquettItem getBriquetteItem() {
        return this.briquetteStack.isEmpty() ? null : (BriquettItem) briquetteStack.getItem();
    }

    public void tick() {
        this.currentTick++;
        this.setChanged();
    }

    public void resetTick() {
        this.currentTick = 0;
        this.setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);

        tag.put("briquette", this.briquetteStack.saveOptional(provider));
        tag.putLong("currentTick", this.currentTick);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);

        this.briquetteStack = ItemStack.parseOptional(provider, tag.getCompound("briquette"));
        this.currentTick = tag.getLong("currentTick");
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);
        this.saveAdditional(tag, provider);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BriquetteGeneratorBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        blockEntity.energyTick((ServerLevel) level, blockPos, blockState);
        if (!blockState.getValue(BriquetteGeneratorBlock.WORKING) || blockEntity.getBriquetteItem() == null)
            return;

        blockEntity.tick();
        if (blockEntity.currentTick >= blockEntity.getBriquetteItem().getBurnTime()) {
            blockEntity.setBriquett(ItemStack.EMPTY);
            BlockState state = blockState.setValue(BriquetteGeneratorBlock.WORKING, false).setValue(BriquetteGeneratorBlock.BRIQUETTE_TYPE, BriquetteGeneratorBlock.BriquetteType.EMPTY);
            level.setBlock(blockPos, state, 3);
            level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(null, state));
            blockEntity.resetTick();
            SkyLifeNetworkServer.sendUpdate(SkyLifeClientEventPayload.ClientEventType.BG_WORKING_FINISHED, blockPos, level);
        }

    }


    @Override
    public int getMaxOutput(ServerLevel level, BlockPos pos, BlockState state) {
        return 40;
    }

    @Override
    public boolean isTransferring(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    @Override
    public boolean isGenerating(ServerLevel world, BlockPos pos, BlockState state) {
        return state.getValue(BriquetteGeneratorBlock.WORKING);
    }

    @Override
    public int getGenerationPerTick(ServerLevel level, BlockPos pos, BlockState state) {
        return state.getValue(BriquetteGeneratorBlock.BRIQUETTE_TYPE) == BriquetteGeneratorBlock.BriquetteType.WOOD ? 20 : 40;
    }
}

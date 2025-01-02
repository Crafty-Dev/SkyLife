package de.crafty.skylife.blockentities;

import de.crafty.skylife.block.EndPortalFrameBlock;
import de.crafty.skylife.registry.BlockEntityRegistry;
import de.crafty.skylife.registry.BlockRegistry;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class EndPortalCoreBlockEntity extends BlockEntity {


    private int animationTick = -1;

    public EndPortalCoreBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.END_PORTAL_CORE, pos, state);
    }


    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registryLookup) {
        super.loadAdditional(tag, registryLookup);

        this.animationTick = tag.getInt("animationTick");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registryLookup) {
        super.saveAdditional(tag, registryLookup);

        tag.putInt("animationTick", this.animationTick);
    }


    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registryLookup);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public boolean hasAnimationStarted() {
        return this.animationTick >= 0;
    }

    public boolean hasAnimationFinished() {
        return this.animationTick > this.getPositioningAnimationTime() + this.getCircleAnimationTime() + this.getTransformationAnimationTime();
    }

    public void startAnimation() {
        this.animationTick = 0;
    }

    public int getAnimationTick() {
        return this.animationTick;
    }

    public double getPositioningAnimationSpeed() {
        return 0.025D;
    }

    public double getCircleAnimationSpeed() {
        return 2.5D;
    }

    public double getTransformationAnimationSpeed() {
        return 0.025D;
    }

    public double getPositioningAnimationTime() {
        return new Vec3(Math.sin(0.0D) * 0.75D, 0.75D, Math.cos(0.0D) * 0.75D).length() / this.getPositioningAnimationSpeed();
    }

    public double getCircleAnimationTime() {
        return 360.0D / this.getCircleAnimationSpeed();
    }

    public double getTransformationAnimationTime() {
        return 1.0D / this.getTransformationAnimationSpeed();
    }

    public Vec3[] getPortalFrameLocations(BlockPos pos) {

        List<Vec3> positions = new ArrayList<>();

        positions.add(new Vec3(pos.getX() + 0.5D + 2, pos.getY() + 0.5D, pos.getZ() + 0.5D));
        positions.add(new Vec3(pos.getX() + 0.5D + 2, pos.getY() + 0.5D, pos.getZ() + 0.5D + 1));
        positions.add(new Vec3(pos.getX() + 0.5D + 1, pos.getY() + 0.5D, pos.getZ() + 0.5D + 2));
        positions.add(new Vec3(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D + 2));
        positions.add(new Vec3(pos.getX() + 0.5D - 1, pos.getY() + 0.5D, pos.getZ() + 0.5D + 2));
        positions.add(new Vec3(pos.getX() + 0.5D - 2, pos.getY() + 0.5D, pos.getZ() + 0.5D + 1));
        positions.add(new Vec3(pos.getX() + 0.5D - 2, pos.getY() + 0.5D, pos.getZ() + 0.5D));
        positions.add(new Vec3(pos.getX() + 0.5D - 2, pos.getY() + 0.5D, pos.getZ() + 0.5D - 1));
        positions.add(new Vec3(pos.getX() + 0.5D - 1, pos.getY() + 0.5D, pos.getZ() + 0.5D - 2));
        positions.add(new Vec3(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D - 2));
        positions.add(new Vec3(pos.getX() + 0.5D + 1, pos.getY() + 0.5D, pos.getZ() + 0.5D - 2));
        positions.add(new Vec3(pos.getX() + 0.5D + 2, pos.getY() + 0.5D, pos.getZ() + 0.5D - 1));

        return positions.toArray(new Vec3[0]);
    }


    public static void tick(Level level, BlockPos pos, BlockState state, EndPortalCoreBlockEntity blockEntity) {
        if (blockEntity.hasAnimationStarted())
            blockEntity.animationTick++;

        if (blockEntity.hasAnimationFinished()) {
            blockEntity.animationTick = -1;
            for (Vec3 frameLocVec : blockEntity.getPortalFrameLocations(pos)) {
                BlockPos framePos = BlockPos.containing(frameLocVec);
                level.setBlock(framePos, BlockRegistry.END_PORTAL_FRAME.defaultBlockState().setValue(EndPortalFrameBlock.FILLED, true).setValue(EndPortalFrameBlock.TRANSFORMING, false), 3);
                level.playLocalSound(framePos.getX(), framePos.getY(), framePos.getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 0.125F, 1.0F, true);
            }

            for(int xOff = -1; xOff <= 1; xOff++){
                for(int zOff = -1; zOff <= 1; zOff++){
                    level.setBlock(pos.offset(xOff, 0, zOff), Blocks.END_PORTAL.defaultBlockState(), 3);
                    if(xOff == 0 && zOff == 0)
                        level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, 0.5F, 1.0F, false);
                }
            }

            for(float x = 0; x < 3.0F; x += 0.25F){
                Vector3f particlePos = new Vector3f(pos.getX() - 1 + x, pos.getY() + 1.0F - 0.1875F, pos.getZ() - 1);

                level.addParticle(new DustParticleOptions(new Color(50, 65, 130).getRGB(), 1), particlePos.x(), particlePos.y(), particlePos.z(), 0.0D, 0.0D, 0.0D);
                particlePos.add(0.0F, 0.0F, 3.0F);
                level.addParticle(new DustParticleOptions(new Color(50, 65, 130).getRGB(), 1), particlePos.x(), particlePos.y(), particlePos.z(), 0.0D, 0.0D, 0.0D);
            }
            for(float z = 0; z < 3.0F; z += 0.25F){
                Vector3f particlePos = new Vector3f(pos.getX() - 1, pos.getY() + 1.0F - 0.1875F, pos.getZ() - 1 + z);
                level.addParticle(new DustParticleOptions(new Color(50, 65, 130).getRGB(), 1), particlePos.x(), particlePos.y(), particlePos.z(), 0.0D, 0.0D, 0.0D);
                particlePos.add(3.0F, 0.0F, 0.0F);
                level.addParticle(new DustParticleOptions(new Color(50, 65, 130).getRGB(), 1), particlePos.x(), particlePos.y(), particlePos.z(), 0.0D, 0.0D, 0.0D);
            }
        }
    }

}

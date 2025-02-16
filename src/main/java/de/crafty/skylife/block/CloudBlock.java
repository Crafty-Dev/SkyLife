package de.crafty.skylife.block;

import de.crafty.skylife.registry.EnchantmentRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class CloudBlock extends HalfTransparentBlock {


    public CloudBlock(Properties properties) {
        super(properties);
    }


    @Override
    protected @NotNull VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        if (collisionContext instanceof EntityCollisionContext ctx && ctx.getEntity() instanceof ItemEntity item && item.getItem().getEnchantments().getLevel(item.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(EnchantmentRegistry.CLOUD_WALKER)) > 0)
            return Shapes.block();

        if (collisionContext instanceof EntityCollisionContext ctx && ctx.getEntity() instanceof LivingEntity entity)
            return EnchantmentHelper.getEnchantmentLevel(entity.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(EnchantmentRegistry.CLOUD_WALKER), entity) > 0 ? Shapes.block() : Shapes.empty();

        return Shapes.empty();
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return Shapes.block();
    }


    @Override
    protected void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {

        entity.makeStuckInBlock(blockState, new Vec3(1.0F, 2.0F, 1.0F));
    }


    @Override
    public void stepOn(Level level, BlockPos blockPos, BlockState blockState, Entity entity) {
        super.stepOn(level, blockPos, blockState, entity);
    }

    @Override
    protected boolean skipRendering(BlockState blockState, BlockState blockState2, Direction direction) {
        return blockState2.getBlock() instanceof CloudBlock && !(blockState2.getBlock() instanceof SmallCloudBlock);
    }
}

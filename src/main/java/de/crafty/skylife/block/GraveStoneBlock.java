package de.crafty.skylife.block;

import de.crafty.skylife.blockentities.GraveStoneBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class GraveStoneBlock extends Block implements EntityBlock {

    private static final VoxelShape BOTTOM_0 = Block.box(1.0D, 0.0D, 0.0D, 15.0D, 1.0D, 16.0D);
    private static final VoxelShape BOTTOM_1 = Block.box(0.0D, 0.0D, 1.0D, 1.0D, 1.0D, 16.0D);
    private static final VoxelShape BOTTOM_2 = Block.box(15.0D, 0.0D, 1.0D, 16.0D, 1.0D, 16.0D);
    private static final VoxelShape BOTTOM_3 = Block.box(1.0D, 1.0D, 1.0D, 15.0D, 2.0D, 16.0D);


    private static final VoxelShape BOTTOM = Shapes.or(BOTTOM_0, BOTTOM_1, BOTTOM_2, BOTTOM_3);

    public GraveStoneBlock() {
        super(BlockBehaviour.Properties.of().noOcclusion().noLootTable().strength(0.5F).sound(SoundType.GRAVEL));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return BOTTOM;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GraveStoneBlockEntity(pos, state);
    }

    @Override
    public void wasExploded(Level world, BlockPos pos, Explosion explosion) {
        if (world.getBlockEntity(pos) instanceof GraveStoneBlockEntity blockEntity) {
            Player player = world.getPlayerByUUID(blockEntity.getOwner().getId());

            if (explosion.getHitPlayers().containsKey(player) && player.isDeadOrDying())
                return;

            blockEntity.getContent().forEach(stack -> Block.popResource(world, pos, stack));
        }

        super.wasExploded(world, pos, explosion);
    }

    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (world.getBlockEntity(pos) instanceof GraveStoneBlockEntity blockEntity && world instanceof ServerLevel)
            blockEntity.getContent().forEach(stack -> {
                ItemEntity entity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() + 0.25D, pos.getZ() + 0.5D, stack);
                ((ServerLevel) world).addFreshEntity(entity);
            });

        return super.playerWillDestroy(world, pos, state, player);
    }
}

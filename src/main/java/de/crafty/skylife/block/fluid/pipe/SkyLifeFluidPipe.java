package de.crafty.skylife.block.fluid.pipe;

import de.crafty.lifecompat.api.fluid.logistic.pipe.AbstractFluidPipeBlockEntity;
import de.crafty.lifecompat.api.fluid.logistic.pipe.BaseFluidPipeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public abstract class SkyLifeFluidPipe extends BaseFluidPipeBlock {

    private static final VoxelShape CORE = makeCoreShape();

    private static final VoxelShape NORTH_CONNECTOR = makeNorthConnectorShape();
    private static final VoxelShape EAST_CONNECTOR = makeEastConnectorShape();
    private static final VoxelShape SOUTH_CONNECTOR = makeSouthConnectorShape();
    private static final VoxelShape WEST_CONNECTOR = makeWestConnectorShape();
    private static final VoxelShape UP_CONNECTOR = makeUpConnectorShape();
    private static final VoxelShape DOWN_CONNECTOR = makeDownConnectorShape();

    private static final VoxelShape NORTH_DEVICE = makeNorthDeviceShape();
    private static final VoxelShape EAST_DEVICE = makeEastDeviceShape();
    private static final VoxelShape SOUTH_DEVICE = makeSouthDeviceShape();
    private static final VoxelShape WEST_DEVICE = makeWestDeviceShape();
    private static final VoxelShape UP_DEVICE = makeUpDeviceShape();
    private static final VoxelShape DOWN_DEVICE = makeDownDeviceShape();


    public SkyLifeFluidPipe(Properties properties) {
        super(properties);
    }


    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {

        if(!player.isShiftKeyDown())
            return InteractionResult.PASS;

        if(blockState.getValue(NORTH) == ConnectionState.ATTACHED && this.hasClickedShape(blockHitResult, NORTH_DEVICE) && level.getBlockEntity(blockPos) instanceof AbstractFluidPipeBlockEntity pipe){
            pipe.loopThroughTransferMode(Direction.NORTH);
            return InteractionResult.SUCCESS;
        }

        if(blockState.getValue(EAST) == ConnectionState.ATTACHED && this.hasClickedShape(blockHitResult, EAST_DEVICE) && level.getBlockEntity(blockPos) instanceof AbstractFluidPipeBlockEntity pipe){
            pipe.loopThroughTransferMode(Direction.EAST);
            return InteractionResult.SUCCESS;
        }


        if(blockState.getValue(SOUTH) == ConnectionState.ATTACHED && this.hasClickedShape(blockHitResult, SOUTH_DEVICE) && level.getBlockEntity(blockPos) instanceof AbstractFluidPipeBlockEntity pipe){
            pipe.loopThroughTransferMode(Direction.SOUTH);
            return InteractionResult.SUCCESS;
        }

        if(blockState.getValue(WEST) == ConnectionState.ATTACHED && this.hasClickedShape(blockHitResult, WEST_DEVICE) && level.getBlockEntity(blockPos) instanceof AbstractFluidPipeBlockEntity pipe){
            pipe.loopThroughTransferMode(Direction.WEST);
            return InteractionResult.SUCCESS;
        }

        if(blockState.getValue(UP) == ConnectionState.ATTACHED && this.hasClickedShape(blockHitResult, UP_DEVICE) && level.getBlockEntity(blockPos) instanceof AbstractFluidPipeBlockEntity pipe){
            pipe.loopThroughTransferMode(Direction.UP);
            return InteractionResult.SUCCESS;
        }

        if(blockState.getValue(DOWN) == ConnectionState.ATTACHED && this.hasClickedShape(blockHitResult, DOWN_DEVICE) && level.getBlockEntity(blockPos) instanceof AbstractFluidPipeBlockEntity pipe){
            pipe.loopThroughTransferMode(Direction.DOWN);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    private boolean hasClickedShape(BlockHitResult blockHitResult, VoxelShape shape) {
        BlockPos clicked = blockHitResult.getBlockPos();
        Vec3 clickedLoc = blockHitResult.getLocation().subtract(clicked.getX(), clicked.getY(), clicked.getZ());


        return clickedLoc.x() >= shape.min(Direction.Axis.X) && clickedLoc.x() <= shape.max(Direction.Axis.X)
                && clickedLoc.y() >= shape.min(Direction.Axis.Y) && clickedLoc.y() <= shape.max(Direction.Axis.Y)
                && clickedLoc.z() >= shape.min(Direction.Axis.Z) && clickedLoc.z() <= shape.max(Direction.Axis.Z);
    }


    @Override
    protected @NotNull VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {

        VoxelShape shape = CORE;

        ConnectionState northState = blockState.getValue(NORTH);
        ConnectionState eastState = blockState.getValue(EAST);
        ConnectionState southState = blockState.getValue(SOUTH);
        ConnectionState westState = blockState.getValue(WEST);
        ConnectionState upState = blockState.getValue(UP);
        ConnectionState downState = blockState.getValue(DOWN);

        if(northState == ConnectionState.TRANSFER)
            shape = Shapes.or(shape, NORTH_CONNECTOR);

        if(eastState == ConnectionState.TRANSFER)
            shape = Shapes.or(shape, EAST_CONNECTOR);

        if(southState == ConnectionState.TRANSFER)
            shape = Shapes.or(shape, SOUTH_CONNECTOR);

        if(westState == ConnectionState.TRANSFER)
            shape = Shapes.or(shape, WEST_CONNECTOR);

        if(upState == ConnectionState.TRANSFER)
            shape = Shapes.or(shape, UP_CONNECTOR);

        if(downState == ConnectionState.TRANSFER)
            shape = Shapes.or(shape, DOWN_CONNECTOR);

        if(northState == ConnectionState.ATTACHED)
            shape = Shapes.or(shape, NORTH_DEVICE);

        if(eastState == ConnectionState.ATTACHED)
            shape = Shapes.or(shape, EAST_DEVICE);

        if(southState == ConnectionState.ATTACHED)
            shape = Shapes.or(shape, SOUTH_DEVICE);

        if(westState == ConnectionState.ATTACHED)
            shape = Shapes.or(shape, WEST_DEVICE);

        if(upState == ConnectionState.ATTACHED)
            shape = Shapes.or(shape, UP_DEVICE);

        if(downState == ConnectionState.ATTACHED)
            shape = Shapes.or(shape, DOWN_DEVICE);

        return shape;
    }

    public static VoxelShape makeCoreShape(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.375, 0.625, 0.3125, 0.625, 0.6875, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.375, 0.625, 0.6875, 0.625, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.3125, 0.375, 0.375, 0.6875, 0.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.3125, 0.3125, 0.625, 0.375, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.375, 0.3125, 0.6875, 0.625, 0.375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.625, 0.3125, 0.375, 0.6875, 0.6875, 0.625), BooleanOp.OR);

        return shape;
    }

    public static VoxelShape makeNorthConnectorShape(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.375, 0, 0.375, 0.625, 0.3125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.625, 0, 0.625, 0.6875, 0.3125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.625, 0.375, 0, 0.6875, 0.625, 0.3125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.3125, 0, 0.625, 0.375, 0.3125), BooleanOp.OR);

        return shape;
    }

    public static VoxelShape makeEastConnectorShape(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.6875, 0.375, 0.3125, 1, 0.625, 0.375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.6875, 0.625, 0.375, 1, 0.6875, 0.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.6875, 0.375, 0.625, 1, 0.625, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.6875, 0.3125, 0.375, 1, 0.375, 0.625), BooleanOp.OR);

        return shape;
    }

    public static VoxelShape makeSouthConnectorShape(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.625, 0.375, 0.6875, 0.6875, 0.625, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.625, 0.6875, 0.625, 0.6875, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.375, 0.6875, 0.375, 0.625, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.3125, 0.6875, 0.625, 0.375, 1), BooleanOp.OR);

        return shape;
    }

    public static VoxelShape makeWestConnectorShape(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0.375, 0.625, 0.3125, 0.625, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.625, 0.375, 0.3125, 0.6875, 0.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.375, 0.3125, 0.3125, 0.625, 0.375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.3125, 0.375, 0.3125, 0.375, 0.625), BooleanOp.OR);

        return shape;
    }

    public static VoxelShape makeUpConnectorShape(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.6875, 0.375, 0.375, 1, 0.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.6875, 0.625, 0.625, 1, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.625, 0.6875, 0.375, 0.6875, 1, 0.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.6875, 0.3125, 0.625, 1, 0.375), BooleanOp.OR);

        return shape;
    }

    public static VoxelShape makeDownConnectorShape(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.625, 0, 0.375, 0.6875, 0.3125, 0.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0, 0.625, 0.625, 0.3125, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0, 0.375, 0.375, 0.3125, 0.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0, 0.3125, 0.625, 0.3125, 0.375), BooleanOp.OR);

        return shape;
    }

    public static VoxelShape makeNorthDeviceShape(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.5625, 0.0625, 0.375, 0.625, 0.3125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.375, 0.0625, 0.375, 0.4375, 0.3125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.625, 0.375, 0.0625, 0.6875, 0.4375, 0.3125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.625, 0.5625, 0.0625, 0.6875, 0.625, 0.3125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5625, 0.625, 0.0625, 0.625, 0.6875, 0.3125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.625, 0.0625, 0.4375, 0.6875, 0.3125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5625, 0.3125, 0.0625, 0.625, 0.375, 0.3125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.3125, 0.0625, 0.4375, 0.375, 0.3125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.25, 0.25, 0, 0.75, 0.375, 0.0625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.25, 0.625, 0, 0.75, 0.75, 0.0625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.25, 0.375, 0, 0.375, 0.625, 0.0625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.625, 0.375, 0, 0.75, 0.625, 0.0625), BooleanOp.OR);

        return shape;
    }

    public static VoxelShape makeEastDeviceShape(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.6875, 0.5625, 0.3125, 0.9375, 0.625, 0.375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.6875, 0.375, 0.3125, 0.9375, 0.4375, 0.375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.6875, 0.375, 0.625, 0.9375, 0.4375, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.6875, 0.5625, 0.625, 0.9375, 0.625, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.6875, 0.625, 0.5625, 0.9375, 0.6875, 0.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.6875, 0.625, 0.375, 0.9375, 0.6875, 0.4375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.6875, 0.3125, 0.5625, 0.9375, 0.375, 0.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.6875, 0.3125, 0.375, 0.9375, 0.375, 0.4375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.9375, 0.25, 0.25, 1, 0.375, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.9375, 0.625, 0.25, 1, 0.75, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.9375, 0.375, 0.25, 1, 0.625, 0.375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.9375, 0.375, 0.625, 1, 0.625, 0.75), BooleanOp.OR);

        return shape;
    }

    public static VoxelShape makeSouthDeviceShape(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.625, 0.5625, 0.6875, 0.6875, 0.625, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.625, 0.375, 0.6875, 0.6875, 0.4375, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.375, 0.6875, 0.375, 0.4375, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.5625, 0.6875, 0.375, 0.625, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.625, 0.6875, 0.4375, 0.6875, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5625, 0.625, 0.6875, 0.625, 0.6875, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.3125, 0.6875, 0.4375, 0.375, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5625, 0.3125, 0.6875, 0.625, 0.375, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.25, 0.25, 0.9375, 0.75, 0.375, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.25, 0.625, 0.9375, 0.75, 0.75, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.625, 0.375, 0.9375, 0.75, 0.625, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.25, 0.375, 0.9375, 0.375, 0.625, 1), BooleanOp.OR);

        return shape;
    }

    public static VoxelShape makeWestDeviceShape(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.5625, 0.625, 0.3125, 0.625, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.375, 0.625, 0.3125, 0.4375, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.375, 0.3125, 0.3125, 0.4375, 0.375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.5625, 0.3125, 0.3125, 0.625, 0.375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.625, 0.375, 0.3125, 0.6875, 0.4375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.625, 0.5625, 0.3125, 0.6875, 0.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.3125, 0.375, 0.3125, 0.375, 0.4375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.3125, 0.5625, 0.3125, 0.375, 0.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.25, 0.25, 0.0625, 0.375, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.625, 0.25, 0.0625, 0.75, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.375, 0.625, 0.0625, 0.625, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.375, 0.25, 0.0625, 0.625, 0.375), BooleanOp.OR);

        return shape;
    }

    public static VoxelShape makeUpDeviceShape(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.5625, 0.6875, 0.625, 0.625, 0.9375, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.6875, 0.625, 0.4375, 0.9375, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.6875, 0.3125, 0.4375, 0.9375, 0.375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5625, 0.6875, 0.3125, 0.625, 0.9375, 0.375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.625, 0.6875, 0.375, 0.6875, 0.9375, 0.4375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.625, 0.6875, 0.5625, 0.6875, 0.9375, 0.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.6875, 0.375, 0.375, 0.9375, 0.4375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.6875, 0.5625, 0.375, 0.9375, 0.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.25, 0.9375, 0.25, 0.375, 1, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.625, 0.9375, 0.25, 0.75, 1, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.9375, 0.625, 0.625, 1, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.9375, 0.25, 0.625, 1, 0.375), BooleanOp.OR);

        return shape;
    }

    public static VoxelShape makeDownDeviceShape(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.375, 0.0625, 0.625, 0.4375, 0.3125, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5625, 0.0625, 0.625, 0.625, 0.3125, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5625, 0.0625, 0.3125, 0.625, 0.3125, 0.375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.0625, 0.3125, 0.4375, 0.3125, 0.375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.0625, 0.375, 0.375, 0.3125, 0.4375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.0625, 0.5625, 0.375, 0.3125, 0.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.625, 0.0625, 0.375, 0.6875, 0.3125, 0.4375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.625, 0.0625, 0.5625, 0.6875, 0.3125, 0.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.625, 0, 0.25, 0.75, 0.0625, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.25, 0, 0.25, 0.375, 0.0625, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0, 0.625, 0.625, 0.0625, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0, 0.25, 0.625, 0.0625, 0.375), BooleanOp.OR);

        return shape;
    }

    public enum Tier {
        BASIC(1000, 50),
        IMPROVED(5000, 500),
        ADVANCED(10000, 1000);

        final int bufferSize, maxInOut;

        Tier(int bufferSize, int maxInOut){
            this.bufferSize = bufferSize;
            this.maxInOut = maxInOut;
        }

        public int getBufferSize() {
            return this.bufferSize;
        }

        public int getMaxInOut() {
            return this.maxInOut;
        }
    }


}

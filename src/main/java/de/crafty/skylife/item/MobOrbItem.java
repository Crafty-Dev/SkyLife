package de.crafty.skylife.item;

import de.crafty.skylife.registry.DataComponentTypeRegistry;
import de.crafty.skylife.registry.ItemRegistry;

import java.util.List;
import java.util.Optional;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class MobOrbItem extends Item {

    public MobOrbItem(Item.Properties properties) {
        super(properties);

    }

    @Override
    public Component getName(ItemStack stack) {
        return MobOrbItem.readEntityType(stack.getOrDefault(DataComponentTypeRegistry.SAVED_ENTITY, CustomData.EMPTY).copyTag()) == null
                ? Component.translatable("item.skylife.mob_orb").append(" (").append(Component.translatable("item.skylife.mob_orb.empty").withStyle(ChatFormatting.AQUA)).append(")")
                : Component.translatable("item.skylife.mob_orb").append(" (").append(Component.translatable("item.skylife.mob_orb.filled").withStyle(ChatFormatting.DARK_PURPLE)).append(")");
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {

        if (entity.getType().equals(EntityType.ENDER_DRAGON) || entity.getType().equals(EntityType.PLAYER))
            return InteractionResult.PASS;

        EntityType<?> savedEntity = readEntityType(stack.getOrDefault(DataComponentTypeRegistry.SAVED_ENTITY, CustomData.EMPTY).copyTag());
        if (savedEntity == null) {
            player.setItemInHand(hand, saveEntity(entity));
            if(player.level().isClientSide())
                player.playSound(SoundEvents.ENDER_EYE_DEATH, 1.0F, 2.0F);

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext ctx) {
        InteractionResult result = super.useOn(ctx);
        if(result.consumesAction())
            return result;

        BlockPos pos = ctx.getClickedPos();
        Direction direction = ctx.getClickedFace();
        Player player = ctx.getPlayer();
        Level world = ctx.getLevel();

        if (player == null)
            return InteractionResult.PASS;

        ItemStack stack = ctx.getItemInHand();
        EntityType<?> savedEntity = readEntityType(stack.getOrDefault(DataComponentTypeRegistry.SAVED_ENTITY, CustomData.EMPTY).copyTag());


        if (savedEntity == null)
            return InteractionResult.PASS;

        if (world instanceof ServerLevel serverWorld) {

            Vec3 p = ctx.getClickLocation().add(direction.step().x() * 0.5D, 0.0D, direction.step().z() * 0.5D);
            player.setItemInHand(ctx.getHand(), loadEntity(pos, stack, serverWorld, p));

        }

        if (world.isClientSide())
            player.playSound(SoundEvents.ENDER_EYE_DEATH, 1.0F, 2.0F);


        return InteractionResult.SUCCESS;
    }

    public static ItemStack saveEntity(Entity entity) {

        CompoundTag entityTag = new CompoundTag();
        entity.saveAsPassenger(entityTag);
        entity.remove(Entity.RemovalReason.DISCARDED);

        ItemStack orbStack = new ItemStack(ItemRegistry.MOB_ORB);
        orbStack.set(DataComponentTypeRegistry.SAVED_ENTITY, CustomData.of(entityTag));

        return orbStack;
    }

    public static ItemStack loadEntity(BlockPos clickedBlockPos, ItemStack stack, ServerLevel world, Vec3 pos) {

        CompoundTag itemTag = stack.getOrDefault(DataComponentTypeRegistry.SAVED_ENTITY, CustomData.EMPTY).copyTag();
        if (itemTag.isEmpty())
            return stack;

        EntityType<?> entityType = readEntityType(itemTag);
        ItemStack orbStack = new ItemStack(ItemRegistry.MOB_ORB);

        if (world.getBlockEntity(new BlockPos(clickedBlockPos)) instanceof SpawnerBlockEntity spawner) {
            spawner.getSpawner().setEntityId(entityType, world, world.getRandom(), clickedBlockPos);
            world.sendBlockUpdated(clickedBlockPos, world.getBlockState(clickedBlockPos), world.getBlockState(clickedBlockPos), 2);
            spawner.setChanged();
            return orbStack;
        }

        Entity entity = entityType.create(world, EntitySpawnReason.BUCKET);

        if (entity == null)
            return stack;

        entity.load(itemTag);
        entity.absMoveTo(pos.x(), pos.y(), pos.z());
        world.addFreshEntity(entity);

        return orbStack;
    }

    public static EntityType<? extends Entity> readEntityType(CompoundTag tag) {
        Optional<EntityType<?>> optional = EntityType.by(tag);
        return optional.orElse(null);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        CompoundTag tag = stack.getOrDefault(DataComponentTypeRegistry.SAVED_ENTITY, CustomData.EMPTY).copyTag();
        EntityType<?> savedEntity = readEntityType(tag);
        if (savedEntity == null) {
            tooltip.add(Component.translatable("item.skylife.mob_orb.empty").withStyle(ChatFormatting.GRAY));
            return;
        }

        tooltip.add(Component.translatable(savedEntity.getDescriptionId()).withStyle(ChatFormatting.DARK_PURPLE));

        if (tag.contains("Health")) {
            float health = tag.getFloat("Health");
            health = Math.round(health * 10) / 10.0F;
            tooltip.add(Component.literal(health + " Health").withStyle(ChatFormatting.RED));
        }
    }


}

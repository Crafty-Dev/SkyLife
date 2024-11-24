package de.crafty.skylife.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import de.crafty.skylife.SkyLife;
import de.crafty.skylife.registry.BlockRegistry;
import de.crafty.skylife.registry.DataComponentTypeRegistry;
import de.crafty.skylife.registry.ItemRegistry;
import de.crafty.skylife.item.MobOrbItem;
import de.crafty.skylife.util.ClassUtils;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BlockTransformationConfig extends AbstractSkyLifeConfig {

    private LinkedHashMap<Item, List<BlockTransformation>> transformations = new LinkedHashMap<>();


    protected BlockTransformationConfig() {
        super("blockTransformation");
    }

    @Override
    protected void setDefaults() {
        this.registerDefaultRecipes();
        this.encodeTransformations();
    }

    @Override
    public void load() {
        super.load();
        this.decodeTransformations();
    }

    private void decodeTransformations() {
        LinkedHashMap<Item, List<BlockTransformation>> blockTransformations = new LinkedHashMap<>();
        this.data().keySet().forEach(itemid -> {
            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(itemid));
            List<BlockTransformation> transformations = new ArrayList<>();

            this.data().getAsJsonArray(itemid).forEach(e -> {
                JsonObject transformation = e.getAsJsonObject();

                Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(transformation.get("block").getAsString()));
                BlockState result = BlockState.CODEC.decode(JsonOps.INSTANCE, transformation.get("result").getAsJsonObject()).getOrThrow().getFirst();
                List<TransformCondition> conditions = new ArrayList<>();
                transformation.getAsJsonArray("conditions").forEach(e1 -> {
                    conditions.add(TransformCondition.decodeCondition(e1.getAsJsonObject()));
                });
                SoundEvent sound = BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.tryParse(transformation.get("sound").getAsString()));
                ItemStack remainder = transformation.get("remainder").getAsJsonObject().isEmpty() ? ItemStack.EMPTY : ItemStack.CODEC.decode(JsonOps.INSTANCE, transformation.get("remainder").getAsJsonObject()).getOrThrow().getFirst();
                ItemStack representable = transformation.has("representable") ? ItemStack.CODEC.decode(JsonOps.INSTANCE, transformation.getAsJsonObject("representable")).getOrThrow().getFirst() : null;
                transformations.add(new BlockTransformation(block, result, conditions, sound, remainder, representable));
            });
            blockTransformations.put(item, transformations);
        });
        this.transformations = blockTransformations;
    }

    private void encodeTransformations() {
        this.transformations.forEach((item, blockTransformations) -> {
            JsonArray transformations = new JsonArray();
            blockTransformations.forEach(blockTransformation -> {
                JsonObject transformation = new JsonObject();
                transformation.addProperty("block", BuiltInRegistries.BLOCK.wrapAsHolder(blockTransformation.block()).getRegisteredName());
                transformation.add("result", BlockState.CODEC.encode(blockTransformation.result(), JsonOps.INSTANCE, new JsonObject()).getOrThrow());
                JsonArray conditions = new JsonArray();
                blockTransformation.conditions().forEach(condition -> {
                    conditions.add(condition.encode());
                });
                transformation.add("conditions", conditions);
                transformation.addProperty("sound", BuiltInRegistries.SOUND_EVENT.wrapAsHolder(blockTransformation.sound()).getRegisteredName());
                transformation.add("remainder", blockTransformation.remainder() == ItemStack.EMPTY ? new JsonObject() : ItemStack.CODEC.encode(blockTransformation.remainder(), JsonOps.INSTANCE, new JsonObject()).getOrThrow());
                if(blockTransformation.representable() != null)
                    transformation.add("representable", ItemStack.CODEC.encode(blockTransformation.representable(), JsonOps.INSTANCE, new JsonObject()).getOrThrow());
                transformations.add(transformation);
            });
            this.data().add(BuiltInRegistries.ITEM.wrapAsHolder(item).getRegisteredName(), transformations);
        });
    }

    public LinkedHashMap<Item, List<BlockTransformation>> getTransformations() {
        return this.transformations;
    }

    private void registerTransformation(Item item, BlockTransformation... transformations) {
        this.transformations.put(item, List.of(transformations));
    }

    private void registerDefaultRecipes() {
        this.registerTransformation(Items.BONE_MEAL, new BlockTransformation(
                Blocks.DIRT,
                Blocks.ROOTED_DIRT.defaultBlockState(),
                List.of(),
                SoundEvents.ROOTED_DIRT_PLACE,
                ItemStack.EMPTY
        ));

        this.registerTransformation(Items.WHEAT_SEEDS, new BlockTransformation(
                Blocks.DIRT,
                Blocks.GRASS_BLOCK.defaultBlockState(),
                List.of(TransformCondition.side(Direction.UP)),
                SoundEvents.GRASS_HIT,
                ItemStack.EMPTY
        ));

        this.registerTransformation(Items.CRIMSON_FUNGUS, new BlockTransformation(
                Blocks.NETHERRACK,
                Blocks.CRIMSON_NYLIUM.defaultBlockState(),
                List.of(TransformCondition.side(Direction.UP), TransformCondition.crouching(false)),
                SoundEvents.NETHERRACK_PLACE,
                ItemStack.EMPTY
        ));

        this.registerTransformation(Items.WARPED_FUNGUS, new BlockTransformation(
                Blocks.NETHERRACK,
                Blocks.WARPED_NYLIUM.defaultBlockState(),
                List.of(TransformCondition.side(Direction.UP), TransformCondition.crouching(false)),
                SoundEvents.NETHERRACK_PLACE,
                ItemStack.EMPTY
        ));

        this.registerTransformation(ItemRegistry.ROTTEN_MIXTURE, new BlockTransformation(
                Blocks.ROOTED_DIRT,
                Blocks.MOSS_BLOCK.defaultBlockState(),
                List.of(),
                SoundEvents.MOSS_STEP,
                ItemStack.EMPTY
        ));

        this.registerTransformation(Items.COAL, new BlockTransformation(
                Blocks.COBBLESTONE,
                Blocks.BLACKSTONE.defaultBlockState(),
                List.of(),
                SoundEvents.STONE_PLACE,
                ItemStack.EMPTY
        ));

        this.registerTransformation(ItemRegistry.MOB_ORB,
                new BlockTransformation(
                        Blocks.SOUL_SAND,
                        BlockRegistry.GHAST_BLOCK.defaultBlockState(),
                        List.of(TransformCondition.mobOrb(EntityType.GHAST)),
                        SoundEvents.GHAST_DEATH,
                        new ItemStack(ItemRegistry.MOB_ORB),
                        createDisplayableMobOrb(EntityType.GHAST)
                ),
                new BlockTransformation(
                        Blocks.SNOW_BLOCK,
                        BlockRegistry.PHANTOM_BLOCK.defaultBlockState(),
                        List.of(TransformCondition.mobOrb(EntityType.PHANTOM)),
                        SoundEvents.PHANTOM_AMBIENT,
                        new ItemStack(ItemRegistry.MOB_ORB),
                        createDisplayableMobOrb(EntityType.PHANTOM)
                ));
    }

    private static ItemStack createDisplayableMobOrb(EntityType<?> entityType){

        ItemStack stack = new ItemStack(ItemRegistry.MOB_ORB);
        CompoundTag tag = stack.getOrDefault(DataComponentTypeRegistry.SAVED_ENTITY, CustomData.EMPTY).copyTag();
        tag.putString("id", EntityType.getKey(entityType).toString());
        stack.set(DataComponentTypeRegistry.SAVED_ENTITY, CustomData.of(tag));
        return stack;
    }

    public record BlockTransformation(Block block, BlockState result, List<TransformCondition> conditions, SoundEvent sound, ItemStack remainder, ItemStack representable) {

        public BlockTransformation(Block block, BlockState result, List<TransformCondition> conditions, SoundEvent sound, ItemStack remainder){
            this(block, result, conditions, sound, remainder, null);
        }
    }

    enum ConditionsType {
        SIDE(SideCondition.class),
        CROUCHING(CrouchingCondition.class),
        MOB_ORB(MobOrbCondition.class);

        final Class<? extends TransformCondition> clazz;

        ConditionsType(Class<? extends TransformCondition> clazz) {
            this.clazz = clazz;
        }

        Class<? extends TransformCondition> classOf() {
            return this.clazz;
        }
    }

    public abstract static class TransformCondition {

        final ConditionsType type;

        private TransformCondition(ConditionsType type) {
            this.type = type;
        }

        JsonObject encode() {
            JsonObject encoded = new JsonObject();
            encoded.addProperty("conditionType", this.type.name());
            return encoded;
        }

        ;

        abstract void decode(JsonObject encoded);

        public abstract boolean check(Player player, InteractionHand hand, net.minecraft.world.level.Level world, BlockState state, Direction side);


        private static TransformCondition decodeCondition(JsonObject encoded) {
            ConditionsType type = ConditionsType.valueOf(encoded.get("conditionType").getAsString());
            TransformCondition condition = ClassUtils.createInstance(type.classOf());
            if (condition == null) {
                SkyLife.LOGGER.error("Failed to decode TransformCondition: {}", encoded);
                return null;
            }
            condition.decode(encoded);
            return condition;
        }

        private static TransformCondition side(Direction side) {
            return new SideCondition(side);
        }

        private static TransformCondition crouching(boolean reversed) {
            return new CrouchingCondition(reversed);
        }

        private static TransformCondition mobOrb(EntityType<?> type) {
            return new MobOrbCondition(type);
        }

    }

    public static class SideCondition extends TransformCondition {

        private Direction side;

        public SideCondition(Direction side) {
            this();
            this.side = side;
        }

        public SideCondition() {
            super(ConditionsType.SIDE);
        }

        @Override
        public JsonObject encode() {
            JsonObject encoded = super.encode();
            encoded.addProperty("side", this.side.name());
            return encoded;
        }

        @Override
        void decode(JsonObject encoded) {
            this.side = Direction.valueOf(encoded.get("side").getAsString());
        }


        @Override
        public boolean check(Player player, InteractionHand hand, net.minecraft.world.level.Level world, BlockState state, Direction side) {
            return side == this.side;
        }
    }


    public static class CrouchingCondition extends TransformCondition {

        boolean reversed;

        public CrouchingCondition(boolean reversed) {
            this();
            this.reversed = reversed;
        }

        public CrouchingCondition() {
            super(ConditionsType.CROUCHING);
        }

        @Override
        JsonObject encode() {
            JsonObject encoded = super.encode();
            encoded.addProperty("reversed", this.reversed);
            return encoded;
        }

        @Override
        void decode(JsonObject encoded) {
            this.reversed = encoded.get("reversed").getAsBoolean();
        }

        @Override
        public boolean check(Player player, InteractionHand hand, net.minecraft.world.level.Level world, BlockState state, Direction side) {
            return player.isShiftKeyDown();
        }
    }

    public static class MobOrbCondition extends TransformCondition {

        EntityType<?> type;

        public MobOrbCondition(EntityType<?> type) {
            this();
            this.type = type;
        }

        public MobOrbCondition() {
            super(ConditionsType.MOB_ORB);
        }

        @Override
        JsonObject encode() {
            JsonObject encoded = super.encode();
            encoded.addProperty("mobType", BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(this.type).getRegisteredName());
            return encoded;
        }

        @Override
        void decode(JsonObject encoded) {
            this.type = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.tryParse(encoded.get("mobType").getAsString()));
        }

        @Override
        public boolean check(Player player, InteractionHand hand, net.minecraft.world.level.Level world, BlockState state, Direction side) {
            return MobOrbItem.readEntityType(player.getItemInHand(hand).getOrDefault(DataComponentTypeRegistry.SAVED_ENTITY, CustomData.EMPTY).copyTag()) == this.type;
        }
    }

}

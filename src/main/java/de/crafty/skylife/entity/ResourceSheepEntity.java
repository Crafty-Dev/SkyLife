package de.crafty.skylife.entity;

import de.crafty.skylife.SkyLife;
import de.crafty.skylife.access.IMixinAnimalEntity;
import de.crafty.skylife.registry.ItemRegistry;
import de.crafty.skylife.item.ResourceWheatItem;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public class ResourceSheepEntity extends Animal implements Shearable {


    private static final int MAX_GRASS_TIMER = 40;
    private static final EntityDataAccessor<Boolean> SHEARED = SynchedEntityData.defineId(ResourceSheepEntity.class, EntityDataSerializers.BOOLEAN);
    private int eatGrassTimer;
    private EatBlockGoal eatGrassGoal;

    private final EntityType<? extends ResourceSheepEntity> sheepType;
    private final ResourceSheepEntity.Type resourceType;

    public ResourceSheepEntity(EntityType<? extends ResourceSheepEntity> entityType, Level world, ResourceSheepEntity.Type resourceType) {
        super(entityType, world);

        this.sheepType = entityType;
        this.resourceType = resourceType;

        if (!world.isClientSide())
            this.overrideGoals();
    }

    private void overrideGoals() {
        this.eatGrassGoal = new EatBlockGoal(this);
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0, Sheep.class));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.1, stack -> stack.is(this.resourceType.getBait()), false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1));
        this.goalSelector.addGoal(5, this.eatGrassGoal);
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(this.resourceType.getBait());
    }

    public Type getResourceType() {
        return this.resourceType;
    }

    @Nullable
    public Animal getBreedOffspring(ServerLevel serverLevel, AgeableMob other) {

        if (!(((IMixinAnimalEntity) this).skyLife$getLastFood().getItem() instanceof ResourceWheatItem resourceWheat))
            return EntityType.SHEEP.create(serverLevel, EntitySpawnReason.BREEDING);

        //Dimension checks
        if (resourceWheat.getRequiredDimension() == ResourceWheatItem.DimensionCriteria.NETHER && serverLevel.dimension() != ServerLevel.NETHER)
            return EntityType.SHEEP.create(serverLevel, EntitySpawnReason.BREEDING);

        if (resourceWheat.getRequiredDimension() == ResourceWheatItem.DimensionCriteria.END && serverLevel.dimension() != ServerLevel.END)
            return EntityType.SHEEP.create(serverLevel, EntitySpawnReason.BREEDING);

        float f = serverLevel.getRandom().nextFloat();

        if (!(other instanceof Sheep sheep) || f <= this.getResourceType().getBait().getSpawnChance(true))
            return this.sheepType.create(serverLevel, EntitySpawnReason.BREEDING);

        Sheep baby = EntityType.SHEEP.create(serverLevel, EntitySpawnReason.BREEDING);
        baby.setColor(sheep.getColor());
        return baby;

    }

    @Override
    public void finalizeSpawnChildFromBreeding(ServerLevel world, Animal other, @Nullable AgeableMob baby) {
        Optional.ofNullable(this.getLoveCause()).or(() -> Optional.ofNullable(other.getLoveCause())).ifPresent(player -> {
            player.awardStat(Stats.ANIMALS_BRED);
            CriteriaTriggers.BRED_ANIMALS.trigger(player, this, other, baby);
        });
        this.setAge((int) (6000 * this.resourceType.getStrength()));
        other.setAge((int) (6000 * this.resourceType.getStrength()));
        this.resetLove();
        other.resetLove();
        world.broadcastEntityEvent(this, EntityEvent.IN_LOVE_HEARTS);
        if (world.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            world.addFreshEntity(new ExperienceOrb(world, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
        }
    }


    @Override
    public void setBaby(boolean baby) {
        this.setAge(baby ? (int) (BABY_START_AGE * this.resourceType.getStrength()) : 0);
    }

    @Override
    public void ate() {
        super.ate();
        this.setSheared(false);
        if (this.isBaby()) {
            this.ageUp(60);
        }
    }

    @Override
    public boolean canMate(Animal other) {
        if (other == this)
            return false;

        if (other.getClass() != this.getClass() && !(other instanceof Sheep sheepEntity && ((IMixinAnimalEntity) sheepEntity).skyLife$getLastFood().getItem() == this.getResourceType().getBait())) {
            return false;
        }
        return this.isInLove() && other.isInLove() && !(other instanceof ResourceSheepEntity resourceSheep && resourceSheep.getResourceType() != this.getResourceType());
    }

    @Override
    protected void customServerAiStep(ServerLevel serverLevel) {
        this.eatGrassTimer = this.eatGrassGoal.getEatAnimationTick();
        super.customServerAiStep(serverLevel);
    }


    @Override
    public void aiStep() {
        if (this.level().isClientSide) {
            this.eatGrassTimer = Math.max(0, this.eatGrassTimer - 1);
        }

        super.aiStep();
    }

    @Override
    public void handleEntityEvent(byte status) {
        if (status == EntityEvent.EAT_GRASS) {
            this.eatGrassTimer = 40;
        } else {
            super.handleEntityEvent(status);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SHEARED, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("Sheared", this.isSheared());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.setSheared(nbt.getBoolean("Sheared"));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SHEEP_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SHEEP_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SHEEP_DEATH;
    }

    public float getHeadEatPositionScale(float f) {
        if (this.eatGrassTimer <= 0) {
            return 0.0F;
        } else if (this.eatGrassTimer >= 4 && this.eatGrassTimer <= 36) {
            return 1.0F;
        } else {
            return this.eatGrassTimer < 4 ? ((float)this.eatGrassTimer - f) / 4.0F : -((float)(this.eatGrassTimer - 40) - f) / 4.0F;
        }
    }

    public float getHeadEatAngleScale(float f) {
        if (this.eatGrassTimer > 4 && this.eatGrassTimer <= 36) {
            float g = ((float)(this.eatGrassTimer - 4) - f) / 32.0F;
            return (float) (Math.PI / 5) + 0.21991149F * Mth.sin(g * 28.7F);
        } else {
            return this.eatGrassTimer > 0 ? (float) (Math.PI / 5) : this.getXRot() * (float) (Math.PI / 180.0);
        }
    }


    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.SHEEP_STEP, 0.15F, 1.0F);
    }

    public void setSheared(boolean sheared) {
        this.entityData.set(SHEARED, sheared);
    }

    public boolean isSheared() {
        return this.entityData.get(SHEARED);
    }

    @Override
    public boolean readyForShearing() {
        return this.isAlive() && !this.isSheared() && !this.isBaby();
    }

    @Override
    public void shear(ServerLevel serverLevel, SoundSource soundSource, ItemStack itemStack) {
        this.level().playSound(null, this, SoundEvents.SHEEP_SHEAR, soundSource, 1.0F, 1.0F);
        this.level().playSound(null, this, this.resourceType.getSound(), soundSource, 0.5F, 1.0F);

        this.resourceSheared(serverLevel, EnchantmentHelper.getItemEnchantmentLevel(this.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE), itemStack));
    }

    public static AttributeSupplier.Builder createSheepAttributes(Type type) {
        return Animal.createAnimalAttributes().add(Attributes.MAX_HEALTH, 8.0 * type.getStrength()).add(Attributes.MOVEMENT_SPEED, 0.23F);
    }


    private void resourceSheared(ServerLevel serverLevel, int fortune) {
        this.setSheared(true);
        this.spawnAtLocation(serverLevel, new ItemStack(this.getResourceType().getResource(), this.getResourceType().isFortuneEffective() ? 1 + serverLevel.getRandom().nextInt(1 + fortune) : 1));
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.is(Items.SHEARS)) {
            if (!this.level().isClientSide && this.readyForShearing()) {
                this.shear((ServerLevel) this.level(), SoundSource.PLAYERS, player.getItemInHand(hand));
                this.gameEvent(GameEvent.SHEAR, player);
                itemStack.hurtAndBreak(1, player, getSlotForHand(hand));
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.CONSUME;
            }
        } else {
            return super.mobInteract(player, hand);
        }
    }


    public enum Type {

        COAL(() -> ItemRegistry.COAL_ORE_DUST, () -> ItemRegistry.COAL_ENRICHED_WHEAT, 1.0F, "ore_sheep", SoundEvents.STONE_BREAK, true),
        IRON(() -> ItemRegistry.IRON_ORE_DUST, () -> ItemRegistry.IRON_ENRICHED_WHEAT, 1.0F, "ore_sheep", SoundEvents.STONE_BREAK, true),
        COPPER(() -> ItemRegistry.COPPER_ORE_DUST, () -> ItemRegistry.COPPER_ENRICHED_WHEAT, 1.0F, "ore_sheep", SoundEvents.STONE_BREAK, true),
        GOLD(() -> ItemRegistry.GOLD_ORE_DUST, () -> ItemRegistry.GOLD_ENRICHED_WHEAT, 1.75F, "ore_sheep", SoundEvents.STONE_BREAK, true),
        LAPIS(() -> ItemRegistry.LAPIS_ORE_DUST, () -> ItemRegistry.LAPIS_ENRICHED_WHEAT, 1.75F, "ore_sheep", SoundEvents.STONE_BREAK, true),
        REDSTONE(() -> ItemRegistry.REDSTONE_ORE_DUST, () -> ItemRegistry.REDSTONE_ENRICHED_WHEAT, 1.75F, "ore_sheep", SoundEvents.STONE_BREAK, true),
        DIAMOND(() -> ItemRegistry.DIAMOND_ORE_DUST, () -> ItemRegistry.DIAMOND_ENRICHED_WHEAT, 2.5F, "ore_sheep", SoundEvents.STONE_BREAK, true),
        EMERALD(() -> ItemRegistry.EMERALD_ORE_DUST, () -> ItemRegistry.EMERALD_ENRICHED_WHEAT, 2.5F, "ore_sheep", SoundEvents.STONE_BREAK, true),
        QUARTZ(() -> ItemRegistry.QUARTZ_ORE_DUST, () -> ItemRegistry.QUARTZ_ENRICHED_WHEAT, 1.5F, "nether_ore_sheep", SoundEvents.NETHERRACK_BREAK, true),
        NETHERITE(() -> ItemRegistry.NETHERITE_ORE_DUST, () -> ItemRegistry.NETHERITE_ENRICHED_WHEAT, 5.0F, "nether_ore_sheep", SoundEvents.ANCIENT_DEBRIS_BREAK, true),
        GLOWSTONE(() -> ItemRegistry.GLOWSTONE_ORE_DUST, () -> ItemRegistry.GLOWSTONE_ENRICHED_WHEAT, 1.25F, "nether_ore_sheep", SoundEvents.GLASS_BREAK, true),
        NETHERRACK(() -> Items.NETHERRACK, () -> ItemRegistry.NETHERRACK_ENRICHED_WHEAT, 1.0F, "nether_ore_sheep", SoundEvents.NETHERRACK_BREAK, false),
        COBBLESTONE(() -> Items.COBBLESTONE, () -> ItemRegistry.COBBLESTONE_ENRICHED_WHEAT, 1.0F, "ore_sheep", SoundEvents.STONE_BREAK, false),
        DIRT(() -> Items.DIRT, () -> ItemRegistry.DIRT_ENRICHED_WHEAT, 1.0F, "overworld_sheep", SoundEvents.GRASS_BREAK, false),
        OIL(() -> ItemRegistry.HARDENED_OIL_FRAGMENT, () -> ItemRegistry.OIL_ENRICHED_WHEAT, 4.0F, "ore_sheep", SoundEvents.STONE_BREAK, true);


        private final Supplier<Item> resource;
        private final Supplier<ResourceWheatItem> bait;
        private final float strength;
        private final ResourceLocation texture;
        private final ResourceLocation fur_texture;
        private final SoundEvent sound;
        private final boolean isFortuneEffective;

        Type(Supplier<Item> resource, Supplier<ResourceWheatItem> bait, float strength, String sheepTexture, SoundEvent sound, boolean isFortuneEffective) {
            this.resource = resource;
            this.bait = bait;
            this.strength = strength;

            this.texture = ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "textures/entity/resourcesheep/" + sheepTexture + ".png");
            this.fur_texture = ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "textures/entity/resourcesheep/fur/" + this.name().toLowerCase() + ".png");

            this.sound = sound;
            this.isFortuneEffective = isFortuneEffective;
        }

        public Item getResource() {
            return this.resource.get();
        }

        public ResourceWheatItem getBait() {
            return this.bait.get();
        }

        public float getStrength() {
            return this.strength;
        }

        public ResourceLocation getTexture() {
            return this.texture;
        }

        public ResourceLocation getFurTexture() {
            return this.fur_texture;
        }

        public SoundEvent getSound() {
            return this.sound;
        }

        public boolean isFortuneEffective() {
            return this.isFortuneEffective;
        }
    }

}

package de.crafty.skylife.mixin.world.entity.animal;

import de.crafty.skylife.access.IMixinAnimalEntity;
import de.crafty.skylife.entity.ResourceSheepEntity;
import de.crafty.skylife.registry.TagRegistry;
import de.crafty.skylife.item.ResourceWheatItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(Sheep.class)
public abstract class MixinSheep extends Animal implements Shearable {


    @Shadow public abstract DyeColor getColor();

    @Shadow protected abstract DyeColor getOffspringColor(Animal animal, Animal animal2);

    protected MixinSheep(EntityType<? extends Animal> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "registerGoals", at = @At("RETURN"))
    private void addMateGoal(CallbackInfo ci) {
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D, ResourceSheepEntity.class));
    }

    @Redirect(method = "registerGoals", at = @At(value = "NEW", target = "(Lnet/minecraft/world/entity/PathfinderMob;DLjava/util/function/Predicate;Z)Lnet/minecraft/world/entity/ai/goal/TemptGoal;"))
    private TemptGoal injectResourceWheat(PathfinderMob entity, double speed, Predicate<Item> foodPredicate, boolean canBeScared) {
        return new TemptGoal(this, 1.1D, stack -> stack.is(ItemTags.SHEEP_FOOD) || stack.getItem() instanceof ResourceWheatItem, false);
    }

    @Redirect(method = "isFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/tags/TagKey;)Z"))
    private boolean injectResourceWheat(ItemStack instance, TagKey<Item> tag) {
        return instance.is(tag) || instance.is(TagRegistry.RESSOURCE_WHEAT);
    }

    @Inject(method = "getBreedOffspring(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/AgeableMob;)Lnet/minecraft/world/entity/animal/Sheep;", at = @At("HEAD"), cancellable = true)
    private void injectResourceSheeps(ServerLevel world, AgeableMob other, CallbackInfoReturnable<AgeableMob> cir){

        if(other instanceof ResourceSheepEntity resourceSheep && world.getRandom().nextFloat() <= resourceSheep.getResourceType().getBait().getSpawnChance(true)){
            cir.setReturnValue(resourceSheep.getResourceType().getBait().getSheepType().create(world));
            return;
        }
        if(other instanceof Sheep sheepEntity && ((IMixinAnimalEntity) sheepEntity).skyLife$getLastFood().getItem() instanceof ResourceWheatItem bait && world.getRandom().nextFloat() <= bait.getSpawnChance()){
            cir.setReturnValue(bait.getSheepType().create(world));
            return;
        }

        Sheep baby = EntityType.SHEEP.create(world);
        if(other instanceof Sheep sheep && baby != null)
            baby.setColor(this.getOffspringColor(this, sheep));

        if(!(other instanceof Sheep && baby != null))
            baby.setColor(this.getColor());

        cir.setReturnValue(baby);
    }
}

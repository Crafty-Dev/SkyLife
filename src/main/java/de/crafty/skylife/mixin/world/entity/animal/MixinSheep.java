package de.crafty.skylife.mixin.world.entity.animal;

import de.crafty.skylife.access.IMixinAnimalEntity;
import de.crafty.skylife.entity.ResourceSheepEntity;
import de.crafty.skylife.entity.ai.goals.BreedGoal;
import de.crafty.skylife.registry.TagRegistry;
import de.crafty.skylife.item.ResourceWheatItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(Sheep.class)
public abstract class MixinSheep extends Animal implements Shearable {


    @Shadow
    public abstract DyeColor getColor();

    protected MixinSheep(EntityType<? extends Animal> entityType, Level world) {
        super(entityType, world);
    }

    @Redirect(method = "registerGoals", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V", ordinal = 2))
    private void modifySheep(GoalSelector goalSelector, int id, Goal goal) {
        goalSelector.addGoal(2, new BreedGoal(this, 1.0D, Sheep.class, ResourceSheepEntity.class));
    }

    @Redirect(method = "registerGoals", at = @At(value = "NEW", target = "(Lnet/minecraft/world/entity/PathfinderMob;DLjava/util/function/Predicate;Z)Lnet/minecraft/world/entity/ai/goal/TemptGoal;"))
    private TemptGoal injectResourceWheat(PathfinderMob entity, double speed, Predicate<Item> foodPredicate, boolean canBeScared) {
        return new TemptGoal(this, 1.1D, stack -> stack.is(ItemTags.SHEEP_FOOD) || stack.is(TagRegistry.RESSOURCE_WHEAT), false);
    }

    @Redirect(method = "isFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/tags/TagKey;)Z"))
    private boolean injectResourceWheat(ItemStack instance, TagKey<Item> tag) {
        return instance.is(tag) || instance.is(TagRegistry.RESSOURCE_WHEAT);
    }
}

package de.crafty.skylife.mixin.world.entity.animal;


import de.crafty.skylife.SkyLife;
import de.crafty.skylife.entity.ResourceSheepEntity;
import de.crafty.skylife.item.ResourceWheatItem;
import de.crafty.skylife.registry.TagRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import de.crafty.skylife.access.IMixinAnimalEntity;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Animal.class)
public abstract class MixinAnimal extends AgeableMob implements IMixinAnimalEntity {

    @Unique
    private ItemStack lastFood = ItemStack.EMPTY;

    @Shadow
    public abstract boolean isInLove();

    @Shadow protected abstract void usePlayerItem(Player player, InteractionHand interactionHand, ItemStack itemStack);

    protected MixinAnimal(EntityType<? extends AgeableMob> entityType, Level world) {
        super(entityType, world);
    }


    @Inject(method = "canMate", at = @At("HEAD"), cancellable = true)
    private void injectResourceSheeps(Animal other, CallbackInfoReturnable<Boolean> cir) {

        if (!((Object) this instanceof Sheep))
            return;

        if (other == (Object) this)
            cir.setReturnValue(false);
        if (other.getClass() != ((Object) this).getClass() && !(other instanceof ResourceSheepEntity resourceSheep && this.skyLife$getLastFood().getItem() == resourceSheep.getResourceType().getBait()))
            cir.setReturnValue(false);

        cir.setReturnValue(this.isInLove() && other.isInLove() && !(other instanceof Sheep sheepEntity && ((IMixinAnimalEntity) sheepEntity).skyLife$getLastFood().getItem() != this.skyLife$getLastFood().getItem()));
    }

    @Redirect(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Animal;usePlayerItem(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;)V", ordinal = 0))
    private void rememberLastFood(Animal instance, Player player, InteractionHand interactionHand, ItemStack itemStack){
        ItemStack saved = itemStack.copy();
        saved.setCount(1);
        this.lastFood = saved;
        this.usePlayerItem(player, interactionHand, itemStack);
    }

    @Override
    public ItemStack skyLife$getLastFood() {
        return this.lastFood;
    }


    @Redirect(method = "spawnChildFromBreeding", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Animal;getBreedOffspring(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/AgeableMob;)Lnet/minecraft/world/entity/AgeableMob;"))
    private AgeableMob injectResourceSheeps(Animal instance, ServerLevel serverLevel, AgeableMob other) {
        if (!(instance instanceof Sheep thisSheep))
            return instance.getBreedOffspring(serverLevel, other);


        if (!(((IMixinAnimalEntity) thisSheep).skyLife$getLastFood().getItem() instanceof ResourceWheatItem resourceWheat))
            return instance.getBreedOffspring(serverLevel, other instanceof ResourceSheepEntity ? thisSheep : other);

        if (resourceWheat.getRequiredDimension() == ResourceWheatItem.DimensionCriteria.NETHER && serverLevel.dimension() != ServerLevel.NETHER)
            return instance.getBreedOffspring(serverLevel, other instanceof ResourceSheepEntity ? thisSheep : other);

        if (resourceWheat.getRequiredDimension() == ResourceWheatItem.DimensionCriteria.END && serverLevel.dimension() != ServerLevel.END)
            return instance.getBreedOffspring(serverLevel, other instanceof ResourceSheepEntity ? thisSheep : other);


        float f = serverLevel.getRandom().nextFloat();

        if (other instanceof ResourceSheepEntity && f <= resourceWheat.getSpawnChance(true))
            return resourceWheat.getSheepType().create(serverLevel, EntitySpawnReason.BREEDING);

        if (other instanceof Sheep && f <= resourceWheat.getSpawnChance())
            return resourceWheat.getSheepType().create(serverLevel, EntitySpawnReason.BREEDING);


        return instance.getBreedOffspring(serverLevel, other instanceof ResourceSheepEntity ? thisSheep : other);
    }


}

package de.crafty.skylife.mixin.world.entity.animal;


import de.crafty.skylife.entity.ResourceSheepEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import de.crafty.skylife.access.IMixinAnimalEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Animal.class)
public abstract class MixinAnimal extends AgeableMob implements IMixinAnimalEntity {

    @Unique
    private ItemStack lastFood = ItemStack.EMPTY;

    @Shadow
    public abstract boolean isInLove();

    protected MixinAnimal(EntityType<? extends AgeableMob> entityType, Level world) {
        super(entityType, world);
    }


    @Inject(method = "canMate", at = @At("HEAD"), cancellable = true)
    private void injectResourceSheeps(Animal other, CallbackInfoReturnable<Boolean> cir) {

        if(!((Object)this instanceof Sheep))
            return;

        if (other == (Object) this)
            cir.setReturnValue(false);
        if (other.getClass() != ((Object) this).getClass() && !(other instanceof ResourceSheepEntity resourceSheep && this.skyLife$getLastFood().getItem() == resourceSheep.getResourceType().getBait()))
            cir.setReturnValue(false);

        cir.setReturnValue(this.isInLove() && other.isInLove() && !(other instanceof Sheep sheepEntity && ((IMixinAnimalEntity)sheepEntity).skyLife$getLastFood().getItem() != this.skyLife$getLastFood().getItem()));
    }


    @Inject(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Animal;setInLove(Lnet/minecraft/world/entity/player/Player;)V", shift = At.Shift.AFTER))
    private void remeberLastFood(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir){
        ItemStack stack = player.getItemInHand(hand).copy();
        stack.setCount(1);
        this.lastFood = stack;
    }

    @Override
    public ItemStack skyLife$getLastFood() {
        return this.lastFood;
    }


}

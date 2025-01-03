package de.crafty.skylife.entity.ai.goals;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

public class BreedGoal extends Goal {

    private static final TargetingConditions PARTNER_TARGETING = TargetingConditions.forNonCombat().range(8.0D).ignoreLineOfSight();
    protected final Animal animal;
    private final List<Class<? extends Animal>> partnerClasses;
    protected final ServerLevel level;
    @Nullable
    protected Animal partner;
    private int loveTime;
    private final double speedModifier;

    public BreedGoal(Animal p_25122_, double p_25123_) {
        this(p_25122_, p_25123_, p_25122_.getClass());
    }

    public BreedGoal(Animal animal, double speedModifier, Class<? extends Animal> partnerClass) {
        this.animal = animal;
        this.level = getServerLevel(animal);
        this.partnerClasses = List.of(partnerClass);
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @SafeVarargs
    public BreedGoal(Animal animal, double speedModifier, Class<? extends Animal>... partnerClasses) {
        this.animal = animal;
        this.level = getServerLevel(animal);
        this.partnerClasses = List.of(partnerClasses);
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public boolean canUse() {
        if (!this.animal.isInLove()) {
            return false;
        } else {
            this.partner = this.getFreePartner();
            return this.partner != null;
        }
    }

    public boolean canContinueToUse() {
        return this.partner.isAlive() && this.partner.isInLove() && this.loveTime < 60 && !this.partner.isPanicking();
    }

    public void stop() {
        this.partner = null;
        this.loveTime = 0;
    }

    public void tick() {
        this.animal.getLookControl().setLookAt(this.partner, 10.0F, (float)this.animal.getMaxHeadXRot());
        this.animal.getNavigation().moveTo(this.partner, this.speedModifier);
        this.loveTime++;
        if (this.loveTime >= this.adjustedTickDelay(60) && this.animal.distanceToSqr(this.partner) < 9.0) {
            this.breed();
        }

    }

    @Nullable
    private Animal getFreePartner() {

        Animal animal = null;

        for (Class<? extends Animal> clazz : (this.partnerClasses)) {
            List<? extends Animal> list = this.level.getNearbyEntities(clazz, PARTNER_TARGETING, this.animal, this.animal.getBoundingBox().inflate(8.0D));

            double d0 = Double.MAX_VALUE;

            for (Animal animal1 : list) {
                if (this.animal.canMate(animal1) && this.animal.distanceToSqr(animal1) < d0) {
                    animal = animal1;
                    d0 = this.animal.distanceToSqr(animal1);
                }
            }
        }

        return animal;
    }

    protected void breed() {
        this.animal.spawnChildFromBreeding(this.level, this.partner);
    }

}

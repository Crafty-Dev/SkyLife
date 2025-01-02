package de.crafty.skylife.mixin.core.dispenser;

import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.core.dispenser.ShearsDispenseItemBehavior;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ShearsDispenseItemBehavior.class)
public abstract class MixinShearsDispenseItemBehavior extends OptionalDispenseItemBehavior {



}

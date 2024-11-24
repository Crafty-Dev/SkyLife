package de.crafty.skylife.registry;

import de.crafty.skylife.SkyLife;
import de.crafty.skylife.block.fluid.ObsidianFluid;
import de.crafty.skylife.block.fluid.OilFluid;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.LinkedHashMap;

public class FluidRegistry {

    private static final LinkedHashMap<ResourceLocation, Fluid> FLUIDS = new LinkedHashMap<>();

    public static final FlowingFluid MOLTEN_OBSIDIAN_FLOWING = register("flowing_molten_obsidian", new ObsidianFluid.Flowing());
    public static final FlowingFluid MOLTEN_OBSIDIAN = register("molten_obsidian", new ObsidianFluid.Source());

    public static final FlowingFluid OIL_FLOWING = register("flowing_oil", new OilFluid.Flowing());
    public static final FlowingFluid OIL = register("oil", new OilFluid.Source());


    private static <T extends Fluid> T register(String id, T fluid){
        FLUIDS.put(ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, id), fluid);
        return fluid;
    }

    public static void perform(){
        FLUIDS.forEach((resourceLocation, fluid) -> {
            Registry.register(BuiltInRegistries.FLUID, resourceLocation, fluid);
        });
    }
}

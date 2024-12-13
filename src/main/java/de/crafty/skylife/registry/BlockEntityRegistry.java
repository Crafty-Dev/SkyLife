package de.crafty.skylife.registry;

import de.crafty.skylife.SkyLife;
import de.crafty.skylife.block.MeltingBlock;
import de.crafty.skylife.block.machines.integrated.ImprovedEnergyCableBlock;
import de.crafty.skylife.blockentities.*;

import de.crafty.skylife.blockentities.fluid.BasicFluidPipeBlockEntity;
import de.crafty.skylife.blockentities.fluid.BasicFluidStorageBlockEntity;
import de.crafty.skylife.blockentities.machines.integrated.*;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BlockEntityRegistry {


    public static final BlockEntityType<LeafPressBlockEntity> LEAF_PRESS = BlockEntityType.Builder.of(LeafPressBlockEntity::new, BlockRegistry.LEAF_PRESS).build();
    public static final BlockEntityType<GraveStoneBlockEntity> GRAVE_STONE = BlockEntityType.Builder.of(GraveStoneBlockEntity::new, BlockRegistry.GRAVE_STONE).build();
    public static final BlockEntityType<EndPortalCoreBlockEntity> END_PORTAL_CORE = BlockEntityType.Builder.of(EndPortalCoreBlockEntity::new, BlockRegistry.END_PORTAL_CORE).build();
    public static final BlockEntityType<MagicalWorkbenchBlockEntity> MAGICAL_WORKBENCH = BlockEntityType.Builder.of(MagicalWorkbenchBlockEntity::new, BlockRegistry.MAGICAL_WORKBENCH).build();

    public static final BlockEntityType<MeltingBlockEntity> MELTING_BLOCK = BlockEntityType.Builder.of(MeltingBlockEntity::new, BlockRegistry.getMeltingBlockList().values().toArray(new MeltingBlock[0])).build();

    //Energy
    public static final BlockEntityType<BriquetteGeneratorBlockEntity> BRIQUETTE_GENERATOR = BlockEntityType.Builder.of(BriquetteGeneratorBlockEntity::new, BlockRegistry.BRIQUETTE_GENERATOR).build();
    public static final BlockEntityType<SolarPanelBlockEntity> SOLAR_PANEL = BlockEntityType.Builder.of(SolarPanelBlockEntity::new, BlockRegistry.SOLAR_PANEL).build();

    public static final BlockEntityType<BlockBreakerBlockEntity> BLOCK_BREAKER = BlockEntityType.Builder.of(BlockBreakerBlockEntity::new, BlockRegistry.BLOCK_BREAKER).build();
    public static final BlockEntityType<FluxFurnaceBlockEntity> FLUX_FURNACE = BlockEntityType.Builder.of(FluxFurnaceBlockEntity::new, BlockRegistry.FLUX_FURNACE).build();
    public static final BlockEntityType<BlockMelterBlockEntity> BLOCK_MELTER = BlockEntityType.Builder.of(BlockMelterBlockEntity::new, BlockRegistry.BLOCK_MELTER).build();
    public static final BlockEntityType<FluidPumpBlockEntity> FLUID_PUMP = BlockEntityType.Builder.of(FluidPumpBlockEntity::new, BlockRegistry.FLUID_PUMP).build();

    public static final BlockEntityType<LowCapacityVPStorageBlockEntity> LC_VP_STORAGE = BlockEntityType.Builder.of(LowCapacityVPStorageBlockEntity::new, BlockRegistry.LC_VP_STORAGE).build();
    public static final BlockEntityType<MediumCapacityVPStorageBlockEntity> MC_VP_STORAGE = BlockEntityType.Builder.of(MediumCapacityVPStorageBlockEntity::new, BlockRegistry.MC_VP_STORAGE).build();
    public static final BlockEntityType<HighCapacityVPStorageBlockEntity> HC_VP_STORAGE = BlockEntityType.Builder.of(HighCapacityVPStorageBlockEntity::new, BlockRegistry.HC_VP_STORAGE).build();

    public static final BlockEntityType<BasicEnergyCableBlockEntity> BASIC_ENERGY_CABLE = BlockEntityType.Builder.of(BasicEnergyCableBlockEntity::new, BlockRegistry.BASIC_ENERGY_CABLE).build();
    public static final BlockEntityType<ImprovedEnergyCableBlockEntity> IMPROVED_ENERGY_CABLE = BlockEntityType.Builder.of(ImprovedEnergyCableBlockEntity::new, BlockRegistry.IMPROVED_ENERGY_CABLE).build();
    public static final BlockEntityType<AdvancedEnergyCableBlockEntity> ADVANCED_ENERGY_CABLE = BlockEntityType.Builder.of(AdvancedEnergyCableBlockEntity::new, BlockRegistry.ADVANCED_ENERGY_CABLE).build();

    //Fluid
    public static final BlockEntityType<BasicFluidStorageBlockEntity> BASIC_FLUID_STORAGE = BlockEntityType.Builder.of(BasicFluidStorageBlockEntity::new, BlockRegistry.BASIC_FLUID_STORAGE).build();
    public static final BlockEntityType<BasicFluidPipeBlockEntity> BASIC_FLUID_PIPE = BlockEntityType.Builder.of(BasicFluidPipeBlockEntity::new, BlockRegistry.BASIC_FLUID_PIPE).build();


    public static void perform(){
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "leaf_press"), LEAF_PRESS);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "grave_stone"), GRAVE_STONE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "end_portal_core"), END_PORTAL_CORE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "magical_workbench"), MAGICAL_WORKBENCH);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "melting_block"), MELTING_BLOCK);

        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "briquette_generator"), BRIQUETTE_GENERATOR);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "solar_panel"), SOLAR_PANEL);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "block_breaker"), BLOCK_BREAKER);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "flux_furnace"), FLUX_FURNACE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "block_melter"), BLOCK_MELTER);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "fluid_pump"), FLUID_PUMP);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "lc_vp_storage"), LC_VP_STORAGE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "mc_vp_storage"), MC_VP_STORAGE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "hc_vp_storage"), HC_VP_STORAGE);

        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "basic_energy_cable"), BASIC_ENERGY_CABLE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "improved_energy_cable"), IMPROVED_ENERGY_CABLE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "advanced_energy_cable"), ADVANCED_ENERGY_CABLE);

        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "basic_fluid_storage"), BASIC_FLUID_STORAGE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, "basic_fluid_pipe"), BASIC_FLUID_PIPE);



    }
}

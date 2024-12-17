package de.crafty.skylife.registry;

import de.crafty.lifecompat.energy.block.BaseEnergyBlock;
import de.crafty.skylife.SkyLife;
import de.crafty.skylife.block.*;
import de.crafty.skylife.block.fluid.LiquidObsidianBlock;
import de.crafty.skylife.block.fluid.LiquidOilBlock;
import de.crafty.skylife.block.fluid.MoltenObsidianCauldron;
import de.crafty.skylife.block.fluid.OilCauldron;
import de.crafty.skylife.block.fluid.container.BasicFluidStorageBlock;
import de.crafty.skylife.block.fluid.pipe.BasicFluidPipeBlock;
import de.crafty.skylife.block.machines.integrated.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import java.util.Collection;
import java.util.LinkedHashMap;

public class BlockRegistry {

    private static final LinkedHashMap<ResourceLocation, Block> BLOCK_LIST = new LinkedHashMap<>();
    private static final LinkedHashMap<ResourceLocation, Item> BLOCK_ITEM_LIST = new LinkedHashMap<>();
    private static final LinkedHashMap<Block, MeltingBlock> MELTING_BLOCK_LIST = new LinkedHashMap<>();

    public static final Block LEAF_PRESS = registerDefault("leaf_press", new LeafPressBlock());

    public static final Block DRIED_LEAVES = registerDefault("dried_leaves", new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.2F).sound(SoundType.GRASS).noOcclusion().isValidSpawn(Blocks::ocelotOrParrot).isSuffocating(Blocks::never).isViewBlocking(Blocks::never).ignitedByLava().pushReaction(PushReaction.DESTROY).isRedstoneConductor(Blocks::never)));

    public static final Block GRAVE_STONE = registerBlock("grave_stone", new GraveStoneBlock());


    public static final Block GHAST_BLOCK = registerDefault("ghast_block", new Block(BlockBehaviour.Properties.of().sound(SoundType.SAND).strength(1.0F)));
    public static final Block PHANTOM_BLOCK = registerDefault("phantom_block", new Block(BlockBehaviour.Properties.of().sound(SoundType.SNOW).strength(1.0F)));

    public static final Block END_DIAMOND_ORE = registerDefault("end_diamond_ore", new Block(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(6.9F, 69.0F).lightLevel(value -> 5)));
    public static final Block END_NETHERITE_ORE = registerDefault("end_netherite_ore", new Block(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(40.0F, 1200.0F).lightLevel(value -> 5)));

    public static final Block DRAGON_INFUSED_BEDROCK = registerDefault("dragon_infused_bedrock", new DragonInfusedBedrockBlock());

    public static final Block END_PORTAL_CORE = registerDefault("end_portal_core", new EndPortalCoreBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(25.0F, 1200.0F).noOcclusion()));
    public static final Block END_PORTAL_FRAME = registerDefault("end_portal_frame", new EndPortalFrameBlock());

    public static final Block MAGICAL_WORKBENCH = registerDefault("magical_workbench", new MagicalWorkbenchBlock(BlockBehaviour.Properties.of().strength(4.0F).requiresCorrectToolForDrops().noOcclusion()));

    public static final Block MELTING_COBBLESTONE = registerMeltingBlock("melting_cobblestone", Blocks.COBBLESTONE);
    public static final Block MELTING_STONE = registerMeltingBlock("melting_stone", Blocks.STONE);
    public static final Block MELTING_OBSIDIAN = registerMeltingBlock("melting_obsidian", Blocks.OBSIDIAN);

    //Energy
    public static final BaseEnergyBlock BRIQUETTE_GENERATOR = registerDefault("briquette_generator", new BriquetteGeneratorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 4.0F).requiresCorrectToolForDrops().noOcclusion().lightLevel(state -> state.getValue(BriquetteGeneratorBlock.WORKING) ? 13 : 0)));
    public static final BaseEnergyBlock SOLAR_PANEL = registerDefault("solar_panel", new SolarPanelBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(2.5F, 3.0F).requiresCorrectToolForDrops().noOcclusion()));
    public static final BaseEnergyBlock BLOCK_BREAKER = registerDefault("block_breaker", new BlockBreakerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 4.0F).requiresCorrectToolForDrops().noOcclusion()));
    public static final BaseEnergyBlock FLUID_PUMP = registerDefault("fluid_pump", new FluidPumpBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 4.0F).requiresCorrectToolForDrops().noOcclusion()));
    public static final BaseEnergyBlock SOLID_FLUID_MERGER = registerDefault("solid_fluid_merger", new SolidFluidMergerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 4.0F).requiresCorrectToolForDrops().noOcclusion()));
    public static final BaseEnergyBlock FLUX_FURNACE = registerDefault("flux_furnace", new FluxFurnaceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(3.5F).noOcclusion().lightLevel(value -> value.getValue(FluxFurnaceBlock.ACTIVE) ? 13 : 0)));
    public static final BaseEnergyBlock BLOCK_MELTER = registerDefault("block_melter", new BlockMelterBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 4.0F).requiresCorrectToolForDrops().noOcclusion().lightLevel(state -> state.getValue(BlockMelterBlock.ENERGY) ? 10 : 0)));
    public static final BaseEnergyBlock LC_VP_STORAGE = registerDefault("lc_vp_storage", new LowCapacityVPStorageBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 4.0F).requiresCorrectToolForDrops().noOcclusion()));
    public static final BaseEnergyBlock MC_VP_STORAGE = registerDefault("mc_vp_storage", new MediumCapacityVPStorageBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 4.0F).requiresCorrectToolForDrops().noOcclusion()));
    public static final BaseEnergyBlock HC_VP_STORAGE = registerDefault("hc_vp_storage", new HighCapacityVPStorageBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 4.0F).requiresCorrectToolForDrops().noOcclusion()));

    public static final Block BASIC_ENERGY_CABLE = registerDefault("basic_energy_cable", new BasicEnergyCableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(0.5F).noOcclusion()));
    public static final Block IMPROVED_ENERGY_CABLE = registerDefault("improved_energy_cable", new ImprovedEnergyCableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(0.75F).noOcclusion()), new Item.Properties().rarity(Rarity.COMMON));
    public static final Block ADVANCED_ENERGY_CABLE = registerDefault("advanced_energy_cable", new AdvancedEnergyCableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(1.0F).noOcclusion()), new Item.Properties().rarity(Rarity.RARE));

    public static final Block OILY_STONE = registerDefault("oily_stone", new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.COAL_ORE)));

    //Liquids
    public static final Block MOLTEN_OBSIDIAN = registerBlock("molten_obsidian", new LiquidObsidianBlock(FluidRegistry.MOLTEN_OBSIDIAN, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).replaceable().noCollission().strength(1000.0F).pushReaction(PushReaction.DESTROY).noLootTable().liquid().sound(SoundType.EMPTY)));
    public static final Block MOLTEN_OBSIDIAN_CAULDRON = registerBlock("molten_obsidian_cauldron", new MoltenObsidianCauldron(BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON)));

    public static final Block OIL = registerBlock("oil", new LiquidOilBlock(FluidRegistry.OIL, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).replaceable().noCollission().strength(1.0F).pushReaction(PushReaction.DESTROY).noLootTable().liquid().sound(SoundType.EMPTY)));
    public static final Block OIL_CAULDRON = registerBlock("oil_cauldron", new OilCauldron(BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON)));

    //Fluid Blocks
    public static final Block BASIC_FLUID_STORAGE = registerDefault("basic_fluid_storage", new BasicFluidStorageBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 4.0F).requiresCorrectToolForDrops().noOcclusion()));
    public static final Block BASIC_FLUID_PIPE = registerDefault("basic_fluid_pipe", new BasicFluidPipeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(0.5F).noOcclusion()));


    private static <T extends Block> T registerDefault(String id, T block, Item.Properties itemProperties){
        registerBlock(id, block);
        registerBlockItem(id, new BlockItem(block, itemProperties));
        return block;
    }

    private static <T extends Block> T registerDefault(String id,  T block){

        registerBlock(id, block);
        registerBlockItem(id, new BlockItem(block, new Item.Properties()));

        return block;
    }

    private static <T extends Block> T registerBlock(String id, T block){
        BLOCK_LIST.put(ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, id), block);
        return block;
    }

    private static void registerBlockItem(String id, BlockItem blockItem){
        BLOCK_ITEM_LIST.put(ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, id), blockItem);
    }

    private static Block registerMeltingBlock(String id, Block blockType){
        MeltingBlock block = registerBlock(id, new MeltingBlock(blockType, BlockBehaviour.Properties.ofFullCopy(blockType)));
        MELTING_BLOCK_LIST.put(blockType, block);
        return block;
    }



    public static void perform(){

        BLOCK_LIST.forEach((identifier, block) -> Registry.register(BuiltInRegistries.BLOCK, identifier, block));
        BLOCK_ITEM_LIST.forEach((identifier, item) -> Registry.register(BuiltInRegistries.ITEM, identifier, item));
    }

    public static Collection<Block> getBlockList(){
        return BLOCK_LIST.values();
    }

    public static LinkedHashMap<Block, MeltingBlock> getMeltingBlockList() {
        return MELTING_BLOCK_LIST;
    }
}

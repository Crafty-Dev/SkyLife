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
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
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
import java.util.function.Function;

public class BlockRegistry {

    private static final LinkedHashMap<ResourceLocation, Block> BLOCK_LIST = new LinkedHashMap<>();
    private static final LinkedHashMap<ResourceLocation, Item> BLOCK_ITEM_LIST = new LinkedHashMap<>();
    private static final LinkedHashMap<Block, MeltingBlock> MELTING_BLOCK_LIST = new LinkedHashMap<>();

    public static final Block LEAF_PRESS = registerDefault("leaf_press", LeafPressBlock::new, BlockBehaviour.Properties.of().sound(SoundType.WOOD).isRedstoneConductor(Blocks::never).strength(2.0F));

    public static final Block DRIED_LEAVES = registerDefault("dried_leaves", Block::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.2F).sound(SoundType.GRASS).noOcclusion().isValidSpawn(Blocks::ocelotOrParrot).isSuffocating(Blocks::never).isViewBlocking(Blocks::never).ignitedByLava().pushReaction(PushReaction.DESTROY).isRedstoneConductor(Blocks::never));

    public static final Block GRAVE_STONE = registerBlock("grave_stone", GraveStoneBlock::new, BlockBehaviour.Properties.of().noOcclusion().noLootTable().strength(0.5F).sound(SoundType.GRAVEL));


    public static final Block GHAST_BLOCK = registerDefault("ghast_block", Block::new, BlockBehaviour.Properties.of().sound(SoundType.SAND).strength(1.0F));
    public static final Block PHANTOM_BLOCK = registerDefault("phantom_block", Block::new, BlockBehaviour.Properties.of().sound(SoundType.SNOW).strength(1.0F));

    public static final Block END_DIAMOND_ORE = registerDefault("end_diamond_ore", Block::new, BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(6.9F, 69.0F).lightLevel(value -> 5));
    public static final Block END_NETHERITE_ORE = registerDefault("end_netherite_ore", Block::new, BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(40.0F, 1200.0F).lightLevel(value -> 5));

    public static final Block DRAGON_INFUSED_BEDROCK = registerDefault("dragon_infused_bedrock", DragonInfusedBedrockBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.BEDROCK).lightLevel(value -> 10));

    public static final Block END_PORTAL_CORE = registerDefault("end_portal_core", EndPortalCoreBlock::new, BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(25.0F, 1200.0F).noOcclusion());
    public static final Block END_PORTAL_FRAME = registerDefault("end_portal_frame", EndPortalFrameBlock::new, BlockBehaviour.Properties.of().strength(20.0F, 1200.0F).requiresCorrectToolForDrops());

    public static final Block MAGICAL_WORKBENCH = registerDefault("magical_workbench", MagicalWorkbenchBlock::new, BlockBehaviour.Properties.of().strength(4.0F).requiresCorrectToolForDrops().noOcclusion());

    public static final Block MELTING_COBBLESTONE = registerMeltingBlock("melting_cobblestone", Blocks.COBBLESTONE);
    public static final Block MELTING_STONE = registerMeltingBlock("melting_stone", Blocks.STONE);
    public static final Block MELTING_OBSIDIAN = registerMeltingBlock("melting_obsidian", Blocks.OBSIDIAN);

    //Energy
    public static final BaseEnergyBlock BRIQUETTE_GENERATOR = registerDefault("briquette_generator", BriquetteGeneratorBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 4.0F).requiresCorrectToolForDrops().noOcclusion().lightLevel(state -> state.getValue(BriquetteGeneratorBlock.WORKING) ? 13 : 0));
    public static final BaseEnergyBlock SOLAR_PANEL = registerDefault("solar_panel", SolarPanelBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(2.5F, 3.0F).requiresCorrectToolForDrops().noOcclusion());
    public static final BaseEnergyBlock BLOCK_BREAKER = registerDefault("block_breaker", BlockBreakerBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 4.0F).requiresCorrectToolForDrops().noOcclusion());
    public static final BaseEnergyBlock FLUID_PUMP = registerDefault("fluid_pump", FluidPumpBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 4.0F).requiresCorrectToolForDrops().noOcclusion());
    public static final BaseEnergyBlock SOLID_FLUID_MERGER = registerDefault("solid_fluid_merger", SolidFluidMergerBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 4.0F).requiresCorrectToolForDrops().noOcclusion());
    public static final BaseEnergyBlock FLUX_FURNACE = registerDefault("flux_furnace", FluxFurnaceBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(3.5F).noOcclusion().lightLevel(value -> value.getValue(FluxFurnaceBlock.ACTIVE) ? 13 : 0));
    public static final BaseEnergyBlock BLOCK_MELTER = registerDefault("block_melter", BlockMelterBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 4.0F).requiresCorrectToolForDrops().noOcclusion().lightLevel(state -> state.getValue(BlockMelterBlock.ENERGY) ? 10 : 0));
    public static final BaseEnergyBlock OIL_PROCESSOR = registerDefault("oil_processor", OilProcessorBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(3.5F).noOcclusion());
    public static final BaseEnergyBlock LC_VP_STORAGE = registerDefault("lc_vp_storage", LowCapacityVPStorageBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 4.0F).requiresCorrectToolForDrops().noOcclusion());
    public static final BaseEnergyBlock MC_VP_STORAGE = registerDefault("mc_vp_storage", MediumCapacityVPStorageBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 4.0F).requiresCorrectToolForDrops().noOcclusion());
    public static final BaseEnergyBlock HC_VP_STORAGE = registerDefault("hc_vp_storage", HighCapacityVPStorageBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 4.0F).requiresCorrectToolForDrops().noOcclusion());

    public static final Block BASIC_ENERGY_CABLE = registerDefault("basic_energy_cable", BasicEnergyCableBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(0.5F).noOcclusion());
    public static final Block IMPROVED_ENERGY_CABLE = registerDefault("improved_energy_cable", ImprovedEnergyCableBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(0.75F).noOcclusion(), new Item.Properties().rarity(Rarity.COMMON));
    public static final Block ADVANCED_ENERGY_CABLE = registerDefault("advanced_energy_cable", AdvancedEnergyCableBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(1.0F).noOcclusion(), new Item.Properties().rarity(Rarity.RARE));

    public static final Block OILY_STONE = registerDefault("oily_stone", Block::new, BlockBehaviour.Properties.ofFullCopy(Blocks.COAL_ORE));

    //Liquids
    public static final Block MOLTEN_OBSIDIAN = registerBlock("molten_obsidian", properties ->  new LiquidObsidianBlock(FluidRegistry.MOLTEN_OBSIDIAN, properties), BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).replaceable().noCollission().strength(1000.0F).pushReaction(PushReaction.DESTROY).noLootTable().liquid().sound(SoundType.EMPTY));
    public static final Block MOLTEN_OBSIDIAN_CAULDRON = registerBlock("molten_obsidian_cauldron", MoltenObsidianCauldron::new, BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON));

    public static final Block OIL = registerBlock("oil", properties ->  new LiquidOilBlock(FluidRegistry.OIL, properties), BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).replaceable().noCollission().strength(1.0F).pushReaction(PushReaction.DESTROY).noLootTable().liquid().sound(SoundType.EMPTY));
    public static final Block OIL_CAULDRON = registerBlock("oil_cauldron", OilCauldron::new, BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON));

    //Fluid Blocks
    public static final Block BASIC_FLUID_STORAGE = registerDefault("basic_fluid_storage", BasicFluidStorageBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(3.0F, 4.0F).requiresCorrectToolForDrops().noOcclusion());
    public static final Block BASIC_FLUID_PIPE = registerDefault("basic_fluid_pipe", BasicFluidPipeBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(0.5F).noOcclusion());


    private static <T extends Block> T registerBlock(String id, Function<BlockBehaviour.Properties, T> blockFactory, BlockBehaviour.Properties properties) {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, id);
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, location);
        T block = blockFactory.apply(properties.setId(key));
        BLOCK_LIST.put(location, block);
        return Registry.register(BuiltInRegistries.BLOCK, key, block);
    }
    private static <T extends Block> T registerDefault(String id, Function<BlockBehaviour.Properties, T> blockFactory, BlockBehaviour.Properties properties) {
        return registerDefault(id, blockFactory, properties, new Item.Properties());
    }

    private static <T extends Block> T registerDefault(String id, Function<BlockBehaviour.Properties, T> blockFactory, BlockBehaviour.Properties properties, Item.Properties itemProperties) {
        T block = registerBlock(id, blockFactory, properties);
        registerBlockItem(id, properties1 -> new BlockItem(block, properties1), itemProperties);
        return block;
    }


    private static void registerBlockItem(String id, Function<Item.Properties, BlockItem> itemFactory, Item.Properties properties) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, id));
        BlockItem blockItem = itemFactory.apply(properties.setId(key));

        BLOCK_ITEM_LIST.put(ResourceLocation.fromNamespaceAndPath(SkyLife.MODID, id), blockItem);
        Registry.register(BuiltInRegistries.ITEM, key, blockItem);
    }

    private static Block registerMeltingBlock(String id, Block blockType){
        MeltingBlock block = registerBlock(id, properties ->  new MeltingBlock(blockType, properties), BlockBehaviour.Properties.ofFullCopy(blockType));
        MELTING_BLOCK_LIST.put(blockType, block);
        return block;
    }



    public static void load(){


    }

    public static Collection<Block> getBlockList(){
        return BLOCK_LIST.values();
    }

    public static LinkedHashMap<Block, MeltingBlock> getMeltingBlockList() {
        return MELTING_BLOCK_LIST;
    }
}

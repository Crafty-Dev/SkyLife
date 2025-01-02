package de.crafty.skylife.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import de.crafty.skylife.SkyLife;
import de.crafty.skylife.registry.FluidRegistry;
import de.crafty.skylife.util.ClassUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.*;

public class BlockMeltingConfig extends AbstractSkyLifeConfig {

    private LinkedHashMap<Block, MeltingRecipe> meltables = new LinkedHashMap<>();

    protected BlockMeltingConfig() {
        super("blockMelting");
    }

    public LinkedHashMap<Block, MeltingRecipe> getMeltables() {
        return this.meltables;
    }

    public Fluid getMeltingResultForBlock(Block block) {
        if(this.meltables.containsKey(block))
            return this.meltables.get(block).meltingResult();

        return Fluids.EMPTY;
    }

    public float getHeatEfficiencyForBlock(Block block, BlockState possibleHeatBlockState, BlockPos possibleHeatBlockPos, Level possibleHeatBlockWorld) {
        if (!this.meltables.containsKey(block))
            return 0.0F;

        for (HeatSource heatSource : this.meltables.get(block).heatSources()) {
            Block heatBlock = heatSource.heatBlock();

            if (heatBlock != possibleHeatBlockState.getBlock())
                continue;

            for(BlockMeltingCondition meltingCondition : heatSource.conditions()){
                if(!meltingCondition.check(possibleHeatBlockWorld, possibleHeatBlockPos, possibleHeatBlockState))
                    return 0.0F;
            }

            return heatSource.heatEfficiency();

        }

        return 0.0F;
    }

    @Override
    protected void setDefaults() {

        this.registerDefaultMeltables();
        this.encodeMeltingRecipes();
    }


    @Override
    public void load() {
        super.load();

        this.decodeMeltingRecipes();
    }

    //Representable for JEI or other Recipe View mods
    public record HeatSource(Block heatBlock, List<BlockMeltingCondition> conditions, float heatEfficiency, ItemStack representable) {

        private HeatSource(Block heatBlock, List<BlockMeltingCondition> conditions, float heatEfficiency) {
            this(heatBlock, conditions, heatEfficiency, new ItemStack(heatBlock));
        }

    }

    public record MeltingRecipe(List<HeatSource> heatSources, Fluid meltingResult) {

    }


    private void encodeMeltingRecipes() {
        this.meltables.forEach((block, recipe) -> {
            String id = BuiltInRegistries.BLOCK.wrapAsHolder(block).getRegisteredName();

            JsonObject recipeJson = new JsonObject();

            JsonArray heatSources = new JsonArray();
            recipe.heatSources().forEach(heatSource -> {
                JsonObject config = new JsonObject();
                config.addProperty("heatBlock", BuiltInRegistries.BLOCK.wrapAsHolder(heatSource.heatBlock()).getRegisteredName());

                //Condition Encoding
                JsonArray conditionConfig = new JsonArray();
                heatSource.conditions().forEach(blockMeltingCondition -> {
                    conditionConfig.add(blockMeltingCondition.encode());
                });
                config.add("conditions", conditionConfig);

                config.addProperty("heatEfficiency", heatSource.heatEfficiency());
                config.add("representable", ItemStack.CODEC.encode(heatSource.representable(), JsonOps.INSTANCE, new JsonObject()).getOrThrow());
                heatSources.add(config);
            });
            recipeJson.add("validHeatSources", heatSources);
            recipeJson.addProperty("meltingResult", BuiltInRegistries.FLUID.wrapAsHolder(recipe.meltingResult()).getRegisteredName());

            this.data().add(id, recipeJson);
        });
    }

    private void decodeMeltingRecipes() {
        LinkedHashMap<Block, MeltingRecipe> meltables = new LinkedHashMap<>();
        this.data().keySet().forEach(id -> {
            Block block = BuiltInRegistries.BLOCK.getValue(ResourceLocation.tryParse(id));

            JsonArray heatSourcesEncoded = this.data().getAsJsonObject(id).getAsJsonArray("validHeatSources");
            List<HeatSource> heatSources = new LinkedList<>();
            heatSourcesEncoded.forEach(e -> {
                JsonObject config = e.getAsJsonObject();
                Block heatBlock = BuiltInRegistries.BLOCK.getValue(ResourceLocation.tryParse(config.get("heatBlock").getAsString()));

                //Condition Decoding
                JsonArray conditionConfigs = config.getAsJsonArray("conditions");
                List<BlockMeltingCondition> conditions = new ArrayList<>();
                conditionConfigs.forEach(e1 -> {
                    JsonObject encodedCondition = e1.getAsJsonObject();
                    BlockMeltingCondition condition = BlockMeltingCondition.decodeCondition(encodedCondition);
                    if(condition != null)
                        conditions.add(condition);
                });

                float heatEfficiency = config.get("heatEfficiency").getAsFloat();
                ItemStack representable = ItemStack.CODEC.decode(JsonOps.INSTANCE, config.getAsJsonObject("representable")).getOrThrow().getFirst();
                heatSources.add(new HeatSource(heatBlock, conditions, heatEfficiency, representable));
            });
            Fluid meltingResult = BuiltInRegistries.FLUID.getValue(ResourceLocation.tryParse(this.data().getAsJsonObject(id).get("meltingResult").getAsString()));
            meltables.put(block, new MeltingRecipe(heatSources, meltingResult));
        });
        this.meltables = meltables;
    }


    private void registerDefaultMeltables() {
        this.registerMeltable(Blocks.COBBLESTONE, Fluids.LAVA, List.of(
                new HeatSource(Blocks.TORCH, BlockMeltingCondition.empty(), 0.25F),
                new HeatSource(Blocks.SOUL_TORCH, BlockMeltingCondition.empty(), 0.5F),
                new HeatSource(Blocks.FIRE, BlockMeltingCondition.empty(), 0.75F, new ItemStack(Items.FLINT_AND_STEEL)),
                new HeatSource(Blocks.CAMPFIRE, List.of(BlockMeltingCondition.state(CampfireBlock.LIT, true)), 1.0F),
                new HeatSource(Blocks.SOUL_FIRE, BlockMeltingCondition.empty(), 1.5F, new ItemStack(Items.FLINT_AND_STEEL)),
                new HeatSource(Blocks.SOUL_CAMPFIRE, List.of(BlockMeltingCondition.state(CampfireBlock.LIT, true)), 2.5F),
                new HeatSource(Blocks.LAVA, BlockMeltingCondition.empty(), 2.5F, new ItemStack(Items.LAVA_BUCKET))
        ));

        this.registerMeltable(Blocks.STONE, Fluids.LAVA, List.of(
                new HeatSource(Blocks.TORCH, BlockMeltingCondition.empty(), 0.25F),
                new HeatSource(Blocks.SOUL_TORCH, BlockMeltingCondition.empty(), 0.5F),
                new HeatSource(Blocks.FIRE, BlockMeltingCondition.empty(), 0.75F, new ItemStack(Items.FLINT_AND_STEEL)),
                new HeatSource(Blocks.CAMPFIRE, List.of(BlockMeltingCondition.state(CampfireBlock.LIT, true)), 1.0F),
                new HeatSource(Blocks.SOUL_FIRE, BlockMeltingCondition.empty(), 1.5F, new ItemStack(Items.FLINT_AND_STEEL)),
                new HeatSource(Blocks.SOUL_CAMPFIRE, List.of(BlockMeltingCondition.state(CampfireBlock.LIT, true)), 2.5F),
                new HeatSource(Blocks.LAVA, BlockMeltingCondition.empty(), 2.5F, new ItemStack(Items.LAVA_BUCKET))
        ));

        this.registerMeltable(Blocks.OBSIDIAN, FluidRegistry.MOLTEN_OBSIDIAN, List.of(
                new HeatSource(Blocks.CAMPFIRE, List.of(BlockMeltingCondition.state(CampfireBlock.LIT, true)), 0.25F),
                new HeatSource(Blocks.SOUL_CAMPFIRE, List.of(BlockMeltingCondition.state(CampfireBlock.LIT, true)), 0.5F),
                new HeatSource(Blocks.LAVA, BlockMeltingCondition.empty(), 1.0F, new ItemStack(Items.LAVA_BUCKET))
        ));
    }

    private void registerMeltable(Block meltable, Fluid meltingResult, List<HeatSource> heatSources) {
        this.meltables.put(meltable, new MeltingRecipe(heatSources, meltingResult));
    }


    public enum ConditionType {
        STATE(StateCondition.class);

        final Class<? extends BlockMeltingCondition> clazz;
        ConditionType(Class<? extends BlockMeltingCondition> clazz){
            this.clazz = clazz;
        }

        public Class<? extends BlockMeltingCondition> classOf() {
            return this.clazz;
        }
    }

    public static abstract class BlockMeltingCondition {

        protected final ConditionType type;
        BlockMeltingCondition(ConditionType conditionType) {
            this.type = conditionType;
        }

        public JsonObject encode(){
            JsonObject encoded = new JsonObject();
            encoded.addProperty("conditionType", this.type.name());
            return encoded;
        }

        public abstract void decode(JsonObject encoded);

        abstract boolean check(Level world, BlockPos pos, BlockState state);

        public static BlockMeltingCondition decodeCondition(JsonObject encoded){
            ConditionType conditionType = ConditionType.valueOf(encoded.get("conditionType").getAsString());
            BlockMeltingCondition condition = ClassUtils.createInstance(conditionType.classOf());
            if(condition == null){
                SkyLife.LOGGER.error("Failed to decode MeltingCondition: {}", encoded);
                return null;
            }

            condition.decode(encoded);
            return condition;
        }

        private static List<BlockMeltingCondition> empty(){
            return List.of();
        }

        private static <T extends Comparable<T>> BlockMeltingCondition state(Property<T> property, T value){
            return new StateCondition<>(property.value(value));
        }
    }

    public static class StateCondition<T extends Comparable<T>> extends BlockMeltingCondition {

        private String property, value;

        public StateCondition(Property.Value<T> propertyValue) {
            this();
            this.property = propertyValue.property().getName();
            this.value = String.valueOf(propertyValue.value());
        }

        public StateCondition(){
            super(ConditionType.STATE);
        }

        @Override
        public JsonObject encode() {
            JsonObject encoded = super.encode();
            encoded.addProperty(this.property, this.value);
            return encoded;
        }

        @Override
        public void decode(JsonObject encoded) {
            encoded.keySet().forEach(property -> {
                this.property = property;
                this.value = encoded.get(property).getAsString();
            });
        }

        @Override
        boolean check(Level world, BlockPos pos, BlockState state) {
            for(Property<?> property : state.getProperties()){
                if(property.getName().equals(this.property) && String.valueOf(state.getValue(property)).equals(this.value))
                    return true;
            }

            return false;
        }
    }
}

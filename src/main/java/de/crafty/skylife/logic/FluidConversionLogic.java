package de.crafty.skylife.logic;

import de.crafty.skylife.config.FluidConversionConfig;
import de.crafty.skylife.config.SkyLifeConfigs;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LavaCauldronBlock;
import net.minecraft.world.level.material.Fluid;

public class FluidConversionLogic {


    public static void onFluidConversion(Fluid fluid, ItemEntity item, Level level) {
        if(!(level instanceof ServerLevel serverWorld))
            return;

        BlockPos pos = item.blockPosition();
        ItemStack stack = item.getItem();

        if (!serverWorld.hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ())))
            return;

        List<FluidConversionConfig.FluidDrop> drops = SkyLifeConfigs.FLUID_CONVERSION.getDropsForItem(stack.getItem(), fluid);
        if (drops.isEmpty())
            return;

        serverWorld.setBlock(pos, Blocks.CAULDRON.defaultBlockState(), 3);
        serverWorld.sendParticles(ParticleTypes.LAVA, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 20, 0.0F, 0.0F, 0.0F, 1.0F);

        item.remove(Entity.RemovalReason.DISCARDED);

        if (stack.getCount() > 1) {
            stack.setCount(stack.getCount() - 1);
            ItemEntity itemEntity = new ItemEntity(serverWorld, item.getX(), item.getY(), item.getZ(), stack);
            serverWorld.addFreshEntity(itemEntity);
        }


        for (FluidConversionConfig.FluidDrop drop : drops) {

            int amount = drop.min();
            for (int i = drop.min(); i < drop.max(); i++) {
                if (serverWorld.getRandom().nextFloat() < drop.bonusChance())
                    amount++;
            }

            if (!drop.dropSeperate()) {
                serverWorld.addFreshEntity(new ItemEntity(serverWorld, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, new ItemStack(drop.output(), amount)));
                continue;
            }

            for (int i = 0; i < amount; i++) {
                serverWorld.addFreshEntity(new ItemEntity(serverWorld, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, new ItemStack(drop.output())));
            }

        }
    }

}

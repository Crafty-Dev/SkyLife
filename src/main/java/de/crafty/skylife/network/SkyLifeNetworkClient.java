package de.crafty.skylife.network;

import de.crafty.skylife.block.machines.integrated.BriquetteGeneratorBlock;
import de.crafty.skylife.network.payload.SkyLifeClientEventPayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;

@Environment(EnvType.CLIENT)
public class SkyLifeNetworkClient {

    public static void registerClientReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(SkyLifeClientEventPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                SkyLifeNetworkClient.handleEventPayload(context.client().level, payload.pos(), payload.eventType());
            });
        });
    }

    public static void handleEventPayload(Level level, BlockPos pos, SkyLifeClientEventPayload.ClientEventType type) {
        switch (type) {
            //Misc
            case SAPLING_GROWTH:
                BoneMealItem.addGrowthParticles(level, pos, 15);
                level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BONE_MEAL_USE, SoundSource.BLOCKS, 1.0F, 1.0F, true);
                break;
            case END_PORTAL_DESTROYED:
                level.addDestroyBlockEffect(pos, Blocks.GLASS.defaultBlockState());
                level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F, true);
                break;

            //Machines
            case BG_BRIQUETTE_CHANGE:
                level.playLocalSound(
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        level.getBlockState(pos).getValue(BriquetteGeneratorBlock.BRIQUETTE_TYPE) == BriquetteGeneratorBlock.BriquetteType.EMPTY ? SoundEvents.ROOTED_DIRT_BREAK : SoundEvents.ROOTED_DIRT_PLACE,
                        SoundSource.BLOCKS, 1.0F, 1.0F, true);
                break;
            case BG_WORKING_STARTED:
                level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
                level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
                break;
            case BG_WORKING_FINISHED:
                level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F, false);
                break;

            case BB_BLOCK_HIT:
                SoundType soundType = level.getBlockState(pos).getSoundType();
                Minecraft.getInstance().getSoundManager().play(new SimpleSoundInstance(
                        soundType.getHitSound(),
                        SoundSource.BLOCKS,
                        (soundType.getVolume() + 1.0F) / 8.0F,
                        soundType.getPitch() * 0.5F,
                        SoundInstance.createUnseededRandom(),
                        pos
                ));
                break;
            case BB_ITEM_BREAK:
                level.playLocalSound(pos, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F, true);
                break;

            case BB_HAMMER_BLOCK:
                level.playLocalSound(pos, level.getBlockState(pos).getSoundType().getBreakSound(), SoundSource.BLOCKS, 0.5F, 0.5F, true);
                break;

            case BM_EXSTINGUISH:
                level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.25F, 2.6F, true);
                break;

            case BM_MELTING_FINISHED:
                level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BUCKET_FILL_LAVA, SoundSource.BLOCKS, 1.0F, 1.0F, true);
                break;
            case BM_PLACE_ITEM:
                level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, 0.5F, true);
                break;

            case MACHINE_UPGRADED:
                level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.COPPER_GRATE_PLACE, SoundSource.BLOCKS, 1.0F, 0.25F, true);
                break;

            case LOOT_GEM_NORMAL:
                level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.25F, 1.5F, true);
                break;
            case LOOT_GEM_RARE:
                level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F, true);
            default:
        }
    }

}

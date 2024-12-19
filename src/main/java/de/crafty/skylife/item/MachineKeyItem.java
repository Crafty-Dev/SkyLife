package de.crafty.skylife.item;

import de.crafty.skylife.blockentities.machines.integrated.OilProcessorBlockEntity;
import de.crafty.skylife.registry.ItemRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.StringUtils;

public class MachineKeyItem extends Item {

    public MachineKeyItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {

        Player player = useOnContext.getPlayer();
        Level level = useOnContext.getLevel();
        BlockPos pos = useOnContext.getClickedPos();

        if(player == null)
            return InteractionResult.PASS;

        if ((player.isCrouching() || player.isShiftKeyDown()) && level.getBlockEntity(pos) instanceof OilProcessorBlockEntity be){
            be.setProcessingMode(be.getProcessingMode() == OilProcessorBlockEntity.Mode.BURNING ? OilProcessorBlockEntity.Mode.PROCESSING : OilProcessorBlockEntity.Mode.BURNING);
            player.displayClientMessage(Component.translatable("skylife.oil_processor.changedMode").withStyle(ChatFormatting.GRAY).append(": ").append(Component.literal(StringUtils.capitalize(be.getProcessingMode().name().toLowerCase())).withStyle(ChatFormatting.RED)), true);
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        return InteractionResult.PASS;
    }

}
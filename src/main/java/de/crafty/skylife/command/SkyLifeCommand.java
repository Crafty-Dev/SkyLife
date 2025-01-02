package de.crafty.skylife.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import de.crafty.eiv.recipe.ServerRecipeManager;
import de.crafty.skylife.SkyLife;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class SkyLifeCommand {

    private static int reloadConfigs(CommandContext<CommandSourceStack> context){
        SkyLife.getInstance().loadConfigs();
        context.getSource().sendSuccess(() -> Component.translatable("commands.skylife.reloaded"), true);
        return 1;
    }

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register(
                Commands.literal("skylife")
                        .requires(commandSourceStack -> commandSourceStack.hasPermission(3))
                        .then(Commands.literal("reloadConfigs").executes(SkyLifeCommand::reloadConfigs))
        );
    }

}

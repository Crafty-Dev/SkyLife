package de.crafty.skylife.command;

import com.mojang.brigadier.CommandDispatcher;
import de.crafty.eiv.recipe.ServerRecipeManager;
import de.crafty.skylife.SkyLife;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class SkyLifeCommand {


    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register(
                Commands.literal("skylife")
                        .requires(source -> source.hasPermission(3))
                        .then(Commands.literal("reloadConfigs").executes(context -> {
                            SkyLife.getInstance().loadConfigs();
                            ServerRecipeManager.INSTANCE.reloadAndSendModOnly();
                            return 1;
                        }))
        );
    }

}

package com.atrik.randomitems.commands;

import com.atrik.randomitems.RandomItemsMod;
import com.atrik.randomitems.game.GameManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class StopGameCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ri_stop")
                .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                .executes(StopGameCommand::execute));
    }

    private static int execute(CommandContext<CommandSourceStack> ctx) {
        RandomItemsMod.getLogger().info("Issued command stop");
        GameManager.getGameManager().stopGame(null);
        return 1;
    }
}

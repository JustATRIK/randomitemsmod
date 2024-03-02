package com.atrik.randomitems.commands;

import com.atrik.randomitems.RandomItemsMod;
import com.atrik.randomitems.game.GameManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class StopGameQuiteCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ri_stop_quite")
                .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                .executes(StopGameQuiteCommand::execute));
    }

    private static int execute(CommandContext<CommandSourceStack> ctx) {
        RandomItemsMod.getLogger().info("Issued command stop");
        GameManager.getGameManager().stopGameQuite();
        return 1;
    }
}

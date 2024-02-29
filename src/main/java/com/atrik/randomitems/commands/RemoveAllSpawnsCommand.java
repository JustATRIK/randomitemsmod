package com.atrik.randomitems.commands;

import com.atrik.randomitems.config.ModConfigHolder;
import com.atrik.randomitems.game.GameManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.io.IOException;

public class RemoveAllSpawnsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("remove_all_spawn")
                .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                .executes(RemoveAllSpawnsCommand::execute));
    }

    private static int execute(CommandContext<CommandSourceStack> ctx) {
        GameManager.getGameManager().spawns.clear();
        try {
            ModConfigHolder.getConfigByName("world_spawns").saveAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return 1;
    }
}

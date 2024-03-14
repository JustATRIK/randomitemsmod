package com.atrik.randomitems.commands;

import com.atrik.randomitems.RandomItemsMod;
import com.atrik.randomitems.game.GameManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class StopGameCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ri_stop")
                .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                .executes(StopGameCommand::execute));
    }

    private static int execute(CommandContext<CommandSourceStack> ctx) {
        try {
            GameManager.getGameManager().stopGame(null);
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.translatable("commands.ri_stop.failure"));
            RandomItemsMod.getLogger().severe("Failed to stop game!");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        ctx.getSource().sendSuccess(() -> Component.translatable("commands.ri_stop.success"), true);

        return 1;
    }
}

package com.atrik.randomitems.commands;

import com.atrik.randomitems.RandomItemsMod;
import com.atrik.randomitems.game.GameManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class StartGameCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ri_start")
                .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                .executes((ctx) -> execute(ctx,
                                GameManager.getGameManager().mainConfig.getInt("default_items_time"), true))
                .then(Commands.argument("item_delay", IntegerArgumentType.integer())
                .executes((ctx) -> execute(ctx,
                    IntegerArgumentType.getInteger(ctx, "item_delay"), true)
                )));
    }

    private static int execute(CommandContext<CommandSourceStack> ctx, int time, boolean cleanInventories) {
        try {
            GameManager.getGameManager().startGame(time, cleanInventories, ctx.getSource().getLevel());
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.translatable("commands.ri_start.failure"));
            RandomItemsMod.getLogger().severe("Failed to start game!");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        ctx.getSource().sendSuccess(() -> Component.translatable("commands.ri_start.success"), true);

        return 1;
    }
}

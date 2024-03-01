package com.atrik.randomitems.commands;

import com.atrik.randomitems.RandomItemsMod;
import com.atrik.randomitems.game.GameManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class StartGameCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ri_start")
                .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                .executes((ctx) -> execute(ctx,
                                ((Double) GameManager.getGameManager().mainConfig.getOrThrow("default_items_time")).intValue(), true))
                .then(Commands.argument("item_delay", IntegerArgumentType.integer())
                .executes((ctx) -> execute(ctx,
                    IntegerArgumentType.getInteger(ctx, "item_delay"), true)
                )));
    }

    private static int execute(CommandContext<CommandSourceStack> ctx, int time, boolean cleanInventories) {
        RandomItemsMod.getLogger().info("Issued command start");
        GameManager.getGameManager().startGame(time, cleanInventories, ctx.getSource().getLevel());
        return 1;
    }
}

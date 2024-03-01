package com.atrik.randomitems.commands;

import com.atrik.randomitems.RandomItemsMod;
import com.atrik.randomitems.game.GameManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class DecreaseItemsTimeDefaultCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ri_decrease_def_items_time")
                .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                .then(Commands.argument("item_delay", IntegerArgumentType.integer())
                .executes((ctx) -> execute(ctx,
                        IntegerArgumentType.getInteger(ctx, "item_delay"))
                )));
    }

    private static int execute(CommandContext<CommandSourceStack> ctx, int time) {
        RandomItemsMod.getLogger().info("Issued command set def items time");
        double newTime = - ((double) time) + (double) GameManager.getGameManager().mainConfig.getOrThrow("default_items_time");
        GameManager.getGameManager().mainConfig.set("default_items_time", newTime);
        return ((Double) newTime).intValue();
    }
}

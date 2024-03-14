package com.atrik.randomitems.commands;

import com.atrik.randomitems.RandomItemsMod;
import com.atrik.randomitems.game.GameManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class SetItemsTimeDefaultCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ri_set_def_items_time")
                .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                .then(Commands.argument("item_delay", IntegerArgumentType.integer())
                .executes((ctx) -> execute(ctx,
                        IntegerArgumentType.getInteger(ctx, "item_delay"))
                )));
    }

    private static int execute(CommandContext<CommandSourceStack> ctx, int time) {
        try {
            GameManager.getGameManager().mainConfig.set("default_items_time", (double) time);
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.translatable("commands.set_default_time.failure"));
            RandomItemsMod.getLogger().severe("Failed to set def time!");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        ctx.getSource().sendSuccess(() -> Component.translatable("commands.set_default_time.success", time), true);

        return time;
    }
}

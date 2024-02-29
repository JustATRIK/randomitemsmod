package com.atrik.randomitems.commands;

import com.atrik.randomitems.config.ModConfigHolder;
import com.atrik.randomitems.game.GameManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.world.item.Item;

import java.io.IOException;
import java.util.List;

public class AddBannedItemCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(Commands.literal("add_banned_item")
                .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                .then(Commands.argument("item", ItemArgument.item(context))
                .executes((ctx) -> execute(ctx,
                        ItemArgument.getItem(ctx, "item")))));
    }

    private static int execute(CommandContext<CommandSourceStack> ctx, ItemInput itemInput) {
        Item item = itemInput.getItem();
        ((List<Item>) ModConfigHolder.getConfigByName("banned_items").getData()).add(item);
        try {
            ModConfigHolder.getConfigByName("banned_items").saveAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        GameManager.getGameManager().reloadItems();
        return 1;
    }
}

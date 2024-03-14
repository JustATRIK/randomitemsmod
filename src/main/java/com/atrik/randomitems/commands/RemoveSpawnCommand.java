package com.atrik.randomitems.commands;

import com.atrik.randomitems.RandomItemsMod;
import com.atrik.randomitems.config.ModConfigHolder;
import com.atrik.randomitems.game.GameManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import java.io.IOException;

public class RemoveSpawnCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("remove_spawn")
                .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                .executes((ctx) -> execute(ctx,
                        BlockPosArgument.getBlockPos(ctx, "pos"))
                )));
    }

    private static int execute(CommandContext<CommandSourceStack> ctx, BlockPos pos) {
        try {
            GameManager.getGameManager().removePos(pos);
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.translatable("commands.remove_spawn.failure"));
            RandomItemsMod.getLogger().severe("Failed to remove spawn!");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        ctx.getSource().sendSuccess(() -> Component.translatable("commands.remove_spawn.success", pos), true);

        return 1;
    }
}

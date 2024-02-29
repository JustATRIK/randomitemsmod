package com.atrik.randomitems.commands;

import com.atrik.randomitems.game.GameManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;

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
        GameManager.getGameManager().removePos(pos);
        return 1;
    }
}

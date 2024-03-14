package com.atrik.randomitems.commands;

import com.atrik.randomitems.RandomItemsMod;
import com.atrik.randomitems.game.GameManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public class AddSpawnCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("add_spawn")
                .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                .executes((ctx) -> execute(ctx,
                        BlockPosArgument.getBlockPos(ctx, "pos")))));
    }

    private static int execute(CommandContext<CommandSourceStack> ctx, BlockPos pos) {
        try {
            GameManager.getGameManager().addPos(pos);
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.translatable("commands.add_spawn.failure"));
            RandomItemsMod.getLogger().severe("Failed to add spawn!");
            e.printStackTrace();
        }
        ctx.getSource().sendSuccess(() -> Component.translatable("commands.add_spawn.success", pos.toString()), true);

        return 1;
    }
}

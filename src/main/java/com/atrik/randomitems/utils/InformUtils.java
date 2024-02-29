package com.atrik.randomitems.utils;

import net.minecraft.core.Holder;
import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.function.Predicate;

public class InformUtils {

    public static void sendAll(Component message) {
        ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().forEach(serverPlayer -> serverPlayer.sendSystemMessage(message));
    }

    public static void sendIf(Component message, Predicate<ServerPlayer> predicate) {
        ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().forEach(serverPlayer -> {
            if (predicate.test(serverPlayer)) serverPlayer.sendSystemMessage(message);
        });
    }

    public static void titleAll(Component message, TitleType titleType) {
        ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().forEach(serverPlayer -> titleType.sendMessage(serverPlayer, message));
    }

    public static void playSoundAll(SoundEvent soundEvent, SoundSource soundSource, Position position, int vol, int pitch) {
        ClientboundSoundPacket packet = new ClientboundSoundPacket(Holder.direct(soundEvent),
                soundSource, position.x(), position.y(), position.z(),
                vol, pitch, 0);

        ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().forEach(serverPlayer -> serverPlayer.connection.send(packet));
    }

    public static void playSoundIf(SoundEvent soundEvent, SoundSource soundSource, Position position, int vol, int pitch, Predicate<ServerPlayer> predicate) {
        ClientboundSoundPacket packet = new ClientboundSoundPacket(Holder.direct(soundEvent),
                soundSource, position.x(), position.y(), position.z(),
                vol, pitch, 0);

        ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().forEach(serverPlayer -> {
            if (predicate.test(serverPlayer)) serverPlayer.connection.send(packet);
        });
    }

}

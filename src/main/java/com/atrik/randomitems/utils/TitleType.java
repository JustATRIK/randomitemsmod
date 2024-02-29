package com.atrik.randomitems.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.level.ServerPlayer;

public enum TitleType {
    TITLE,
    SUB_TITLE,
    ACTION_BAR;

    public void sendMessage(ServerPlayer player, Component message) {
        Packet<?> packet = null;
        switch (this) {
            case TITLE -> packet = new ClientboundSetTitleTextPacket(message);
            case SUB_TITLE -> packet = new ClientboundSetSubtitleTextPacket(message);
            case ACTION_BAR -> packet = new ClientboundSetActionBarTextPacket(message);
        }
        assert packet != null;
        player.connection.send(packet);
    }

}

package com.atrik.randomitems.network;

import com.atrik.randomitems.RandomItemsMod;
import com.atrik.randomitems.network.s2c_packets.CloseDeathScreenPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.SimpleChannel;

public class ModPacketHandler {
    private static final SimpleChannel INSTANCE = ChannelBuilder.named(
                    new ResourceLocation(RandomItemsMod.MODID, "main"))
            .serverAcceptedVersions((status, version) -> true)
            .clientAcceptedVersions((status, version) -> true)
            .networkProtocolVersion(1)
            .simpleChannel();

    public static void register() {
        INSTANCE.messageBuilder(CloseDeathScreenPacket.class, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(CloseDeathScreenPacket::encode)
                .decoder(CloseDeathScreenPacket::new)
                .consumerMainThread(CloseDeathScreenPacket::handle)
                .add();
    }

    public static void sendS2C(ServerPlayer player, Object packet) {
        INSTANCE.send(packet, player.connection.getConnection());
    }
}

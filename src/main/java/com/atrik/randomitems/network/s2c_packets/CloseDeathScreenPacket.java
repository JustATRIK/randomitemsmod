package com.atrik.randomitems.network.s2c_packets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class CloseDeathScreenPacket {
    public CloseDeathScreenPacket() {}
    public CloseDeathScreenPacket(FriendlyByteBuf buffer) {}

    public void encode(FriendlyByteBuf buffer) {}

    public void handle(CustomPayloadEvent.Context context) {
        Minecraft.getInstance().doRunTask(() -> {
            if (Minecraft.getInstance().screen instanceof DeathScreen) {
                Minecraft.getInstance().setScreen(null);
                context.setPacketHandled(true);
            }
        });
    }
}

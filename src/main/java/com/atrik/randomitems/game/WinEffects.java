package com.atrik.randomitems.game;

import net.minecraft.core.Holder;
import net.minecraft.core.Position;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class WinEffects implements Runnable{

    private final ServerPlayer winner;

    public WinEffects(ServerPlayer winner) {
        this.winner = winner;
    }

    @Override
    public void run() {
        Random random = new Random();
        Position playerPos = winner.position();

        ClientboundSoundPacket soundPacket = new ClientboundSoundPacket(Holder.direct(SoundEvents.PLAYER_LEVELUP),
                SoundSource.PLAYERS, playerPos.x(), playerPos.y(), playerPos.z(),
                100, 100, 0);

        for (int i = 0; i < random.nextInt(40 - 20) + 20; i++) {
            playerPos = winner.position();
            DustParticleOptions particleOptions = new DustParticleOptions(Vec3.fromRGB24(random.nextInt(99999999 - 10000000) + 10000000).toVector3f(), random.nextFloat(3));
            ClientboundLevelParticlesPacket particlePacket = new ClientboundLevelParticlesPacket(particleOptions, true, playerPos.x() + random.nextFloat(-0.9f, 0.9f), playerPos.y() + random.nextFloat(-0.9f, 1.9f), playerPos.z() + random.nextFloat(-0.9f, 0.9f),
                    random.nextFloat(-0.3f, 0.3f), random.nextFloat(-0.3f, 0.3f), random.nextFloat(-0.3f, 0.3f), 1, 10);
            ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().forEach(serverPlayer -> {
                serverPlayer.connection.send(particlePacket);
                serverPlayer.connection.send(soundPacket);
            });
            try {
                TimeUnit.MILLISECONDS.sleep(150);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void runEffect(ServerPlayer winner) {
        Thread effectThread = new Thread(new WinEffects(winner));
        effectThread.start();
    }

    public ServerPlayer getWinner() {
        return winner;
    }
}

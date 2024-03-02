package com.atrik.randomitems.game;

import com.atrik.randomitems.RandomItemsMod;
import com.atrik.randomitems.config.ModConfigHolder;
import com.atrik.randomitems.config.configs.MainConfig;
import com.atrik.randomitems.network.ModPacketHandler;
import com.atrik.randomitems.network.s2c_packets.CloseDeathScreenPacket;
import com.atrik.randomitems.utils.InformUtils;
import com.atrik.randomitems.utils.TitleType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

@Mod.EventBusSubscriber
public class GameInstance {
    private WorldBorder.Settings worldBorderSettings;

    private final int itemsTime;
    private final ServerBossEvent bossBar;
    private List<ServerPlayer> livingPlayers;

    private final MainConfig mainConfig;

    public GameInstance(int itemsTime, Level level) {
        mainConfig = (MainConfig) ModConfigHolder.getConfigByName("main_config");

        RandomItemsMod.getLogger().info("Initializing new game instance");

        if (mainConfig.getBool("enable_border")) {
            worldBorderSettings = ServerLifecycleHooks.getCurrentServer().overworld().getWorldBorder().createSettings();
            setupWorldBorders(level);
        }

        this.itemsTime = itemsTime;
        bossBar = new ServerBossEvent(Component.literal("Initializing"), BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.PROGRESS);
        bossBar.setProgress(1);
        bossBar.setVisible(true);

        livingPlayers = new ArrayList<>(ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers());
        livingPlayers.forEach(bossBar::addPlayer);

        spawnPlayers();
    }

    private void spawnPlayers() {
        List<BlockPos> freeSpawns = new ArrayList<>(GameManager.getGameManager().spawns);
        RandomItemsMod.getLogger().info("Spawning players. Spawns: " + freeSpawns);

        if (freeSpawns.isEmpty()) {
            posesIsNullWarn();
            return;
        }

        livingPlayers.forEach(serverPlayer -> {
            if (!freeSpawns.isEmpty()) {
                BlockPos spawn = freeSpawns.remove(new Random().nextInt(freeSpawns.size()));
                serverPlayer.teleportToWithTicket(spawn.getX() + 0.5, spawn.getY(), spawn.getZ() + 0.5);
                RandomItemsMod.getLogger().info("Spawned player " + serverPlayer.getDisplayName().toString() + " on " + spawn);
            } else {
                RandomItemsMod.getLogger().warning("No valid spawns found! Please setup spawns with /add_spawn");
            }

            serverPlayer.setGameMode(GameType.SURVIVAL);
        });
    }

    int ticker = 0;
    @SubscribeEvent
    public void serverTick(TickEvent.ServerTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.END)) return;
        bossBar.setProgress(1.0f - (100f / itemsTime * ++ticker) / 100f);
        bossBar.setName(Component.translatable("ri.boss_bar.name", Component.literal("§a" + ((itemsTime - ticker) / 20 + 1))));
        if (itemsTime <= ticker) {
            bossBar.setProgress(0);
            List<Item> items = GameManager.getGameManager().items;
            livingPlayers.forEach(serverPlayer -> {
                serverPlayer.addItem(new ItemStack(items.get(new Random().nextInt(items.size()))));
            });
            ticker = 0;
        }
    }

    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (!livingPlayers.contains(player)) return;

            RandomItemsMod.getLogger().info("Player just died. Left only " + livingPlayers.size() + " players");
            player.setGameMode(GameType.SPECTATOR);

            ServerLifecycleHooks.getCurrentServer().getPlayerList().respawn(player, false);
            ModPacketHandler.sendS2C(player, new CloseDeathScreenPacket());

            InformUtils.sendAll(Component.translatable("ri.message.player_died", Component.literal("§9§l" + player.getDisplayName().getString())));
            handlePlayerRemove(player);
        }
    }

    @SubscribeEvent
    public void onPlayerLogging(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.setGameMode(GameType.SPECTATOR);
            bossBar.addPlayer(player);
        }
    }

    @SubscribeEvent
    public void onPlayerLeft(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (!livingPlayers.contains(player)) return;

            handlePlayerRemove(player);
        }
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        GameManager.getGameManager().stopGame(null);
    }

    private void handlePlayerRemove(ServerPlayer player) {
        livingPlayers.remove(player);
        if (livingPlayers.size() == 1) {
            endGame(livingPlayers.get(0));
        } else if (livingPlayers.isEmpty()) {
            GameManager.getGameManager().stopGame(null);
        } else {
            InformUtils.titleAll(Component.translatable("ri.message.left_players", Component.literal("§c" + livingPlayers.size())), TitleType.ACTION_BAR);
        }
    }

    private void endGame(ServerPlayer winner) {
        RandomItemsMod.getLogger().info("Game just ended. Winner is " + winner.getDisplayName().getString());

        String winnerName = winner.getDisplayName().getString();

        RandomItemsMod.getLogger().info("Calling GameManager stop");
        GameManager.getGameManager().stopGame(winnerName);
        (GameManager.getGameManager().scoreboard = ServerLifecycleHooks.getCurrentServer().getScoreboard()).getOrCreatePlayerScore(ScoreHolder.forNameOnly(winnerName), GameManager.getGameManager().scoreboard.getObjective("wins")).add(1);

        RandomItemsMod.getLogger().info("Creating wining effects");
        WinEffects.runEffect(winner);
    }

    public void stopGame() {
        if (mainConfig.getBool("enable_border")) {
            ServerLifecycleHooks.getCurrentServer().overworld().getWorldBorder().applySettings(worldBorderSettings);
        }
        bossBar.removeAllPlayers();
        bossBar.setVisible(false);
    }

    private void setupWorldBorders(Level level) {
        List<BlockPos> poses = GameManager.getGameManager().spawns;
        if (poses.isEmpty()) {
            posesIsNullWarn();
            return;
        }
        List<Integer> xPoses = new ArrayList<>();
        List<Integer> zPoses = new ArrayList<>();

        poses.forEach(blockPos -> {
            xPoses.add(blockPos.getX());
            zPoses.add(blockPos.getZ());
        });

        WorldBorder worldBorder = level.getWorldBorder();
        worldBorder.setCenter((Collections.min(xPoses) + Collections.max(xPoses)) / 2d, (Collections.min(zPoses) + Collections.max(zPoses)) / 2d);
        worldBorder.setDamagePerBlock(1);
        worldBorder.setDamageSafeZone(0);
        worldBorder.setWarningBlocks(0);
        worldBorder.setWarningTime(0);
        worldBorder.setSize(Math.max(Math.abs(Collections.min(xPoses)) + Math.abs(Collections.max(xPoses)),
                Math.abs(Collections.min(zPoses)) + Math.abs(Collections.max(zPoses))) + mainConfig.getDouble("border_size"));
    }

    private void posesIsNullWarn() {
        Predicate<ServerPlayer> predicate = player -> ServerLifecycleHooks.getCurrentServer().getPlayerList().isOp(player.getGameProfile());
        RandomItemsMod.getLogger().warning("No valid spawns found! Please setup spawns with /add_spawn");

        InformUtils.sendIf(Component.translatable("ri.message.no_spawns"), predicate);
        InformUtils.playSoundIf(SoundEvents.ANVIL_DESTROY, SoundSource.MASTER, new Vec3(0, 0, 0), 100 ,100, predicate);
    }
}

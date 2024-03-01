package com.atrik.randomitems.game;

import com.atrik.randomitems.RandomItemsMod;
import com.atrik.randomitems.config.ModConfigHolder;
import com.atrik.randomitems.config.configs.BannedItemsConfig;
import com.atrik.randomitems.config.configs.MainConfig;
import com.atrik.randomitems.config.configs.WorldSpawnConfig;
import com.atrik.randomitems.utils.ComponentUtils;
import com.atrik.randomitems.utils.InformUtils;
import com.atrik.randomitems.utils.TitleType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GameManager {
    private static GameManager instance = null;
    public static GameManager getGameManager() {
        return instance == null ? instance = new GameManager(): instance;
    }

    public final List<Item> items = new ArrayList<>();
    public Scoreboard scoreboard;
    public final List<BlockPos> spawns;
    public final MainConfig mainConfig;

    public GameManager() {
        ModConfigHolder.addConfigEntry("banned_items", new BannedItemsConfig());
        ModConfigHolder.addConfigEntry("main_config", new MainConfig());
        mainConfig = (MainConfig) ModConfigHolder.getConfigByName("main_config");
        initMainConfig();

        reloadItems();

        scoreboard = ServerLifecycleHooks.getCurrentServer().getScoreboard();
        if (scoreboard.getObjective("wins") == null) {
            scoreboard.addObjective("wins", ObjectiveCriteria.DUMMY, Component.literal("Wins"), ObjectiveCriteria.RenderType.INTEGER, false, null);
        }
        scoreboard.setDisplayObjective(DisplaySlot.BELOW_NAME, scoreboard.getObjective("wins"));
        scoreboard.setDisplayObjective(DisplaySlot.LIST, scoreboard.getObjective("wins"));

        WorldSpawnConfig config = new WorldSpawnConfig();
        spawns = config.getData();

        ModConfigHolder.addConfigEntry("world_spawns", config);
    }

    public GameInstance gameInstance = null;
    public void startGame(int itemsTime, boolean cleanInventories, Level level) {
        RandomItemsMod.getLogger().info("Initializing game");
        new Thread(() -> {
            try {
                InformUtils.playSoundAll(SoundEvents.PLAYER_LEVELUP, SoundSource.MASTER, new Vec3(0, 0, 0), 100, 25);
                InformUtils.titleAll(Component.literal("§43"), TitleType.TITLE);
                TimeUnit.SECONDS.sleep(1);
                InformUtils.playSoundAll(SoundEvents.PLAYER_LEVELUP, SoundSource.MASTER, new Vec3(0, 0, 0), 100, 50);
                InformUtils.titleAll(Component.literal("§e2"), TitleType.TITLE);
                TimeUnit.SECONDS.sleep(1);
                InformUtils.playSoundAll(SoundEvents.PLAYER_LEVELUP, SoundSource.MASTER, new Vec3(0, 0, 0), 100, 75);
                InformUtils.titleAll(Component.literal("§21"), TitleType.TITLE);
                TimeUnit.SECONDS.sleep(1);
                InformUtils.playSoundAll(SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(2).get(), SoundSource.MASTER, new Vec3(0, 0, 0), 100, 100);
                InformUtils.titleAll(ComponentUtils.doRainbowEffect("46eab1d", Component.literal("Random Items Mod")), TitleType.TITLE);
                InformUtils.titleAll(Component.literal("§eby ATRIK and Lonors"), TitleType.SUB_TITLE);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            endStartGame(itemsTime, cleanInventories, level);
        }).start();
    }

    private void endStartGame(int itemsTime, boolean cleanInventories, Level level) {
        if (gameInstance != null) throw new RuntimeException("Game has already started!");
        if (cleanInventories) {
            RandomItemsMod.getLogger().info("Cleaning inventories");
            ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().forEach(serverPlayer -> {
                serverPlayer.getInventory().clearContent();
                serverPlayer.removeAllEffects();
                serverPlayer.setHealth(serverPlayer.getMaxHealth());
                serverPlayer.setExperiencePoints(0);
            });
            RandomItemsMod.getLogger().info("Inventories fully cleaned");
        }

        gameInstance = new GameInstance(itemsTime, level);
        MinecraftForge.EVENT_BUS.register(gameInstance);
        RandomItemsMod.getLogger().info("Game successfully started");
    }

    public void stopGame(@Nullable String winnerNick) {
        MinecraftForge.EVENT_BUS.unregister(gameInstance);
        gameInstance.stopGame();
        gameInstance = null;
        if (winnerNick != null) {
            InformUtils.sendAll(Component.translatable("ri.message.winner_is", Component.literal("§9§l" + winnerNick)));
            InformUtils.titleAll(ComponentUtils.doRainbowEffect("46eab1d", Component.literal(winnerNick)), TitleType.TITLE);
            InformUtils.titleAll(Component.translatable("ri.message.winner_is_title"), TitleType.SUB_TITLE);
        } else {
            InformUtils.sendAll(Component.translatable("ri.message.ended_by_admin"));
        }
    }

    public void addPos(BlockPos pos) {
        spawns.add(pos);
        try {
            ModConfigHolder.getConfigByName("world_spawns").saveAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void removePos(BlockPos pos) {
        spawns.remove(pos);
        try {
            ModConfigHolder.getConfigByName("world_spawns").saveAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void reloadItems() {
        items.addAll(ForgeRegistries.ITEMS.getValues());
        items.removeAll(((BannedItemsConfig) ModConfigHolder.getConfigByName("banned_items")).getData());
    }

    private void    initMainConfig() {
        mainConfig.setIfNull("enable_border", false);
        mainConfig.setIfNull("border_size", 30);
        mainConfig.setIfNull("default_items_time", 400);
    }
}

package com.atrik.randomitems;

import com.atrik.randomitems.commands.*;
import com.atrik.randomitems.config.ModConfigHolder;
import com.atrik.randomitems.game.GameInstance;
import com.atrik.randomitems.network.ModPacketHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.logging.Logger;

@Mod(RandomItemsMod.MODID)
@Mod.EventBusSubscriber
public class RandomItemsMod {
    public static final String MODID = "randomitems";
    private static Logger logger;

    public RandomItemsMod() {
        logger = Logger.getLogger(MODID);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(GameInstance.class);

        ModConfigHolder.onModSetup();

        ModPacketHandler.register();

        logger.info("Random items mod initialized");
    }

    @SubscribeEvent
    public static void onRegisterCommands(final RegisterCommandsEvent event) {
        logger.info("Registering commands from " + MODID);
        AddBannedItemCommand.register(event.getDispatcher(), event.getBuildContext());

        AddSpawnCommand.register(event.getDispatcher());
        RemoveAllSpawnsCommand.register(event.getDispatcher());
        RemoveSpawnCommand.register(event.getDispatcher());

        StartGameCommand.register(event.getDispatcher());
        StopGameCommand.register(event.getDispatcher());

        SetItemsTimeDefaultCommand.register(event.getDispatcher());
        DecreaseItemsTimeDefaultCommand.register(event.getDispatcher());
        IncreaseItemsTimeDefaultCommand.register(event.getDispatcher());
    }

    public static Logger getLogger() {
        return logger;
    }
}

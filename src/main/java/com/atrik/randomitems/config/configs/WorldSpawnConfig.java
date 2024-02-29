package com.atrik.randomitems.config.configs;

import com.atrik.randomitems.config.BaseConfig;
import com.atrik.randomitems.game.GameManager;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WorldSpawnConfig extends BaseConfig<List<BlockPos>> {
    private static final Gson cfgGson = new Gson();

    @Override
    public JsonElement saveData(List<BlockPos> data) {
        data = GameManager.getGameManager().spawns;
        List<Long> data1 = new ArrayList<>();
        data.forEach(blockPos -> data1.add(BlockPos.asLong(blockPos.getX(), blockPos.getY(), blockPos.getZ())));
        return JsonParser.parseString(cfgGson.toJson(data1, new TypeToken<List<Long>>() {}.getType()));
    }

    @Override
    public List<BlockPos> parseData(JsonElement data) {
        List<BlockPos> poses = new ArrayList<>();
        data.getAsJsonArray().forEach(jsPos -> {
            poses.add(BlockPos.of(jsPos.getAsLong()));
        });
        return poses;
    }

    @Override
    protected Gson getGson() {
        return cfgGson;
    }

    @Override
    protected String getMinJsonStr() {
        return "[]";
    }

    @Override
    public File getConfigFile() {
        return new File(ServerLifecycleHooks.getCurrentServer().getWorldPath(LevelResource.ROOT).toFile().getAbsolutePath() + "/randomitemsmod", "world_spawns.json");
    }
}

package com.atrik.randomitems.config.configs;

import com.atrik.randomitems.config.BaseConfig;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class MainConfig extends BaseConfig<Map<String, Object>> {
    private static final Gson cfgGson = new GsonBuilder().setPrettyPrinting().enableComplexMapKeySerialization().create();

    @Override
    public JsonElement saveData(Map<String, Object> data) {
        return JsonParser.parseString(cfgGson.toJson(data, new TypeToken<Map<String, Object>>() {}.getType()));
    }

    @Override
    public Map<String, Object> parseData(JsonElement data) {
        return getGson().fromJson(data, new TypeToken<Map<String, Object>>() {}.getType());
    }

    @Override
    protected Gson getGson() {
        return cfgGson;
    }

    @Override
    protected String getMinJsonStr() {
        return "{}";
    }

    @Override
    public File getConfigFile() {
        return new File(ServerLifecycleHooks.getCurrentServer().getWorldPath(LevelResource.ROOT).toFile().getAbsolutePath() + "/randomitemsmod", "main_cfg.json");
    }

    public Object getOrNull(String key) {
        return data.getOrDefault(key, null);
    }

    public Object getOrThrow(String key) {
        Object value;
        if ((value = data.get(key)) == null) {
            throw new RuntimeException("No such field " + key + " in main config!");
        }
        return value;
    }

    public void setIfNull(String key, Object value) {
        if (getOrNull(key) == null) {
            data.put(key, value);
            try {
                saveAll();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void set(String key, Object value) {
        data.put(key, value);
        try {
            saveAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

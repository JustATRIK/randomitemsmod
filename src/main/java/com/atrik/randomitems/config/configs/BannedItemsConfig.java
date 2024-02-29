package com.atrik.randomitems.config.configs;

import com.atrik.randomitems.config.BaseConfig;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BannedItemsConfig extends BaseConfig<List<Item>> {
    private static final Gson cfgGson = new Gson();

    @Override
    public JsonElement saveData(List<Item> data) {
        List<String> data1 = new ArrayList<>();
        data.forEach(item -> {
            data1.add(ForgeRegistries.ITEMS.getKey(item).toString());
        });
        return JsonParser.parseString(getGson().toJson(data1, new TypeToken<List<String>> () {}.getType()));
    }

    @Override
    public List<Item> parseData(JsonElement data) {
        List<Item> items = new ArrayList<>();

        JsonArray jsonArray = data.getAsJsonArray();
        jsonArray.forEach(jsonElement -> {
            items.add(ForgeRegistries.ITEMS.getValue(new ResourceLocation(jsonElement.getAsString())));
        });
        return items;
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
        return new File(ServerLifecycleHooks.getCurrentServer().getWorldPath(LevelResource.ROOT).toFile().getAbsolutePath() + "/randomitemsmod", "banned_items.json");
    }
}

package com.atrik.randomitems.config;

import com.atrik.randomitems.RandomItemsMod;

import java.util.HashMap;
import java.util.Map;

public class ModConfigHolder {
    private final static Map<String, BaseConfig<?>> configMap = new HashMap<>();

    public static void addConfigEntry(String name, BaseConfig<?> config) {
        if (configMap.containsKey(name)) throw new RuntimeException("Already registered config with same name");

        configMap.put(name, config);
    }

    public static BaseConfig<?> getConfigByName(String name) {
        return configMap.get(name);
    }

    public static void onModSetup() {
        RandomItemsMod.getLogger().info("FML Setup in Random Items config holder!");
    }
}

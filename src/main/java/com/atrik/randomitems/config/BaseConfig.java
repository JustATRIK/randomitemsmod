package com.atrik.randomitems.config;

import com.atrik.randomitems.RandomItemsMod;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.*;
import java.util.concurrent.TimeUnit;

public abstract class BaseConfig<T> {
    protected T data;

    public BaseConfig(){
        try {
            initialize();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract JsonElement saveData(T data);
    public abstract T parseData(JsonElement data);
    protected abstract Gson getGson();
    protected abstract String getMinJsonStr();
    public abstract File getConfigFile();

    protected JsonElement loadConfig(File configFile) {
        try {
            return JsonParser.parseReader(new BufferedReader(new FileReader(configFile)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    protected void createIfNotExist() throws IOException {
        if (getConfigFile().exists()) throw new RuntimeException("Config file already exists");

        getConfigFile().getParentFile().mkdirs();
        if (!getConfigFile().createNewFile()) {
            throw new IOException("Failed to create " + getConfigFile().getName() + " config file");
        }

        writeMinJson();
    }

    protected void writeMinJson() throws IOException {
        FileWriter fileWriter = new FileWriter(getConfigFile());
        fileWriter.write(getMinJsonStr());
        fileWriter.close();
    }

    public void saveAll() throws IOException {
        FileWriter fileWriter = new FileWriter(getConfigFile());
        fileWriter.write(saveData(data).toString());
        fileWriter.close();
    }

    public void initialize() throws IOException, InterruptedException {
        if (!getConfigFile().exists()) createIfNotExist();

        T data = null;
        int failedCounter = 0;
        while (data == null) {
            try {
                data = parseData(loadConfig(getConfigFile()));
            } catch (Exception ex) {
                RandomItemsMod.getLogger().warning("Failed to load " + getConfigFile().getName() + " config. Retrying");
                RandomItemsMod.getLogger().severe(ex.toString());
                writeMinJson();
                TimeUnit.SECONDS.sleep(1);
                if (++failedCounter >= 3) {
                    throw new RuntimeException("Failed to load " + getConfigFile().getName() + " 3 times. Report this please!");
                }
            }
        }
        setData(data);
    }

    protected void setData(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }
}

package de.neocraftr.simplemqtt.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;

public class FileManager {

    private File configFile;
    private JsonObject config;

    public FileManager() {
        configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "simple-mqtt.json");
        if(!configFile.isFile()) {
            try {
                configFile.createNewFile();
                config = new JsonObject();
                saveConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            loadConfig();
        }
    }

    public void loadConfig() {
        try {
            FileReader reader = new FileReader(configFile);
            config = new JsonParser().parse(reader).getAsJsonObject();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        try {
            FileWriter writer = new FileWriter(configFile);
            new Gson().toJson(config, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JsonObject getConfig() {
        return config;
    }
}

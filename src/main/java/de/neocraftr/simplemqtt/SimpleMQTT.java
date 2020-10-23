package de.neocraftr.simplemqtt;

import de.neocraftr.simplemqtt.commands.MQTTCommand;
import de.neocraftr.simplemqtt.gui.ConfigGUI;
import de.neocraftr.simplemqtt.utils.FileManager;
import de.neocraftr.simplemqtt.utils.MQTTClient;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

public class SimpleMQTT implements ModInitializer {

    private static SimpleMQTT simpleMQTT;
    private FileManager fileManager;
    private MQTTClient mqttClient;
    private ConfigGUI configGUI;

    @Override
    public void onInitialize() {
        simpleMQTT = this;
        fileManager = new FileManager();
        mqttClient = new MQTTClient();
        configGUI = new ConfigGUI();

        mqttClient.connect();

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            new MQTTCommand(dispatcher);
        });
    }

    public static SimpleMQTT getSimpleMQTT() {
        return simpleMQTT;
    }

    public MQTTClient getMqttClient() {
        return mqttClient;
    }

    public ConfigGUI getConfigGUI() {
        return configGUI;
    }

    public FileManager getFileManager() {
        return fileManager;
    }
}

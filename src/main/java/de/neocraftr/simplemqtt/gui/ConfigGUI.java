package de.neocraftr.simplemqtt.gui;

import com.google.gson.JsonObject;
import de.neocraftr.simplemqtt.SimpleMQTT;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

public class ConfigGUI {

    private String mqttHost;
    private int mqttPort;
    private String mqttUsername;
    private String mqttPassword, editedMqttPassword;

    public ConfigGUI() {
        JsonObject cfg = getSimpleMQTT().getFileManager().getConfig();
        if(!cfg.has("mqttHost")) {
            cfg.addProperty("mqttHost", "localhost");
        }
        if(!cfg.has("mqttPort")) {
            cfg.addProperty("mqttPort", 1883);
        }
        if(!cfg.has("mqttUsername")) {
            cfg.addProperty("mqttUsername", "");
        }
        if(!cfg.has("mqttPassword")) {
            cfg.addProperty("mqttPassword", "");
        }

        mqttHost = cfg.get("mqttHost").getAsString();
        mqttPort = cfg.get("mqttPort").getAsInt();
        mqttUsername = cfg.get("mqttUsername").getAsString();
        mqttPassword = cfg.get("mqttPassword").getAsString();
    }

    public Screen createGui() {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(MinecraftClient.getInstance().currentScreen)
                .setTitle(Text.of("Simple MQTT"));

        builder.setSavingRunnable(() -> {
            JsonObject cfg = getSimpleMQTT().getFileManager().getConfig();
            cfg.addProperty("mqttHost", mqttHost);
            cfg.addProperty("mqttPort", mqttPort);
            cfg.addProperty("mqttUsername", mqttUsername);
            if(!editedMqttPassword.matches("^\\*+$")) {
                mqttPassword = editedMqttPassword;
                cfg.addProperty("mqttPassword", mqttPassword);
            }

            getSimpleMQTT().getFileManager().saveConfig();

            getSimpleMQTT().getMqttClient().connect();
        });

        ConfigCategory mqttBroker = builder.getOrCreateCategory(Text.of("Mqtt Broker"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        mqttBroker.addEntry(entryBuilder.startStrField(Text.of("Host"), mqttHost)
                .setDefaultValue("localhost")
                .setTooltip(Text.of("The address of the MQTT broker"))
                .setSaveConsumer(value -> mqttHost = value)
                .setErrorSupplier(value -> {
                    if(value.isEmpty()) {
                        return Optional.of(Text.of("Host must be specified!"));
                    } else {
                        return Optional.empty();
                    }
                })
                .build());

        mqttBroker.addEntry(entryBuilder.startIntField(Text.of("Port"), mqttPort)
                .setDefaultValue(1883)
                .setTooltip(Text.of("The port of the MQTT broker"))
                .setSaveConsumer(value -> mqttPort = value)
                .build());

        mqttBroker.addEntry(entryBuilder.startStrField(Text.of("Username (optional)"), mqttUsername)
                .setDefaultValue("")
                .setTooltip(Text.of("The username for the MQTT broker"))
                .setSaveConsumer(value -> mqttUsername = value)
                .build());

        editedMqttPassword = mqttPassword.replaceAll(".", "*");
        mqttBroker.addEntry(entryBuilder.startStrField(Text.of("Password (optional)"), editedMqttPassword)
                .setDefaultValue("")
                .setTooltip(Text.of("The password for the MQTT broker"))
                .setSaveConsumer(value -> editedMqttPassword = value)
                .build());

        return builder.build();
    }

    public void openGui() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                MinecraftClient.getInstance().openScreen(createGui());
            }
        }, 100);
    }

    private SimpleMQTT getSimpleMQTT() {
        return SimpleMQTT.getSimpleMQTT();
    }
}

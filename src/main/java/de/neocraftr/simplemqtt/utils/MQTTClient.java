package de.neocraftr.simplemqtt.utils;

import com.google.gson.JsonObject;
import de.neocraftr.simplemqtt.SimpleMQTT;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.List;

public class MQTTClient {

    private MqttClient client;
    private List<String> subscribedTopics = new ArrayList<>();

    public boolean connect() {
        if(client != null && client.isConnected()) disconnect();

        JsonObject cfg = getSimpleMQTT().getFileManager().getConfig();
        String host = cfg.get("mqttHost").getAsString();
        int port = cfg.get("mqttPort").getAsInt();
        String username = cfg.get("mqttUsername").getAsString();
        String password = cfg.get("mqttPassword").getAsString();

        try {
            client = new MqttClient("tcp://"+host+":"+port, MqttClient.generateClientId(), new MemoryPersistence());

            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            if(!username.trim().equals("")) options.setUserName(username);
            if(!password.trim().equals("")) options.setPassword(password.toCharArray());

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    if(MinecraftClient.getInstance().player != null)
                        MinecraftClient.getInstance().player.sendMessage(Text.of("§eMQTT message in §a"+topic+"§e: §b"+new String(message.getPayload())), false);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });

            client.connect(options);
            return true;
        } catch (MqttException e) {
            System.out.println("Can't connect to MQTT broker: "+e.getMessage());
        }
        return false;
    }

    public void disconnect() {
        if(client == null) return;
        try {
            client.disconnect();
            client.close();
        } catch (MqttException e) {
            e.printStackTrace();
        }
        client = null;
        subscribedTopics.clear();
    }

    public boolean isConnected() {
        return client != null && client.isConnected();
    }

    public String getClientId() {
        return client.getClientId();
    }

    public void publish(String topic, String payload) {
        try {
            client.publish(topic, payload.getBytes(), 2, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(String topic) {
        try {
            client.subscribe(topic);
            if(!subscribedTopics.contains(topic)) subscribedTopics.add(topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void unsubscribe(String topic) {
        try {
            client.unsubscribe(topic);
            subscribedTopics.remove(topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public List<String> getSubscribedTopics() {
        return subscribedTopics;
    }

    private SimpleMQTT getSimpleMQTT() {
        return SimpleMQTT.getSimpleMQTT();
    }
}

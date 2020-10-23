package de.neocraftr.simplemqtt.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.neocraftr.simplemqtt.SimpleMQTT;
import de.neocraftr.simplemqtt.utils.MQTTClient;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.StringJoiner;

public class MQTTCommand {

    public MQTTCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("mqtt")
                .then(CommandManager.literal("settings").executes(context -> settings(context.getSource())))
                .then(CommandManager.literal("info").executes(context -> info(context.getSource())))
                .then(CommandManager.literal("connect").executes(context -> connect(context.getSource())))
                .then(CommandManager.literal("disconnect").executes(context -> disconnect(context.getSource())))
                .then(CommandManager.literal("publish")
                        .then(CommandManager.argument("topic", StringArgumentType.string())
                            .then(CommandManager.argument("message", StringArgumentType.greedyString())
                                    .executes(context -> publish(context.getSource(),
                                            StringArgumentType.getString(context, "topic"),
                                            StringArgumentType.getString(context, "message"))))))
                .then(CommandManager.literal("subscribe")
                    .then(CommandManager.argument("topic", StringArgumentType.string())
                        .executes(context -> subscribe(context.getSource(), StringArgumentType.getString(context, "topic")))))
                .then(CommandManager.literal("unsubscribe")
                        .then(CommandManager.argument("topic", StringArgumentType.string())
                                .executes(context -> unsubscribe(context.getSource(), StringArgumentType.getString(context, "topic")))))
        );
    }

    private int settings(ServerCommandSource source) throws CommandSyntaxException {
        if(source.getPlayer() != null)
            getSimpleMQTT().getConfigGUI().openGui();
        return 1;
    }

    private int info(ServerCommandSource source) {
        MQTTClient client = getSimpleMQTT().getMqttClient();
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("§eMQTT client information");
        joiner.add("§7Connected: "+(client.isConnected() ? "§atrue" : "§cfalse"));
        if(client.isConnected()) {
            joiner.add("§7Client ID: §a"+client.getClientId());
            if(!client.getSubscribedTopics().isEmpty()) {
                joiner.add("§7Subscribed topics:");
                for(String topic : client.getSubscribedTopics()) {
                    joiner.add("§8- §a"+topic);
                }
            }
        }
        source.sendFeedback(Text.of(joiner.toString()), false);
        return 1;
    }

    private int connect(ServerCommandSource source) {
        if(!getSimpleMQTT().getMqttClient().isConnected()) {
            if(getSimpleMQTT().getMqttClient().connect()) {
                source.sendFeedback(Text.of("§aConnected to MQTT broker."), false);
            } else {
                source.sendError(Text.of("§cCan't connect to MQTT broker. Please view logs for more information."));
            }
        } else {
            source.sendFeedback(Text.of("§cAlready connected to MQTT broker."), false);
        }
        return 1;
    }

    private int disconnect(ServerCommandSource source) {
        if(getSimpleMQTT().getMqttClient().isConnected()) {
            getSimpleMQTT().getMqttClient().disconnect();
             source.sendFeedback(Text.of("§aDisconnected from MQTT broker."), false);
        } else {
             source.sendError(Text.of("§cNot connected to MQTT broker."));
        }
        return 1;
    }

    private int publish(ServerCommandSource source, String topic, String message) {
        if(getSimpleMQTT().getMqttClient().isConnected()) {
            getSimpleMQTT().getMqttClient().publish(topic, message);
            source.sendFeedback(Text.of("§aPublished message §b"+message+" §ato §e"+topic+"§a."), false);
        } else {
            source.sendError(Text.of("§cNot connected to MQTT broker."));
        }
        return 1;
    }

    private int subscribe(ServerCommandSource source, String topic) {
        if(getSimpleMQTT().getMqttClient().isConnected()) {
            if(!getSimpleMQTT().getMqttClient().getSubscribedTopics().contains(topic)) {
                getSimpleMQTT().getMqttClient().subscribe(topic);
                source.sendFeedback(Text.of("§aSubscribed to topic §e"+topic+"§a."), false);
            } else {
                source.sendError(Text.of("§cAlready subscribed to topic §e"+topic+"§c."));
            }
        } else {
            source.sendError(Text.of("§cNot connected to MQTT broker."));
        }
        return 1;
    }

    private int unsubscribe(ServerCommandSource source, String topic) {
        if(getSimpleMQTT().getMqttClient().isConnected()) {
            if(getSimpleMQTT().getMqttClient().getSubscribedTopics().contains(topic)) {
                getSimpleMQTT().getMqttClient().unsubscribe(topic);
                source.sendFeedback(Text.of("§aUnsubscribed topic §e"+topic+"§a."), false);
            } else {
                source.sendError(Text.of("§cNot subscribed to topic §e"+topic+"§c."));
            }
        } else {
            source.sendError(Text.of("§cNot connected to MQTT broker."));
        }
        return 1;
    }

    private SimpleMQTT getSimpleMQTT() {
        return SimpleMQTT.getSimpleMQTT();
    }
}

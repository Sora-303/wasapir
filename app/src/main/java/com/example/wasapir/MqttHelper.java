package com.example.wasapir;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;

import java.nio.charset.StandardCharsets;

public class MqttHelper {

    private Mqtt3AsyncClient client;
    private OnMessageReceivedListener listener;

    // Guardamos el UID del usuario actual
    private final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public void connect() {
        String serverUri = "c21a6fa242bf41c9952f4b876fe7dcb8.s1.eu.hivemq.cloud";
        int port = 8883;

        client = MqttClient.builder()
                .useMqttVersion3()
                .identifier("wasapiBeta_" + System.currentTimeMillis())
                .serverHost(serverUri)
                .serverPort(port)
                .sslWithDefaultConfig()
                .buildAsync();

        client.connectWith()
                .simpleAuth()
                .username("Foxito")
                .password("3312qQtt".getBytes(StandardCharsets.UTF_8))
                .applySimpleAuth()
                .send()
                .whenComplete((connAck, throwable) -> {
                    if (throwable == null) {
                        Log.d("MQTT", "✅ Conectado a HiveMQ Cloud");
                        // ✅ Ya no nos suscribimos aquí
                        // La suscripción se hace en ChatActivity al canal correcto
                    } else {
                        Log.e("MQTT", "❌ Error de conexión: " + throwable.getMessage());
                    }
                });
    }

    public void subscribe(String topic) {
        client.subscribeWith()
                .topicFilter(topic)
                .qos(com.hivemq.client.mqtt.datatypes.MqttQos.AT_LEAST_ONCE)
                .callback(this::handleMessage)
                .send();
    }

    private void handleMessage(Mqtt3Publish publish) {
        String msg = new String(publish.getPayloadAsBytes(), StandardCharsets.UTF_8);
        Log.d("MQTT", "📩 Mensaje recibido: " + msg);

        // Esperamos formato "uid:mensaje"
        if (msg.contains(":")) {
            String[] parts = msg.split(":", 2);
            String senderUid = parts[0];
            String content = parts[1];

            // Ignorar si el mensaje viene de mí mismo
            if (!senderUid.equals(myUid)) {
                if (listener != null) {
                    listener.onMessageReceived(content);
                }
            }
        } else {
            // Mensaje sin formato, lo pasamos tal cual
            if (listener != null) {
                listener.onMessageReceived(msg);
            }
        }
    }

    public void publish(String topic, String msg) {
        // Publicamos con el UID incluido
        String formattedMsg = myUid + ":" + msg;

        client.publishWith()
                .topic(topic)
                .payload(formattedMsg.getBytes(StandardCharsets.UTF_8))
                .qos(com.hivemq.client.mqtt.datatypes.MqttQos.AT_LEAST_ONCE)
                .send();
    }

    public void setOnMessageReceivedListener(OnMessageReceivedListener listener) {
        this.listener = listener;
    }

    public interface OnMessageReceivedListener {
        void onMessageReceived(String message);
    }
}
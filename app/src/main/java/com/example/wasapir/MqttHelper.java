package com.example.wasapir;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttHelper {

    private MqttAndroidClient client;
    private OnMessageReceivedListener listener;

    public void connect(Context context) {
        String serverUri = "ssl://c21a6fa242bf41c9952f4b876fe7dcb8.s1.eu.hivemq.cloud:8883";
        String clientId = "wasapiBeta_" + System.currentTimeMillis();

        client = new MqttAndroidClient(context, serverUri, clientId);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName("Foxito");
        options.setPassword("3312qQtt".toCharArray());
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        options.setKeepAliveInterval(60);
        options.setAutomaticReconnect(true);

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("MQTT", "✅ Conectado a HiveMQ Cloud");
                    subscribe("chat/general");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("MQTT", "❌ Error de conexión: " + exception.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void subscribe(String topic) {
        try {
            client.subscribe(topic, 1);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.e("MQTT", "⚠️ Conexión perdida");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    String msg = new String(message.getPayload());
                    Log.d("MQTT", "📩 Mensaje recibido: " + msg);

                    if (listener != null) {
                        listener.onMessageReceived(msg);
                    }
                }

                @Override
                public void deliveryComplete(org.eclipse.paho.client.mqttv3.IMqttDeliveryToken token) {
                    Log.d("MQTT", "✅ Mensaje entregado");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void publish(String topic, String msg) {
        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(msg.getBytes());
            client.publish(topic, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Setter del listener
    public void setOnMessageReceivedListener(OnMessageReceivedListener listener) {
        this.listener = listener;
    }

    // Interfaz
    public interface OnMessageReceivedListener {
        void onMessageReceived(String message);
    }
}
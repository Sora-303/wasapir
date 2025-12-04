package com.example.wasapir;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView messagesRecyclerView;
    private EditText messageInput;
    private Button sendButton;
    private MessageAdapter adapter;
    private List<String> messagesList = new ArrayList<>();

    private MqttHelper mqttHelper;
    private String topic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        adapter = new MessageAdapter(messagesList);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesRecyclerView.setAdapter(adapter);

        // Inicializar MQTT
        mqttHelper = new MqttHelper();
        mqttHelper.connect(this);

        // Obtener UIDs
        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String contactUid = getIntent().getStringExtra("contactUid");

        topic = "chat/" + myUid + "_" + contactUid;
        mqttHelper.subscribe(topic);

        // Enviar mensaje
        sendButton.setOnClickListener(v -> {
            String msg = messageInput.getText().toString().trim();
            if (!msg.isEmpty()) {
                mqttHelper.publish(topic, msg);
                messagesList.add("Yo: " + msg);
                adapter.notifyItemInserted(messagesList.size() - 1);
                messageInput.setText("");
            }
        });

        // Callback para recibir mensajes
        mqttHelper.setOnMessageReceivedListener((receivedMsg) -> {
            runOnUiThread(() -> {
                messagesList.add("Contacto: " + receivedMsg);
                adapter.notifyItemInserted(messagesList.size() - 1);
                messagesRecyclerView.scrollToPosition(messagesList.size() - 1);
            });
        });
    }
}
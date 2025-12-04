package com.example.wasapir;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

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
    private DatabaseReference dbRef;

    private String myUid;
    private String contactUid;
    private String conversationId;

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
        mqttHelper.connect();

        // Firebase
        dbRef = FirebaseDatabase.getInstance().getReference("chats");

        // Obtener UIDs
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        contactUid = getIntent().getStringExtra("contactUid");

        // Conversación única
        conversationId = getSortedTopic(myUid, contactUid);
        topic = "chat/" + conversationId;

        // Suscribirse al tópico MQTT
        mqttHelper.subscribe(topic);

        // Recuperar historial desde Firebase
        loadMessagesFromFirebase();

        // Enviar mensaje
        sendButton.setOnClickListener(v -> {
            String msg = messageInput.getText().toString().trim();
            if (!msg.isEmpty()) {
                // Publicar en MQTT
                mqttHelper.publish(topic, msg);

                // Guardar en Firebase SOLO aquí
                DatabaseReference messagesRef = dbRef.child(conversationId).child("messages");
                String key = messagesRef.push().getKey();
                Message messageObj = new Message(myUid, msg, System.currentTimeMillis());
                messagesRef.child(key).setValue(messageObj);

                messageInput.setText("");
            }
        });

        // Callback MQTT para recibir mensajes
        mqttHelper.setOnMessageReceivedListener((receivedMsg) -> {
            runOnUiThread(() -> {
                // ⚠️ Ya no guardamos en Firebase aquí
                // Solo dejamos que Firebase notifique y actualice la UI
                Log.d("MQTT", "Mensaje recibido por MQTT: " + receivedMsg);
            });
        });
    }

    // 🔧 Método para ordenar UIDs y generar un canal único
    private String getSortedTopic(String uid1, String uid2) {
        return uid1.compareTo(uid2) < 0 ? uid1 + "_" + uid2 : uid2 + "_" + uid1;
    }

    // 🔧 Cargar historial desde Firebase
    private void loadMessagesFromFirebase() {
        DatabaseReference messagesRef = dbRef.child(conversationId).child("messages");
        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                messagesList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Message msg = child.getValue(Message.class);
                    if (msg != null) {
                        if (msg.getSenderUid().equals(myUid)) {
                            messagesList.add("Yo: " + msg.getText());
                        } else {
                            messagesList.add("Contacto: " + msg.getText());
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                messagesRecyclerView.scrollToPosition(messagesList.size() - 1);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", "Error al leer mensajes: " + error.getMessage());
            }
        });
    }
}
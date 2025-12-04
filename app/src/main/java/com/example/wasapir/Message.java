package com.example.wasapir;

public class Message {
    private String senderUid;
    private String text;
    private long timestamp;

    // 🔧 Constructor vacío requerido por Firebase
    public Message() {}

    public Message(String senderUid, String text, long timestamp) {
        this.senderUid = senderUid;
        this.text = text;
        this.timestamp = timestamp;
    }

    // Getters y setters
    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

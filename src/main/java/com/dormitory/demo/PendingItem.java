package com.dormitory.demo;

public class PendingItem {
    private String type;
    private String text;
    private String status;
    private String id;

    public PendingItem(String type, String text, String status, String id) {
        this.type = type;
        this.text = text;
        this.status = status;
        this.id = id;
    }

    public String getType() { return type; }
    public String getText() { return text; }
    public String getStatus() { return status; }
    public String getId() { return id; }
}
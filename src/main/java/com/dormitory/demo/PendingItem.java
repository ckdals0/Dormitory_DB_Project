package com.dormitory.demo;

public class PendingItem {
    private String type;    // 예: "외박", "민원"
    private String text;    // 예: "홍길동 (2024001) - 외박 신청"
    private String status;  // 예: "대기", "접수"
    private String id;      // 처리를 위한 고유 ID (Absence_ID 등)

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
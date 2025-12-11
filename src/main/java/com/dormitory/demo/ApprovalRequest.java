package com.dormitory.demo;

public class ApprovalRequest {
    private String id;      // 대상 ID (Absence_ID 등)
    private String status;  // 변경할 상태 (Approved, Rejected)

    // Getter, Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
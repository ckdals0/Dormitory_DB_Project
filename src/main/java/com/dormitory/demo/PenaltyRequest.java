package com.dormitory.demo;

public class PenaltyRequest {
    private String studentId; // 대상 학생 학번
    private String type;      // 상점/벌점
    private int points;       // 점수
    private String reason;    // 사유

    // Getter, Setter
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
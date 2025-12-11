package com.dormitory.demo;

public class StatsResponse {
    private int totalStudents;
    private int todayAbsence;
    private int pendingAbsence;
    private int pendingComplaints;

    // 생성자, Getter
    public StatsResponse(int totalStudents, int todayAbsence, int pendingAbsence, int pendingComplaints) {
        this.totalStudents = totalStudents;
        this.todayAbsence = todayAbsence;
        this.pendingAbsence = pendingAbsence;
        this.pendingComplaints = pendingComplaints;
    }

    public int getTotalStudents() { return totalStudents; }
    public int getTodayAbsence() { return todayAbsence; }
    public int getPendingAbsence() { return pendingAbsence; }
    public int getPendingComplaints() { return pendingComplaints; }
}
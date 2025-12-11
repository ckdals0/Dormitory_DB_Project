package com.dormitory.demo;

public class Student {
    private String sid;
    private String name;
    private String gender;
    private String deptName;
    private String roomNo;
    private int totalPenalty; // ★ 추가됨

    public Student(String sid, String name, String gender, String deptName, String roomNo, int totalPenalty) {
        this.sid = sid;
        this.name = name;
        this.gender = gender;
        this.deptName = deptName;
        this.roomNo = roomNo;
        this.totalPenalty = totalPenalty;
    }

    public String getSid() { return sid; }
    public String getName() { return name; }
    public String getGender() { return gender; }
    public String getDeptName() { return deptName; }
    public String getRoomNo() { return roomNo; }
    public int getTotalPenalty() { return totalPenalty; } // Getter 추가
}
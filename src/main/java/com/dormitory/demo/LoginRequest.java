package com.dormitory.demo;

public class LoginRequest {
    private String id;       // 학번 또는 관리자ID
    private String password; // ★ 변경됨: 이름 -> 비밀번호
    private String role;     // 역할

    // Getter, Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPassword() { return password; } // 메서드 이름 변경
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
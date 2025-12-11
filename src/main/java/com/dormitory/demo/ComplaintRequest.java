package com.dormitory.demo;

public class ComplaintRequest {
    private String facilityId; // ★ 추가됨: 고장 난 시설물 ID
    private String content;    // 신고 내용

    // Getter, Setter
    public String getFacilityId() { return facilityId; }
    public void setFacilityId(String facilityId) { this.facilityId = facilityId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
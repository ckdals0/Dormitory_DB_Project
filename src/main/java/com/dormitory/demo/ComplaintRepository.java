package com.dormitory.demo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
public class ComplaintRepository {

    private final JdbcTemplate jdbcTemplate;

    public ComplaintRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 1. 학생용: 민원 저장
    public void save(String sid, String roomNo, String content) {
        String sql = "INSERT INTO COMPLAINT (Comp_ID, SID, Room_No, Date, Content, Status) VALUES (?, ?, ?, CURRENT_DATE, ?, ?)";
        String compId = "C-" + (int)(Math.random() * 100000);
        jdbcTemplate.update(sql, compId, sid, roomNo, content, "접수");
    }

    // 2. 관리자용: 민원 전체 목록 조회 (관리자 이름 포함)
    public List<Map<String, Object>> findAllDetailed() {
        String sql = """
            SELECT c.*, s.Name AS StudentName, m.MName AS Manager_Name
            FROM COMPLAINT c
            JOIN STUDENT s ON c.SID = s.SID
            LEFT JOIN MANAGER m ON c.Manager_ID = m.Manager_ID
            ORDER BY 
                CASE WHEN c.Status = '접수' THEN 1 
                     WHEN c.Status = '처리중' THEN 2 
                     ELSE 3 END,
                c.Date DESC
        """;
        return jdbcTemplate.queryForList(sql);
    }

    // 3. ★ 상태 변경 (관리자 ID 포함) - 3개 매개변수 버전
    public void updateStatus(String compId, String status, String managerId) {
        System.out.println("=== ComplaintRepository.updateStatus 호출됨 ===");
        System.out.println("Comp_ID: " + compId);
        System.out.println("Status: " + status);
        System.out.println("Manager_ID: " + managerId);

        // (1) 민원 상태 업데이트 + 관리자 정보 기록
        String sql = "UPDATE COMPLAINT SET Status = ?, Manager_ID = ?, Processed_Date = CURRENT_TIMESTAMP WHERE Comp_ID = ?";
        int updated = jdbcTemplate.update(sql, status, managerId, compId);

        System.out.println("업데이트된 행 수: " + updated);

        // (2) 만약 '완료' 처리라면, 연결된 시설물을 찾아 '정상'으로 변경
        if ("완료".equals(status)) {
            try {
                String selectSql = "SELECT Content FROM COMPLAINT WHERE Comp_ID = ?";
                String content = jdbcTemplate.queryForObject(selectSql, String.class, compId);

                if (content != null && content.startsWith("[")) {
                    int endIdx = content.indexOf("]");
                    if (endIdx > 1) {
                        String facId = content.substring(1, endIdx);
                        String updateFacSql = "UPDATE FACILITY SET Status = '정상' WHERE Fac_ID = ?";
                        jdbcTemplate.update(updateFacSql, facId);
                        System.out.println("시설물 복구 완료: " + facId);
                    }
                }
            } catch (Exception e) {
                System.out.println("시설물 상태 자동 연동 실패: " + e.getMessage());
            }
        }
    }

    // 4. 학생별 민원 내역 조회 (관리자 이름 포함)
    public List<Map<String, Object>> findHistoryByStudent(String sid) {
        String sql = """
            SELECT c.*, m.MName AS Manager_Name
            FROM COMPLAINT c
            LEFT JOIN MANAGER m ON c.Manager_ID = m.Manager_ID
            WHERE c.SID = ? 
            ORDER BY c.Date DESC
        """;
        return jdbcTemplate.queryForList(sql, sid);
    }
}
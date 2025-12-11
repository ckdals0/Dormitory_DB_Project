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

    // 2. 관리자용: 민원 전체 목록 조회
    public List<Map<String, Object>> findAllDetailed() {
        String sql = """
            SELECT c.*, s.Name AS StudentName 
            FROM COMPLAINT c
            JOIN STUDENT s ON c.SID = s.SID
            ORDER BY 
                CASE WHEN c.Status = '접수' THEN 1 
                     WHEN c.Status = '처리중' THEN 2 
                     ELSE 3 END,
                c.Date DESC
        """;
        return jdbcTemplate.queryForList(sql);
    }

    // 3. ★ 수정됨: 상태 변경 및 시설물 상태 자동 복구
    public void updateStatus(String compId, String status) {
        // (1) 민원 상태 업데이트
        String sql = "UPDATE COMPLAINT SET Status = ? WHERE Comp_ID = ?";
        jdbcTemplate.update(sql, status, compId);

        // (2) 만약 '완료' 처리라면, 연결된 시설물을 찾아 '정상'으로 변경
        if ("완료".equals(status)) {
            try {
                // 해당 민원의 내용(Content) 조회
                String selectSql = "SELECT Content FROM COMPLAINT WHERE Comp_ID = ?";
                String content = jdbcTemplate.queryForObject(selectSql, String.class, compId);

                // 내용에서 [Fac_ID] 추출 로직 (예: "[AC-101] 에어컨 고장" -> "AC-101")
                if (content != null && content.startsWith("[")) {
                    int endIdx = content.indexOf("]");
                    if (endIdx > 1) {
                        String facId = content.substring(1, endIdx);

                        // 시설물 상태를 '정상'으로 업데이트
                        String updateFacSql = "UPDATE FACILITY SET Status = '정상' WHERE Fac_ID = ?";
                        jdbcTemplate.update(updateFacSql, facId);
                        System.out.println("시설물 복구 완료: " + facId);
                    }
                }
            } catch (Exception e) {
                // 시설물 ID 파싱 실패하거나 해당 시설물이 없을 경우 (일반 민원 등) 무시
                System.out.println("시설물 상태 자동 연동 실패 (일반 민원이거나 ID 없음): " + e.getMessage());
            }
        }
    }

    // 4. 학생별 민원 내역 조회
    public List<Map<String, Object>> findHistoryByStudent(String sid) {
        String sql = "SELECT * FROM COMPLAINT WHERE SID = ? ORDER BY Date DESC";
        return jdbcTemplate.queryForList(sql, sid);
    }
}
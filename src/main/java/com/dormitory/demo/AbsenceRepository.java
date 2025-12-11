package com.dormitory.demo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
public class AbsenceRepository {

    private final JdbcTemplate jdbcTemplate;

    public AbsenceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 1. 외박 신청 저장 (학생용)
    public void save(String sid, AbsenceRequest request) {
        String sql = "INSERT INTO ABSENCE (Absence_ID, SID, Start_Date, Return_Date, Status, Reason) VALUES (?, ?, ?, ?, ?, ?)";

        // ID 생성 (실무에서는 Sequence나 UUID 사용 권장)
        String absenceId = "A-" + (int)(Math.random() * 1000000);

        jdbcTemplate.update(sql,
                absenceId,
                sid,
                request.getStartDate(),
                request.getReturnDate(),
                "Pending", // 초기 상태: 대기
                request.getReason()
        );
    }

    // 2. 대기 목록 요약 조회 (관리자 메인 대시보드용 - 최신 5개)
    public List<PendingItem> findPendingItems() {
        String sql = """
            SELECT a.Absence_ID, s.Name, s.SID, a.Reason 
            FROM ABSENCE a 
            JOIN STUDENT s ON a.SID = s.SID
            WHERE a.Status = 'Pending' 
            ORDER BY a.Start_Date ASC 
            LIMIT 5
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            String desc = String.format("%s (%s) - %s",
                    rs.getString("Name"),
                    rs.getString("SID"),
                    rs.getString("Reason"));

            return new PendingItem("외박", desc, "대기", rs.getString("Absence_ID"));
        });
    }

    // 3. ★ 승인 대기 목록 상세 조회 (관리자 승인 팝업용 - 전체)
    // 학생의 이름과 학과 정보까지 조인해서 가져옵니다.
    public List<Map<String, Object>> findAllPendingDetailed() {
        String sql = """
            SELECT a.*, s.Name, s.Dept_Name 
            FROM ABSENCE a 
            JOIN STUDENT s ON a.SID = s.SID 
            WHERE a.Status = 'Pending' 
            ORDER BY a.Start_Date ASC
        """;
        return jdbcTemplate.queryForList(sql);
    }

    // 4. ★ 상태 변경 (승인/반려 처리)
    public void updateStatus(String absenceId, String status) {
        String sql = "UPDATE ABSENCE SET Status = ? WHERE Absence_ID = ?";
        jdbcTemplate.update(sql, status, absenceId);
    }

    public List<Map<String, Object>> findHistoryByStudent(String sid) {
        String sql = "SELECT * FROM ABSENCE WHERE SID = ? ORDER BY Start_Date DESC";
        return jdbcTemplate.queryForList(sql, sid);
    }
}
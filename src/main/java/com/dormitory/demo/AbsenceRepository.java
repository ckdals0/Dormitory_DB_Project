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
        String absenceId = "A-" + (int)(Math.random() * 1000000);
        jdbcTemplate.update(sql,
                absenceId,
                sid,
                request.getStartDate(),
                request.getReturnDate(),
                "Pending",
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

    // 3. 승인 대기 목록 상세 조회 (관리자 승인 팝업용 - 전체)
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

    // 4. ★ 상태 변경 (승인/반려 처리) - 관리자 정보 추가
    public void updateStatus(String absenceId, String status, String managerId) {
        String sql = "UPDATE ABSENCE SET Status = ?, Manager_ID = ?, Processed_Date = CURRENT_TIMESTAMP WHERE Absence_ID = ?";
        jdbcTemplate.update(sql, status, managerId, absenceId);
    }

    // 5. ★ 학생별 외박 내역 조회 (관리자 이름 포함)
    public List<Map<String, Object>> findHistoryByStudent(String sid) {
        String sql = """
            SELECT a.*, m.MName AS Manager_Name
            FROM ABSENCE a
            LEFT JOIN MANAGER m ON a.Manager_ID = m.Manager_ID
            WHERE a.SID = ? 
            ORDER BY a.Start_Date DESC
        """;
        return jdbcTemplate.queryForList(sql, sid);
    }
}
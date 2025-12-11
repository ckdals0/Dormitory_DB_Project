package com.dormitory.demo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
public class PenaltyRepository {

    private final JdbcTemplate jdbcTemplate;

    public PenaltyRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 1. 상벌점 저장 (관리자용)
    public void save(PenaltyRequest request, String managerId) {
        String sql = "INSERT INTO PENALTY (Penalty_ID, SID, Manager_ID, Date, Type, Points, Reason) VALUES (?, ?, ?, CURRENT_DATE, ?, ?, ?)";
        String pId = "P-" + (int)(Math.random() * 100000);
        jdbcTemplate.update(sql, pId, request.getStudentId(), managerId, request.getType(), request.getPoints(), request.getReason());
    }

    // 2. 학생별 상벌점 내역 조회 (학생용)
    public List<Map<String, Object>> findHistoryByStudent(String sid) {
        String sql = "SELECT * FROM PENALTY WHERE SID = ? ORDER BY Date DESC";
        return jdbcTemplate.queryForList(sql, sid);
    }
}

package com.dormitory.demo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
public class FacilityRepository {

    private final JdbcTemplate jdbcTemplate;

    public FacilityRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 1. 특정 호실의 시설물 목록 조회
    public List<Map<String, Object>> findFacilitiesByRoom(String roomNo) {
        String sql = "SELECT * FROM FACILITY WHERE Room_No = ?";
        return jdbcTemplate.queryForList(sql, roomNo);
    }

    // 2. 시설물 상태 변경
    public void updateStatus(String facId, String status) {
        String sql = "UPDATE FACILITY SET Status = ? WHERE Fac_ID = ?";
        jdbcTemplate.update(sql, status, facId);
    }
}
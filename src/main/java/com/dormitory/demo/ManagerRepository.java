package com.dormitory.demo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ManagerRepository {

    private final JdbcTemplate jdbcTemplate;

    public ManagerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 로그인용 관리자 조회
    public Manager findByIdAndPassword(String id, String password) {
        String sql = "SELECT * FROM MANAGER WHERE Manager_ID = ? AND Password = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Manager(
                    rs.getString("Manager_ID"),
                    rs.getString("MName"),
                    rs.getString("Phone"),
                    rs.getString("Role")
            ), id, password);
        } catch (Exception e) {
            return null;
        }
    }
}
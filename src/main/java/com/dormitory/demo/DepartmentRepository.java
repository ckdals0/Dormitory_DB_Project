package com.dormitory.demo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class DepartmentRepository {

    private final JdbcTemplate jdbcTemplate;

    public DepartmentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 모든 학과 이름 조회
    public List<String> findAllDeptNames() {
        String sql = "SELECT Dept_Name FROM DEPARTMENT";
        // String 리스트로 반환
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("Dept_Name"));
    }
}
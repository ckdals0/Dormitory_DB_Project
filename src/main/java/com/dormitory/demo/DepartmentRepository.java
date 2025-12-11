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

    public List<String> findAllDeptNames() {
        String sql = "SELECT Dept_Name FROM DEPARTMENT";

        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("Dept_Name"));
    }
}
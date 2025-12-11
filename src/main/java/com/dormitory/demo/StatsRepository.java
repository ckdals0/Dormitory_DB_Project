package com.dormitory.demo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;

@Repository
public class StatsRepository {

    private final JdbcTemplate jdbcTemplate;

    public StatsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public StatsResponse getManagerStats() {
        // 1. 총 거주 학생 수
        String sql1 = "SELECT COUNT(*) FROM STUDENT";
        Integer totalStudents = jdbcTemplate.queryForObject(sql1, Integer.class);

        // 2. 오늘의 외박자 수 (오늘 날짜가 외박 기간에 포함되고 승인된 건)
        // (MySQL의 CURDATE() 사용)
        String sql2 = "SELECT COUNT(*) FROM ABSENCE WHERE Status = 'Approved' AND CURDATE() BETWEEN Start_Date AND Return_Date";
        Integer todayAbsence = jdbcTemplate.queryForObject(sql2, Integer.class);

        // 3. 승인 대기 외박 건수
        String sql3 = "SELECT COUNT(*) FROM ABSENCE WHERE Status = 'Pending'";
        Integer pendingAbsence = jdbcTemplate.queryForObject(sql3, Integer.class);

        // 4. 처리 대기 민원 건수 (아직 COMPLAINT 테이블이 없다면 0으로 처리하거나 테이블 생성 필요)
        // 여기서는 COMPLAINT 테이블이 있다고 가정합니다. 없다면 0으로 리턴하세요.
        String sql4 = "SELECT COUNT(*) FROM COMPLAINT WHERE Status = '접수'";
        Integer pendingComplaints = 0;
        try {
            pendingComplaints = jdbcTemplate.queryForObject(sql4, Integer.class);
        } catch (Exception e) {
            // 테이블이 없거나 에러 시 0
            pendingComplaints = 0;
        }

        return new StatsResponse(
                totalStudents != null ? totalStudents : 0,
                todayAbsence != null ? todayAbsence : 0,
                pendingAbsence != null ? pendingAbsence : 0,
                pendingComplaints != null ? pendingComplaints : 0
        );
    }
}
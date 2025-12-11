package com.dormitory.demo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class StudentRepository {

    private final JdbcTemplate jdbcTemplate;

    public StudentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper 정의
    private final RowMapper<Student> studentRowMapper = (rs, rowNum) -> new Student(
            rs.getString("SID"), rs.getString("Name"), rs.getString("Gender"),
            rs.getString("Dept_Name"), rs.getString("Room_No"), rs.getInt("Total_Penalty")
    );

    // 1. 학생 등록 (SAVE)
    public void save(Student s) {
        String sql = "INSERT INTO STUDENT (SID, Name, Gender, Dept_Name, Room_No, Password, Total_Penalty, Entry_Date, Is_Exit_Requested) VALUES (?, ?, ?, ?, ?, ?, 0, CURDATE(), FALSE)";
        jdbcTemplate.update(sql, s.getSid(), s.getName(), s.getGender(), s.getDeptName(), s.getRoomNo(), "1234");
    }

    // 2. 전체 학생 조회 (FIND ALL)
    public List<Student> findAll() { return jdbcTemplate.query("SELECT * FROM STUDENT", studentRowMapper); }

    // 3. 로그인 인증 (FINDBYIDANDPASSWORD)
    public Student findByIdAndPassword(String id, String pw) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM STUDENT WHERE SID=? AND Password=?", studentRowMapper, id, pw);
        } catch (Exception e) {
            return null;
        }
    }

    // 4. 학생 검색 (SEARCHSTUDENTS)
    public List<Student> searchStudents(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return jdbcTemplate.query("SELECT * FROM STUDENT ORDER BY SID", studentRowMapper);
        String sql = "SELECT * FROM STUDENT WHERE Name LIKE ? OR SID LIKE ? ORDER BY SID";
        String param = "%" + keyword + "%";
        return jdbcTemplate.query(sql, studentRowMapper, param, param);
    }

    // 5. 현재 비밀번호 확인 (CHECKPASSWORD)
    public boolean checkPassword(String id, String pw) {
        String sql = "SELECT COUNT(*) FROM STUDENT WHERE SID = ? AND Password = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id, pw);
        return count != null && count > 0;
    }

    // 6. 비밀번호 업데이트 (UPDATEPASSWORD)
    public boolean updatePassword(String id, String newPassword) {
        String sql = "UPDATE STUDENT SET Password = ? WHERE SID = ?";
        int updatedRows = jdbcTemplate.update(sql, newPassword, id);
        return updatedRows > 0;
    }

    // 7. 누적 상벌점 업데이트 (UPDATEPENALTYPOINTS)
    public void updatePenaltyPoints(String id, int p) {
        String sql = "UPDATE STUDENT SET Total_Penalty = Total_Penalty + ? WHERE SID = ?";
        jdbcTemplate.update(sql, p, id);
    }

    // 8. 기존 방 번호 조회 (FINDROOMNOBYSID)
    public String findRoomNoBySid(String sid) {
        try {
            return jdbcTemplate.queryForObject("SELECT Room_No FROM STUDENT WHERE SID = ?", String.class, sid);
        } catch (Exception e) {
            return null;
        }
    }

    // 9. 학생의 방 정보 업데이트 (UPDATEROOM)
    public void updateRoom(String sid, String newRoomNo) {
        String sql = "UPDATE STUDENT SET Room_No = ? WHERE SID = ?";
        jdbcTemplate.update(sql, newRoomNo, sid);
    }

    // 10. 학생의 퇴소 신청 상태 기록 (REQUESTEXIT)
    public void requestExit(String sid) {
        String sql = "UPDATE STUDENT SET Is_Exit_Requested = TRUE, Exit_Request_Date = CURDATE() WHERE SID = ?";
        jdbcTemplate.update(sql, sid);
    }

    // 11. 학생 삭제 (DELETE)
    public void delete(String sid) {
        jdbcTemplate.update("DELETE FROM STUDENT WHERE SID = ?", sid);
    }

    // 12. 퇴소 신청 목록 조회 (FINDEXITREQUESTS)
    public List<java.util.Map<String, Object>> findExitRequests() {
        String sql = """
            SELECT SID, Name, Dept_Name, Room_No, Exit_Request_Date 
            FROM STUDENT 
            WHERE Is_Exit_Requested = TRUE 
            ORDER BY Exit_Request_Date ASC
        """;
        return jdbcTemplate.queryForList(sql);
    }

    // 13. 현재 누적 상벌점 조회 (FINDTOTALPENALTY)
    public Integer findTotalPenalty(String sid) {
        try {
            String sql = "SELECT Total_Penalty FROM STUDENT WHERE SID = ?";
            return jdbcTemplate.queryForObject(sql, Integer.class, sid);
        } catch (Exception e) {
            return 0;
        }
    }
}
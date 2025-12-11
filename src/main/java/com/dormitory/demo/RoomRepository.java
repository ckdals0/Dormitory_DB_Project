package com.dormitory.demo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
public class RoomRepository {

    private final JdbcTemplate jdbcTemplate;

    public RoomRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 1. 빈 자리가 있는 방 번호 조회
    public List<String> findAvailableRoomNumbers() {
        String sql = "SELECT Room_No FROM ROOM WHERE Current_Occupants < Capacity ORDER BY Room_No";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("Room_No"));
    }

    // 2. 모든 호실 정보 조회
    public List<Map<String, Object>> findAllRooms() {
        String sql = """
            SELECT r.*, 
                   (SELECT COUNT(*) FROM FACILITY f 
                    WHERE f.Room_No = r.Room_No 
                      AND f.Status IN ('고장', '수리중')) AS Issue_Count
            FROM ROOM r
            ORDER BY r.Room_No
        """;
        return jdbcTemplate.queryForList(sql);
    }

    // 3. 특정 호실 학생 목록
    public List<Map<String, Object>> findStudentsByRoom(String roomNo) {
        String sql = "SELECT SID, Name, Phone, Dept_Name FROM STUDENT WHERE Room_No = ?";
        return jdbcTemplate.queryForList(sql, roomNo);
    }

    // 4. 방 인원 증가
    public void increaseOccupancy(String roomNo) {
        String sql = "UPDATE ROOM SET Current_Occupants = Current_Occupants + 1 WHERE Room_No = ?";
        jdbcTemplate.update(sql, roomNo);
    }

    // 5. 방 인원 감소
    public void decreaseOccupancy(String roomNo) {
        String sql = "UPDATE ROOM SET Current_Occupants = Current_Occupants - 1 WHERE Room_No = ?";
        jdbcTemplate.update(sql, roomNo);
    }
}
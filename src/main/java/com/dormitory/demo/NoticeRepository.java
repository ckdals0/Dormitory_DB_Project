package com.dormitory.demo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class NoticeRepository {

    private final JdbcTemplate jdbcTemplate;

    public NoticeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 1. 공지사항 목록 조회
    public List<Notice> findAll() {
        String sql = "SELECT * FROM NOTICE ORDER BY Notice_ID DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Notice(
                rs.getInt("Notice_ID"),
                rs.getString("Title"),
                rs.getString("Content"),
                rs.getString("Writer"),
                rs.getString("Reg_Date")
        ));
    }

    // 2. 공지사항 등록
    public void save(Notice notice) {
        String sql = "INSERT INTO NOTICE (Title, Content, Writer) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, notice.getTitle(), notice.getContent(), notice.getWriter());
    }

    // 3. 공지사항 삭제
    public void delete(int noticeId) {
        String sql = "DELETE FROM NOTICE WHERE Notice_ID = ?";
        jdbcTemplate.update(sql, noticeId);
    }
}
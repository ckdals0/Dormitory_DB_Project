package com.dormitory.demo;

public class Notice {
    private int noticeId;
    private String title;
    private String content;
    private String writer;
    private String regDate;

    // 생성자
    public Notice() {} // 기본 생성자 (JSON 변환용)

    public Notice(int noticeId, String title, String content, String writer, String regDate) {
        this.noticeId = noticeId;
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.regDate = regDate;
    }

    // Getter, Setter
    public int getNoticeId() { return noticeId; }
    public void setNoticeId(int noticeId) { this.noticeId = noticeId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getWriter() { return writer; }
    public void setWriter(String writer) { this.writer = writer; }
    public String getRegDate() { return regDate; }
    public void setRegDate(String regDate) { this.regDate = regDate; }
}
package com.dormitory.demo;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notices")
public class NoticeController {

    private final NoticeRepository noticeRepository;

    public NoticeController(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    // 목록 조회
    @GetMapping
    public List<Notice> getNotices() {
        return noticeRepository.findAll();
    }

    // 등록
    @PostMapping
    public String createNotice(@RequestBody Notice notice) {
        noticeRepository.save(notice);
        return "공지사항 등록 성공";
    }

    // 삭제
    @DeleteMapping("/{noticeId}")
    public String deleteNotice(@PathVariable int noticeId) {
        noticeRepository.delete(noticeId);
        return "공지사항 삭제 성공";
    }
}
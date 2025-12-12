package com.dormitory.demo;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class PenaltyController {

    private final PenaltyRepository penaltyRepository;
    private final StudentRepository studentRepository;

    public PenaltyController(PenaltyRepository penaltyRepository, StudentRepository studentRepository) {
        this.penaltyRepository = penaltyRepository;
        this.studentRepository = studentRepository;
    }

    // 1. 학생용: 나의 상벌점 내역 및 총점 조회
    @GetMapping("/api/penalty/history")
    public Map<String, Object> getMyPenaltyHistory(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        Student user = (Student) session.getAttribute("user");
        if (user == null) return null;

        List<Map<String, Object>> history = penaltyRepository.findHistoryByStudent(user.getSid());

        Integer currentTotal = studentRepository.findTotalPenalty(user.getSid());

        response.put("totalPenalty", currentTotal != null ? currentTotal : 0);
        response.put("history", history);

        return response;
    }

    // 2. 관리자용: 학생 검색 API
    @GetMapping("/api/manager/students/search")
    public List<Student> searchStudents(@RequestParam String keyword) {
        return studentRepository.searchStudents(keyword);
    }
}
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

        // (1) 내역 조회
        List<Map<String, Object>> history = penaltyRepository.findHistoryByStudent(user.getSid());

        // (2) 총점 조회
        Integer currentTotal = studentRepository.findTotalPenalty(user.getSid());

        response.put("totalPenalty", currentTotal != null ? currentTotal : 0);
        response.put("history", history);

        return response;
    }

    // 2. 관리자용: 학생 검색 API (이름 또는 학번)
    // (이 기능은 PenaltyController에 남겨두어 ManagerController의 비대화를 막습니다.)
    @GetMapping("/api/manager/students/search")
    public List<Student> searchStudents(@RequestParam String keyword) {
        return studentRepository.searchStudents(keyword);
    }

    // [삭제됨] givePenalty 메서드는 ManagerController로 통합되었으므로 여기서 제거합니다.
    // @PostMapping("/api/manager/penalty/give") ...
}
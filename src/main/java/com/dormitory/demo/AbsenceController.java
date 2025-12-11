package com.dormitory.demo;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AbsenceController {

    private final AbsenceRepository absenceRepository;

    public AbsenceController(AbsenceRepository absenceRepository) {
        this.absenceRepository = absenceRepository;
    }

    // 1. 외박 신청 API
    @PostMapping("/api/absence/apply")
    public Map<String, Object> applyAbsence(@RequestBody AbsenceRequest request, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        Student user = (Student) session.getAttribute("user");
        if (user == null) {
            response.put("success", false);
            response.put("message", "로그인이 필요합니다.");
            return response;
        }

        try {
            absenceRepository.save(user.getSid(), request);
            response.put("success", true);
            response.put("message", "외박 신청이 완료되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "신청 중 오류가 발생했습니다. (날짜 등을 확인하세요)");
        }

        return response;
    }

    // 2. 나의 외박 신청 내역 조회 API
    @GetMapping("/api/absence/history")
    public List<Map<String, Object>> getMyHistory(HttpSession session) {
        Student user = (Student) session.getAttribute("user");
        if (user == null) return null;

        return absenceRepository.findHistoryByStudent(user.getSid());
    }
}
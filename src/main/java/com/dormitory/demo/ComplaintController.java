package com.dormitory.demo;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class ComplaintController {

    private final ComplaintRepository complaintRepository;
    private final FacilityRepository facilityRepository;

    public ComplaintController(ComplaintRepository complaintRepository, FacilityRepository facilityRepository) {
        this.complaintRepository = complaintRepository;
        this.facilityRepository = facilityRepository;
    }

    // 1. 수리 요청 접수 API
    @PostMapping("/api/complaint/apply")
    public Map<String, Object> applyComplaint(@RequestBody ComplaintRequest request, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // 로그인 확인
        Student user = (Student) session.getAttribute("user");
        if (user == null) {
            response.put("success", false);
            response.put("message", "로그인이 필요합니다.");
            return response;
        }

        try {
            // 1: 시설물 ID가 있다면 상태를 '고장'으로 변경
            if (request.getFacilityId() != null && !request.getFacilityId().isEmpty()) {
                facilityRepository.updateStatus(request.getFacilityId(), "고장");
            }

            // 2: 민원 내용에 시설물 ID 정보 추가 (선택 사항)
            String finalContent = request.getContent();
            if (request.getFacilityId() != null && !request.getFacilityId().isEmpty()) {
                finalContent = "[" + request.getFacilityId() + "] " + finalContent;
            }

            // 민원 저장 실행
            complaintRepository.save(user.getSid(), user.getRoomNo(), finalContent);

            response.put("success", true);
            response.put("message", "수리 요청이 접수되었으며, 해당 시설물이 '고장' 처리되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "요청 중 오류가 발생했습니다.");
        }

        return response;
    }

    // 2. 나의 민원/수리 내역 조회 API
    @GetMapping("/api/complaint/history")
    public List<Map<String, Object>> getMyComplaintHistory(HttpSession session) {
        Student user = (Student) session.getAttribute("user");
        if (user == null) return null;

        return complaintRepository.findHistoryByStudent(user.getSid());
    }
}
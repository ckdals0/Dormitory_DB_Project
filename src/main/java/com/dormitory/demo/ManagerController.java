package com.dormitory.demo;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
public class ManagerController {

    private final StatsRepository statsRepository;
    private final AbsenceRepository absenceRepository;
    private final ComplaintRepository complaintRepository;
    private final StudentRepository studentRepository;
    private final RoomRepository roomRepository;
    private final PenaltyRepository penaltyRepository;

    public ManagerController(StatsRepository statsRepository,
                             AbsenceRepository absenceRepository,
                             ComplaintRepository complaintRepository,
                             StudentRepository studentRepository,
                             RoomRepository roomRepository,
                             PenaltyRepository penaltyRepository) {
        this.statsRepository = statsRepository;
        this.absenceRepository = absenceRepository;
        this.complaintRepository = complaintRepository;
        this.studentRepository = studentRepository;
        this.roomRepository = roomRepository;
        this.penaltyRepository = penaltyRepository;
    }

    // ★ 세션에서 관리자 ID 가져오는 헬퍼 메서드
    private String getManagerId(HttpSession session) {
        Manager manager = (Manager) session.getAttribute("user");
        return manager != null ? manager.getManagerId() : null;
    }

    // 1. 대시보드 통계 및 대기 목록 API
    @GetMapping("/api/manager/stats")
    public StatsResponse getStats(HttpSession session) {
        return statsRepository.getManagerStats();
    }

    @GetMapping("/api/manager/pending-items")
    public List<PendingItem> getPendingItems() {
        return absenceRepository.findPendingItems();
    }

    // 2. 외박 관리 API
    @GetMapping("/api/manager/absence/pending")
    public List<Map<String, Object>> getPendingAbsences() {
        return absenceRepository.findAllPendingDetailed();
    }

    // ★ 외박 승인/반려 처리 (관리자 정보 추가)
    @PostMapping("/api/manager/absence/approval")
    public String approveAbsence(@RequestBody ApprovalRequest request, HttpSession session) {
        String managerId = getManagerId(session);
        absenceRepository.updateStatus(request.getId(), request.getStatus(), managerId);
        return "처리되었습니다.";
    }

    // 3. 시설/민원 관리 API
    @GetMapping("/api/manager/complaints")
    public List<Map<String, Object>> getComplaints() {
        return complaintRepository.findAllDetailed();
    }

    // ★ 민원 상태 변경 (관리자 정보 추가)
    @PostMapping("/api/manager/complaint/status")
    public String updateComplaintStatus(@RequestBody Map<String, String> payload, HttpSession session) {
        String managerId = getManagerId(session);
        complaintRepository.updateStatus(payload.get("id"), payload.get("status"), managerId);
        return "상태가 변경되었습니다.";
    }

    // 4. 학생 관리 API
    @PostMapping("/api/manager/penalty/give")
    public String givePenalty(@RequestBody PenaltyRequest request, HttpSession session) {
        String managerId = getManagerId(session);

        try {
            int pointsToApply = request.getPoints();
            if ("벌점".equals(request.getType())) {
                pointsToApply = -pointsToApply;
            }

            penaltyRepository.save(request, managerId);
            studentRepository.updatePenaltyPoints(request.getStudentId(), pointsToApply);

            return "처리가 완료되었습니다.";
        } catch (Exception e) {
            e.printStackTrace();
            return "오류가 발생했습니다.";
        }
    }

    @PostMapping("/api/manager/student/move")
    public String moveStudent(@RequestBody Map<String, String> payload) {
        String sid = payload.get("sid");
        String newRoom = payload.get("newRoom");

        try {
            String oldRoom = studentRepository.findRoomNoBySid(sid);
            if (oldRoom != null && !oldRoom.isEmpty()) {
                roomRepository.decreaseOccupancy(oldRoom);
            }

            studentRepository.updateRoom(sid, newRoom);
            roomRepository.increaseOccupancy(newRoom);

            return "호실 이동이 완료되었습니다.";
        } catch (Exception e) {
            e.printStackTrace();
            return "호실 이동 중 오류가 발생했습니다. (방 인원수 확인)";
        }
    }

    @PostMapping("/api/manager/student/exit/approve")
    public String approveExit(@RequestBody Map<String, String> payload) {
        String sid = payload.get("sid");

        try {
            String oldRoom = studentRepository.findRoomNoBySid(sid);
            if (oldRoom != null && !oldRoom.isEmpty()) {
                roomRepository.decreaseOccupancy(oldRoom);
            }

            studentRepository.delete(sid);

            return "퇴소 처리가 완료되었습니다.";
        } catch (Exception e) {
            e.printStackTrace();
            return "퇴소 처리 중 오류가 발생했습니다.";
        }
    }

    @GetMapping("/api/manager/exit/requests")
    public List<Map<String, Object>> getExitRequests() {
        return studentRepository.findExitRequests();
    }
}
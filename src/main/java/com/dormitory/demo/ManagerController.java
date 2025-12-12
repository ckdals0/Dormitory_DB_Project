package com.dormitory.demo;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Comparator;

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

    private String getManagerId(HttpSession session) {
        Object user = session.getAttribute("user");
        if (user instanceof Manager) {
            return ((Manager) user).getManagerId();
        }
        return null;
    }

    // 1. 대시보드 통계 및 대기 목록 API
    @GetMapping("/api/manager/stats")
    public StatsResponse getStats(HttpSession session) {
        return statsRepository.getManagerStats();
    }

    @GetMapping("/api/manager/pending-items")
    public List<PendingItem> getPendingItems() {
        List<PendingItem> absenceList = absenceRepository.findPendingItems();
        List<PendingItem> complaintList = complaintRepository.findPendingComplaints();

        List<PendingItem> combinedList = new ArrayList<>();
        combinedList.addAll(absenceList);
        combinedList.addAll(complaintList);
        return combinedList;
    }

    // 2. 외박 관리 API
    @GetMapping("/api/manager/absence/pending")
    public List<Map<String, Object>> getPendingAbsences() {
        return absenceRepository.findAllPendingDetailed();
    }

    @PostMapping("/api/manager/absence/approval")
    public Map<String, Object> approveAbsence(@RequestBody ApprovalRequest request, HttpSession session) {
        String managerId = getManagerId(session);
        if (managerId == null) {
            return Map.of("success", false, "message", "관리자 세션 정보가 없습니다.");
        }

        try {
            absenceRepository.updateStatus(request.getId(), request.getStatus(), managerId);
            return Map.of("success", true, "message", "처리되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("success", false, "message", "DB 처리 중 오류가 발생했습니다.");
        }
    }

    // 3. 시설/민원 관리 API
    @GetMapping("/api/manager/complaints")
    public List<Map<String, Object>> getComplaints() {
        return complaintRepository.findAllDetailed();
    }

    @PostMapping("/api/manager/complaint/status")
    public Map<String, Object> updateComplaintStatus(@RequestBody Map<String, String> payload, HttpSession session) {
        String managerId = getManagerId(session);

        if (managerId == null) {
            return Map.of("success", false, "message", "로그인 세션이 만료되었거나 관리자 정보가 없습니다.");
        }

        try {
            complaintRepository.updateStatus(payload.get("id"), payload.get("status"), managerId);

            return Map.of("success", true, "message", "상태가 변경되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("success", false, "message", "처리 중 데이터베이스 오류가 발생했습니다. (관리자 ID 또는 데이터 제약 확인)");
        }
    }

    // 4. 학생 관리 API
    @PostMapping("/api/manager/penalty/give")
    public Map<String, Object> givePenalty(@RequestBody PenaltyRequest request, HttpSession session) {
        String managerId = getManagerId(session);

        if (managerId == null) {
            return Map.of("success", false, "message", "오류: 관리자 세션 정보가 없습니다.");
        }

        try {
            int pointsToApply = request.getPoints();
            String type = request.getType();

            if ("벌점".equals(type)) {
                if (pointsToApply > 0) {
                    pointsToApply = -pointsToApply;
                } else {
                    pointsToApply = 0;
                }
            }

            penaltyRepository.save(request, managerId);

            studentRepository.updatePenaltyPoints(request.getStudentId(), pointsToApply);

            return Map.of("success", true, "message", "상벌점 처리가 완료되었으며 누적 점수가 반영되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("success", false, "message", "상벌점 부여 중 DB 오류가 발생했습니다. (콘솔 확인)");
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
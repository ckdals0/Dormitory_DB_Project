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

    // 생성자 주입
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

    // ==========================================
    // 1. 대시보드 통계 및 대기 목록 API (현황판용)
    // ==========================================

    // ★ 현황판 통계 조회
    @GetMapping("/api/manager/stats")
    public StatsResponse getStats(HttpSession session) {
        return statsRepository.getManagerStats();
    }

    // 처리 대기 목록 조회
    @GetMapping("/api/manager/pending-items")
    public List<PendingItem> getPendingItems() {
        return absenceRepository.findPendingItems();
    }

    // ==========================================
    // 2. 외박 관리 API
    // ==========================================

    // 외박 승인 대기 목록 상세 조회
    @GetMapping("/api/manager/absence/pending")
    public List<Map<String, Object>> getPendingAbsences() {
        return absenceRepository.findAllPendingDetailed();
    }

    // 외박 승인/반려 처리
    @PostMapping("/api/manager/absence/approval")
    public String approveAbsence(@RequestBody ApprovalRequest request) {
        absenceRepository.updateStatus(request.getId(), request.getStatus());
        return "처리되었습니다.";
    }

    // ==========================================
    // 3. 시설/민원 관리 API
    // ==========================================

    // 민원 전체 목록 조회
    @GetMapping("/api/manager/complaints")
    public List<Map<String, Object>> getComplaints() {
        return complaintRepository.findAllDetailed();
    }

    // 민원 상태 변경
    @PostMapping("/api/manager/complaint/status")
    public String updateComplaintStatus(@RequestBody Map<String, String> payload) {
        complaintRepository.updateStatus(payload.get("id"), payload.get("status"));
        return "상태가 변경되었습니다.";
    }

    // ==========================================
    // 4. 학생 관리 API (상벌점, 이동, 퇴소)
    // ==========================================

    // 상벌점 부여 (PenaltyController 기능을 통합 사용)
    @PostMapping("/api/manager/penalty/give")
    public String givePenalty(@RequestBody PenaltyRequest request) {
        String managerId = "9001"; // 임시 관리자 ID

        try {
            // (1) 벌점 로직 수행 (상점+, 벌점-로 누적)
            int pointsToApply = request.getPoints();
            if ("벌점".equals(request.getType())) {
                pointsToApply = -pointsToApply;
            }

            // (2) PenaltyRepository에 기록 저장
            penaltyRepository.save(request, managerId);

            // (3) Student Total Penalty 업데이트
            studentRepository.updatePenaltyPoints(request.getStudentId(), pointsToApply);

            return "처리가 완료되었습니다.";
        } catch (Exception e) {
            e.printStackTrace();
            return "오류가 발생했습니다.";
        }
    }

    // 학생 호실 이동
    @PostMapping("/api/manager/student/move")
    public String moveStudent(@RequestBody Map<String, String> payload) {
        String sid = payload.get("sid");
        String newRoom = payload.get("newRoom");

        try {
            // 1. 기존 방 인원 감소
            String oldRoom = studentRepository.findRoomNoBySid(sid);
            if (oldRoom != null && !oldRoom.isEmpty()) {
                roomRepository.decreaseOccupancy(oldRoom);
            }

            // 2. 학생 정보 업데이트 (방 변경)
            studentRepository.updateRoom(sid, newRoom);

            // 3. 새 방 인원 증가
            roomRepository.increaseOccupancy(newRoom);

            return "호실 이동이 완료되었습니다.";
        } catch (Exception e) {
            e.printStackTrace();
            return "호실 이동 중 오류가 발생했습니다. (방 인원수 확인)";
        }
    }

    // 학생 퇴소 승인 (최종 삭제)
    @PostMapping("/api/manager/student/exit/approve")
    public String approveExit(@RequestBody Map<String, String> payload) {
        String sid = payload.get("sid");

        try {
            // 1. 기존 방 인원 감소
            String oldRoom = studentRepository.findRoomNoBySid(sid);
            if (oldRoom != null && !oldRoom.isEmpty()) {
                roomRepository.decreaseOccupancy(oldRoom);
            }

            // 2. 학생 삭제 (FK CASCADE 설정으로 관련 기록도 삭제됨)
            studentRepository.delete(sid);

            return "퇴소 처리가 완료되었습니다.";
        } catch (Exception e) {
            e.printStackTrace();
            return "퇴소 처리 중 오류가 발생했습니다.";
        }
    }

    // 퇴소 신청 목록 조회
    @GetMapping("/api/manager/exit/requests")
    public List<Map<String, Object>> getExitRequests() {
        return studentRepository.findExitRequests();
    }
}
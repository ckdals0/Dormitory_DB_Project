package com.dormitory.demo;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentRepository studentRepository;
    private final RoomRepository roomRepository;

    public StudentController(StudentRepository studentRepository, RoomRepository roomRepository) {
        this.studentRepository = studentRepository;
        this.roomRepository = roomRepository;
    }

    // 1. 학생 등록
    @PostMapping
    public String registerStudent(@RequestBody Student student) {
        studentRepository.save(student);
        roomRepository.increaseOccupancy(student.getRoomNo());
        return "학생 등록 성공: " + student.getName();
    }

    // 2. 학생 목록 조회
    @GetMapping
    public List<Student> getStudents(@RequestParam(value = "keyword", required = false) String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return studentRepository.searchStudents(keyword);
        }
        return studentRepository.findAll();
    }

    // 3. 비밀번호 변경
    @PostMapping("/change-password")
    public Map<String, Object> changePassword(@RequestBody ChangePasswordRequest request, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        Student user = (Student) session.getAttribute("user");
        if (user == null) {
            response.put("success", false);
            response.put("message", "로그인이 필요합니다.");
            return response;
        }

        if (!studentRepository.checkPassword(user.getSid(), request.getCurrentPassword())) {
            response.put("success", false);
            response.put("message", "현재 비밀번호가 일치하지 않습니다.");
            return response;
        }

        boolean result = studentRepository.updatePassword(user.getSid(), request.getNewPassword());

        if (result) {
            response.put("success", true);
            response.put("message", "비밀번호가 성공적으로 변경되었습니다.");
        } else {
            response.put("success", false);
            response.put("message", "비밀번호 변경 중 오류가 발생했습니다.");
        }

        return response;
    }

    // 4. 학생 즉시 퇴소
    @PostMapping("/request-exit")
    public Map<String, Object> requestExit(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Student user = (Student) session.getAttribute("user");

        if (user == null) {
            response.put("success", false);
            response.put("message", "로그인이 필요합니다.");
            return response;
        }

        try {
            // (1) 현재 살고 있는 방 찾기
            String oldRoom = studentRepository.findRoomNoBySid(user.getSid());

            // (2) 방 인원수 감소
            if (oldRoom != null) {
                roomRepository.decreaseOccupancy(oldRoom);
            }

            // (3) 학생 데이터 삭제
            studentRepository.delete(user.getSid());

            // (4) 세션 만료
            session.invalidate();

            response.put("success", true);
            response.put("message", "퇴소 처리가 완료되었습니다. 이용해 주셔서 감사합니다.");
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "퇴소 처리 중 오류가 발생했습니다.");
        }
        return response;
    }
}
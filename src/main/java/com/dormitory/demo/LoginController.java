package com.dormitory.demo;

import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoginController {

    private final StudentRepository studentRepository;
    private final ManagerRepository managerRepository;

    public LoginController(StudentRepository studentRepository, ManagerRepository managerRepository) {
        this.studentRepository = studentRepository;
        this.managerRepository = managerRepository;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest request, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        String selectedRole = request.getRole(); // 프론트에서 선택한 역할 (STUDENT 또는 MANAGER)

        // 1. "학생"을 선택하고 로그인한 경우
        if ("STUDENT".equals(selectedRole)) {
            // ★ 수정됨: 비밀번호로 조회하는 메서드 호출
            Student student = studentRepository.findByIdAndPassword(request.getId(), request.getPassword());
            if (student != null) {
                session.setAttribute("user", student);
                session.setAttribute("role", "STUDENT");

                response.put("success", true);
                response.put("role", "STUDENT");
                response.put("name", student.getName());
                return response;
            }
        }
        // 2. "관리자"를 선택하고 로그인한 경우
        else if ("MANAGER".equals(selectedRole)) {
            // ★ 수정됨: 비밀번호로 조회하는 메서드 호출
            Manager manager = managerRepository.findByIdAndPassword(request.getId(), request.getPassword());
            if (manager != null) {
                session.setAttribute("user", manager);
                session.setAttribute("role", "MANAGER");

                response.put("success", true);
                response.put("role", "MANAGER");
                response.put("name", manager.getMName());
                return response;
            }
        }

        // 3. 로그인 실패 (정보 불일치 또는 역할 오류)
        response.put("success", false);
        response.put("message", "아이디 또는 비밀번호가 일치하지 않습니다.");
        return response;
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 세션 전체 삭제 (로그아웃)
        return "로그아웃 되었습니다.";
    }

    // 현재 로그인된 사용자 정보 확인 (세션 체크 및 보안 유지)
    @GetMapping("/current-user")
    public Map<String, Object> getCurrentUser(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        String role = (String) session.getAttribute("role");

        if (role != null) {
            response.put("loggedIn", true);
            response.put("role", role);
            // 중요: 보안상 민감한 정보(비밀번호 등)는 제외하고 세션에 저장된 객체를 반환합니다.
            response.put("user", session.getAttribute("user"));
        } else {
            response.put("loggedIn", false);
        }
        return response;
    }
}
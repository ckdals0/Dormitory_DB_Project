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
        String selectedRole = request.getRole();

        // 1. "학생"을 선택하고 로그인한 경우
        if ("STUDENT".equals(selectedRole)) {
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

        // 3. 로그인 실패
        response.put("success", false);
        response.put("message", "아이디 또는 비밀번호가 일치하지 않습니다.");
        return response;
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "로그아웃 되었습니다.";
    }

    // 현재 로그인된 사용자 정보 확인
    @GetMapping("/current-user")
    public Map<String, Object> getCurrentUser(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        String role = (String) session.getAttribute("role");

        if (role != null) {
            response.put("loggedIn", true);
            response.put("role", role);
            response.put("user", session.getAttribute("user"));
        } else {
            response.put("loggedIn", false);
        }
        return response;
    }
}
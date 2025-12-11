package com.dormitory.demo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        String requestURI = request.getRequestURI(); // 사용자가 접속하려는 주소

        // 1. 세션 확인 (로그인 여부)
        Object user = session.getAttribute("user");
        if (user == null) {
            // 로그인 안 했으면 로그인 페이지로 쫓아냄
            response.sendRedirect("/login.html");
            return false; // 요청 차단
        }

        // 2. 관리자 페이지 접근 권한 확인
        String role = (String) session.getAttribute("role");
        if (requestURI.startsWith("/manager_") && !"MANAGER".equals(role)) {
            // 관리자 페이지인데 학생이 들어오려고 하면 차단
            response.sendRedirect("/login.html"); // 또는 "권한 없음" 페이지
            return false;
        }

        // 3. 학생 페이지 접근 권한 확인 (선택 사항)
        if (requestURI.startsWith("/student_") && !"STUDENT".equals(role)) {
            response.sendRedirect("/login.html");
            return false;
        }

        return true; // 통과
    }
}
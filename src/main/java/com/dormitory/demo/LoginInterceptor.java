package com.dormitory.demo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        String requestURI = request.getRequestURI();

        // 1. 세션 확인
        Object user = session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("/login.html");
            return false;
        }

        // 2. 관리자 페이지 접근 권한 확인
        String role = (String) session.getAttribute("role");
        if (requestURI.startsWith("/manager_") && !"MANAGER".equals(role)) {
            response.sendRedirect("/login.html");
            return false;
        }

        // 3. 학생 페이지 접근 권한 확인
        if (requestURI.startsWith("/student_") && !"STUDENT".equals(role)) {
            response.sendRedirect("/login.html");
            return false;
        }

        return true;
    }
}
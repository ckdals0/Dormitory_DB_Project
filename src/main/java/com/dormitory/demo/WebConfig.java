package com.dormitory.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**") // 모든 경로에 대해 검사
                .excludePathPatterns(   // 검사 제외할 경로들 (로그인 안 해도 되는 곳)
                        "/login.html",
                        "/api/login",
                        "/api/logout",
                        "/api/notices", // ★ 추가됨: 공지사항 목록은 로그인 없이도 조회 가능해야 함
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/error"
                );
    }
}
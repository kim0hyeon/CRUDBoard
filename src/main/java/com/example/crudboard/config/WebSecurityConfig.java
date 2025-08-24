package com.example.crudboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;


@Configuration  // 스프링 설정 클래스임을 알린다.
@EnableWebSecurity  // Spring Security를 활성화하고 웹 보안 설정을 구성할 수 있도록 한다.
public class WebSecurityConfig {
    // HTTP 보안 설정을 구성하는 Bean
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // CSRF 보호를 비활성화 (REST API에서는 일반적으로 토큰 기반 인증을 사용하므로 비활성화)
                .authorizeHttpRequests(auth -> auth
                        // 회원가입 및 로그인 API는 인증 없이도 접근을 허용한다.
                        .requestMatchers(
                                "/api/users/signup",  // 회원가입 경로 허용
                                "/api/users/login",  // 로그인 경로 허용
                                "/api/boards/**",  // api/boards 및 그 하위 모든 경로 허용
                                "/api/posts",  // 모든 게시글 목록 조회 및 허용
                                "/api/posts/search",  // 게시글 검색 허용
                                "/api/posts/{postId}",  // 특정 게시글 조회 허용
                                "api/comments/**"
                        ).permitAll()
                        // 그 외 모든 요청은 인증된 사용자만 접근을 허용한다.
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}

// 추후 PUT(수정), DELETE(삭제) API는 인증된 사용자만 접근할 수 있도록 설정 필요 - 현재는 기능 테스트 중으로 모두 허용
package com.example.crudboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration  // 이 클래스가 스프링 설정(configuration) 클래스임을 알린다.
public class SecurityConfig {

    // PasswordEncoder 인터페이스의 구현체(BCryptPasswordEncoder)를 스프링 빈으로 등록
    // 이렇게 등록하면 다른 서비스에서 PasswordEncoder를 주입해 사용할 수 있음
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCryptPasswordEncoder는 비밀번호를 안전하게 암호화하고 검증하는 데 사용되는 강력한 해시 함수
        return new BCryptPasswordEncoder();
    }
}

package com.example.crudboard.repository;

import com.example.crudboard.domain.User; // User 엔티티 클래스 import
import org.springframework.data.jpa.repository.JpaRepository; // JpaRepository import
import java.util.Optional; // Optional 클래스 import

// UserRepository 인터페이스는 JpaRepository를 상속받아 User 엔티티와 Long 타입의 ID를 관리합니다.
public interface UserRepository extends JpaRepository<User, Long> {

    // 사용자 이름(user_name)으로 User 엔티티를 찾는 메서드를 정의합니다.
    // 로그인 기능 구현 시 사용됩니다.
    Optional<User> findByUsername(String user_name);

    // 닉네임으로 User 엔티티를 찾는 메서드 정의
    // 회원가입 시 닉네임 중복 확인에 사용
    Optional<User> findByNickname(String nickname);
}
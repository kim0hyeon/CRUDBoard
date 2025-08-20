package com.example.crudboard.domain;  // 이 클래스가 속한 패키지를 선언
import jakarta.persistence.*;  // JPA 관련 기능을 사용하기 위해 필요한 어노테이션(@Entity, @Id 등)을 가져옴

// Lombok 라이브러리에서 제공하는 어노테이션, getter, setter 메서드와 기본 생성자를 자동으로 만들어주어 코드를 간결하게 만듦
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;  // 날짜와 시간을 다루는 자바의 표준 클래스를 가져옴

@Entity  // 이 클래스가 JPA의 Entity임을 선언함, 이 어노테이션을 보고 Spring Boot는 이 클래스를 DB의 테이블과 연결함
@Table(name = "user")  // DB에서 테이블 이름은 'user'이다. 이름이 다르니까 매핑을 명시해야 한다.
@Getter  // userId, userName 등 모든 필드의 값을 가져오는 메서드를 자동으로 생성
@Setter  // 각 필드의 값을 설정하는 메서드를 자동으로 생성
@NoArgsConstructor  // 인자 없는 기본 생성자 public User() {}를 자동으로 생성, JPA가 엔티티 객체를 만들 때 이 생성자가 필요
public class User {
    @Id  // 기본 키임을 알리는 어노테이션
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // userId의 값이 DB에서 자동으로 1씩 증가하며 생성되도록 설정, AUTO_INCREMENT와 같은 역할
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_name", nullable = false, unique = true, length = 20)
    private String userName;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, unique = true, length = 20)
    private String nickname;

    @Column(name = "created_at", nullable = false)  // Default값은 DB에서 설정
    private LocalDateTime createdAt;

    @Column(name = "is_dangerous", nullable = false)  // Default값은 DB에서 설정
    private boolean isDangerous;

    // 기본 생성자
    public User(String user_name, String password, String nickname){
        this.userName = user_name;
        this.password = password;
        this.nickname = nickname;
        this.createdAt = LocalDateTime.now();
        this.isDangerous = false;
    }

    // 비밀번호 업데이트 메서드
    public void updatePassword(String new_password){
        this.password = new_password;
    }

    // 일반사용자 -> 위험사용자, 위험사용자 -> 일반사용자 변환 메서드
    public void setDangerous(boolean dangerous){
        this.isDangerous = dangerous;
    }
}


package com.example.crudboard.domain;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "post")
@Getter
@Setter
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long post_id;

    // 자바에선 외래키가 필요할 땐, 외래키 값만 아니라 객체 전체를 불러와서 사용
    // JPA의 핵심적인 장점이자 객체 지향적인 접근 방식
    // fetch = FetchType.LAZY: 필요할 때(Board 객체에 접근할 때) 로딩 (성능 최적화)
    // 여러 Post는 하나의 Board에 속함 (다대일 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    // 여러 Post는 하나의 User가 작성 (다대일 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length=60)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length=100)
    private String image_url;

    @Column(nullable = false)
    private int like_count;

    @Column(nullable = false)
    private int hate_count;

    @Column(nullable = false)
    private int view_count;

    @Column(nullable = false)
    private boolean is_dangerous;

    @Column(nullable = false)
    private LocalDateTime created_at;

    @Column(nullable = false)
    private LocalDateTime updated_at;

    public Post(Board board, User user, String title, String content, String image_url){
        this.board = board;
        this.user = user;
        this.title = title;
        this.content = content;
        this.image_url = image_url;
        this.like_count = 0;
        this.hate_count = 0;
        this.view_count = 0;
        this.is_dangerous = false;
        this.created_at = LocalDateTime.now();
        this.updated_at = LocalDateTime.now();
    }

    public Post(Board board, User user, String title, String content){
        this.board = board;
        this.user = user;
        this.title = title;
        this.content = content;
        this.like_count = 0;
        this.hate_count = 0;
        this.view_count = 0;
        this.is_dangerous = false;
        this.created_at = LocalDateTime.now();
        this.updated_at = LocalDateTime.now();
    }

    // Post를 업데이트 할 때 사용할 메서드
    public void updatePost(String new_title, String new_content, String new_image_url){
        this.title = new_title;
        this.content = new_content;
        this.image_url = new_image_url;
        this.updated_at = LocalDateTime.now(); // 수정 시간 업데이트
    }

    // 변경할 이미지가 없을 때 Post 업데이트
    public void updatePost(String new_title, String new_content){
        this.title = new_title;
        this.content = new_content;
        this.updated_at = LocalDateTime.now();
    }

    public void addLike(){
        this.like_count++;
    }

    // Like를 눌렀던 사용자만 가능하게끔 Service에서 메서드 설정 필요
    public void removeLike(){
        this.like_count--;
    }

    public void incrementViewCount(){
        this.view_count++;
    }

    public void addHate(){
        this.hate_count++;

        if (this.hate_count >= 10){
            this.is_dangerous = true;
        }
    }

    // Hate를 눌렀던 사용자만 가능하게끔 Service에서 메서드 설정 필요
    public void removeHate(){
        this.hate_count--;

        if (this.hate_count < 10){
            this.is_dangerous = false;
        }
    }
}

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
    @Column(name = "post_id")
    private Long postId;

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

    @Column(name = "image_url", length=100)
    private String imageUrl;

    @Column(name = "like_count", nullable = false)
    private int likeCount;

    @Column(name = "hate_count", nullable = false)
    private int hateCount;

    @Column(name = "view_count", nullable = false)
    private int viewCount;

    @Column(name = "is_dangerous", nullable = false)
    private boolean isDangerous;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Post(Board board, User user, String title, String content, String image_url){
        this.board = board;
        this.user = user;
        this.title = title;
        this.content = content;
        this.imageUrl = image_url;
        this.likeCount = 0;
        this.hateCount = 0;
        this.viewCount = 0;
        this.isDangerous = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Post(Board board, User user, String title, String content){
        this.board = board;
        this.user = user;
        this.title = title;
        this.content = content;
        this.likeCount = 0;
        this.hateCount = 0;
        this.viewCount = 0;
        this.isDangerous = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Post를 업데이트 할 때 사용할 메서드
    public void updatePost(String new_title, String new_content, String new_image_url){
        this.title = new_title;
        this.content = new_content;
        this.imageUrl = new_image_url;
        this.updatedAt = LocalDateTime.now(); // 수정 시간 업데이트
    }

    // 변경할 이미지가 없을 때 Post 업데이트
    public void updatePost(String new_title, String new_content){
        this.title = new_title;
        this.content = new_content;
        this.updatedAt = LocalDateTime.now();
    }

    public void addLike(){
        this.likeCount++;
    }

    // Like를 눌렀던 사용자만 가능하게끔 Service에서 메서드 설정 필요
    public void removeLike(){
        this.likeCount--;
    }

    public void incrementViewCount(){
        this.viewCount++;
    }

    public void addHate(){
        this.hateCount++;

        if (this.hateCount >= 10){
            this.isDangerous = true;
        }
    }

    // Hate를 눌렀던 사용자만 가능하게끔 Service에서 메서드 설정 필요
    public void removeHate(){
        this.hateCount--;

        if (this.hateCount < 10){
            this.isDangerous = false;
        }
    }
}

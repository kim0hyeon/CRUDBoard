package com.example.crudboard.domain;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
@Getter
@Setter
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    // 여러 comment은 하나의 post에 속함
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // 여러 comment는 하나의 user에 속함
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "like_count", nullable = false)
    private int likeCount;

    @Column(name = "hate_count", nullable = false)
    private int hateCount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Comment(Post post, User user, String content){
        this.post = post;
        this.user = user;
        this.content = content;
        this.likeCount = 0;
        this.hateCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void addLike(){
        this.likeCount++;
    }

    public void removeLike(){
        this.likeCount--;
    }

    public void addHate(){
        this.hateCount++;
    }

    public void removeHate(){
        this.hateCount--;
    }

    public void updateComment(String new_content){
        this.content = new_content;
        this.updatedAt = LocalDateTime.now();
    }
}

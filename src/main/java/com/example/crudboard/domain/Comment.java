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
    private Long comment_id;

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

    @Column(nullable = false)
    private int like_count;

    @Column(nullable = false)
    private int hate_count;

    @Column(nullable = false)
    private LocalDateTime created_at;

    @Column(nullable = false)
    private LocalDateTime updated_at;

    public Comment(Post post, User user, String content){
        this.post = post;
        this.user = user;
        this.content = content;
        this.like_count = 0;
        this.hate_count = 0;
        this.created_at = LocalDateTime.now();
        this.updated_at = LocalDateTime.now();
    }

    public void addLike(){
        this.like_count++;
    }

    public void removeLike(){
        this.like_count--;
    }

    public void addHate(){
        this.hate_count++;
    }

    public void removeHate(){
        this.hate_count--;
    }

    public void updateComment(String new_content){
        this.content = new_content;
        this.updated_at = LocalDateTime.now();
    }
}

package com.example.crudboard.repository;

import com.example.crudboard.domain.Comment;
import com.example.crudboard.domain.Post;  // 특정 게시글의 댓글 조회를 위해 필요
import com.example.crudboard.domain.User;  // 특정 사용자의 댓글 조회를 위해 필요
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;  // 페이지네이션을 위한 Page 객체
import org.springframework.data.domain.Pageable;  // 페이지네이션 정보를 위한 Pageable 객체
import java.util.Optional;

public interface CommentRepository extends  JpaRepository<Comment, Long>{

    // 특정 게시글(Post)에 속하는 모든 댓글을 페이지 단위로 조회한다.
    // 게시글 상세 페이지에서 댓글 목록을 보여줄 때 사용된다.
    Page<Comment> findByPost(Post post, Pageable pageable);

    // 특정 사용자(User)가 작성한 모든 댓글을 페이지 단위로 조회한다.
    Page<Comment> findByUser(User user, Pageable pageable);

    // 특정 CommentId를 가진 댓글이 특정 사용자가 작성한 댓글인지 확인 (댓글 수정/삭제 권한 확인)
    Optional<Comment> findByCommentIdAndUser(Long commentId, User user);

    // 특정 게시글에 특정 사용자가 작성한 댓글이 있는지 확인
    Optional<Comment> findByPostAndUser(Post post, User user);
}

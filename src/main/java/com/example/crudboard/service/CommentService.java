package com.example.crudboard.service;

import com.example.crudboard.domain.Comment;
import com.example.crudboard.domain.User;
import com.example.crudboard.domain.Post;
import com.example.crudboard.repository.CommentRepository;
import com.example.crudboard.repository.UserRepository;
import com.example.crudboard.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    /*
    * 새로운 댓글을 생성한다.
    * @param postId 게시글 고유 ID
    * @param userId 사용자 고유 ID
    * @param conetent 댓글 내용
    * @return 생성된 Comment 엔티티
    * @throws IllegalArgumentException (게시글 또는 사용자를 찾을 수 없을 경우 발생)
    * */
    @Transactional
    public Comment createComment(Long postId, Long userId, String content) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("해당 ID의 게시글을 찾을 수 없습니다."));
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("해당 ID의 사용자를 찾을 수 없습니다."));

        Comment newComment = new Comment(post, user, content);
        return commentRepository.save(newComment);  // 데이터베이스에 저장 및 리턴
    }

    /*
    * 특정 게시글에 속하는 모든 댓글을 페이지 단위로 조회한다.
    * @param postId 댓글을 조회할 게시글 ID
    * @param page 페이지 번호(0부터 시작)
    * @param size 한 페이지당 댓글 수
    * @return 페이지네이션된 Comment 엔티티 목록
    * @throws IllegalArgumentException 게시글을 찾을 수 없을 경우 발생
    * */
    public Page<Comment> findCommentsByPostId(Long postId, int page, int size) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("해당 ID의 게시글을 찾을 수 없습니다."));

        // 최신 댓글이 먼저 오도록 createdAt 기준으로 내림차순 정렬
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return commentRepository.findByPost(post, pageable);
    }

    /*
    * 특정 ID의 댓글을 조회한다.
    * @param commentId 조회할 댓글 ID
    * @return 조회된 Comment 엔티티
    * @throws IllegalArgumentException 해당 ID의 댓글을 찾을 수 없을 경우 발생
    * */
    public Comment getCommentById(Long commentId){
        return commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("해당 ID의 댓글을 찾을 수 없습니다."));
    }

    /*
    * 댓글을 수정한다.
    * @param commentId 수정할 댓글 ID
    * @param userId 댓글 작성자 ID (권한 확인)
    * @param newContent 새로운 댓글 내용
    * @return 수정된 Comment 엔티티
    * @throws IllegalArgumentException 해당 ID의 댓글을 찾을 수 없거나, 작성자가 일치하지 않을 경우
    * */
    @Transactional
    public Comment updateComment(Long commentId, Long userId, String newContent){
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("해당 ID의 댓글을 찾을 수 없습니다."));
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("해당 ID의 사용자를 찾을 수 없습니다."));

        // 댓글 작성자가 아닐 경우
        if (!comment.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("댓글을 수정할 권한이 없습니다.");
        }

        comment.updateComment(newContent);
        return comment;
    }

    /*
    * 댓글을 삭제한다. (관리자, 댓글 작성자만 가능)
    * @param commentId 삭제할 댓글 ID
    * @param userId 댓글 작성자 ID (권한 확인용)
    * @throws IllegalArgumentException 해당 ID의 댓글을 찾을 수 없거나, 작성자가 일치하지 않ㅇ르 경우 발생
    * */
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("해당 ID의 댓글을 찾을 수 없습니다."));
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("해당 ID의 사용자를 찾을 수 없습니다."));

        // 권한 확인
        if (!comment.getUser().getUserId().equals(user.getUserId())){
            throw new IllegalArgumentException("댓글을 삭제할 권한이 없습니다.");
        }

        commentRepository.delete(comment);  // 데이터베이스에서 댓글 삭제
    }
}
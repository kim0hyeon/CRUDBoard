package com.example.crudboard.controller;

import com.example.crudboard.domain.Comment;
import com.example.crudboard.service.CommentService;
import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


// 댓글 요청을 받을 DTO
@Getter
@Setter
class CommentRequestDto {
    private Long postId;  // 댓글이 속할 게시글 ID
    private Long userId;  // 댓글 작성자 ID
    private String content;  // 댓글 내용
}

// 댓글 응답 데이터를 담을 DTO
@Getter
@RequiredArgsConstructor
class CommentResponseDto {
    private final Long commentId;
    private final Long postId;
    private final Long userId;
    private final String content;
    private final int likeCount;
    private final int hateCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public CommentResponseDto (Comment comment) {
        this.commentId = comment.getCommentId();
        this.postId = comment.getPost().getPostId();
        this.userId = comment.getUser().getUserId();
        this.content = comment.getContent();
        this.likeCount = comment.getLikeCount();
        this.hateCount = comment.getHateCount();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
    }
}

// 페이지네이션된 댓글 목록 응답을 위한 DTO
@Getter
@RequiredArgsConstructor
class CommentPageResponseDto {
    private final List<CommentResponseDto> content;
    private final int totalPages;
    private final long totalElements;
    private final int currentPage;
    private final int pageSize;
    private final boolean hasNext;
    private final boolean hasPrevious;

    public CommentPageResponseDto(Page<Comment> commentPage) {
        this.content = commentPage.getContent().stream().map(CommentResponseDto::new).collect(Collectors.toList());
        this.totalPages = commentPage.getTotalPages();
        this.totalElements = commentPage.getTotalElements();
        this.currentPage = commentPage.getNumber();
        this.pageSize = commentPage.getSize();
        this.hasNext = commentPage.hasNext();
        this.hasPrevious = commentPage.hasPrevious();
    }
}

@RestController  // RESTful API 컨트롤러임을 알림
@RequiredArgsConstructor  // final 필드를 자동으로 주입받는 생성자
@RequestMapping("/api/comments")  // 이 컨트롤러의 기본 URL 경로를 설정
public class CommentController {

    private final CommentService commentService;  // @ReauiredArgsConstructor에 의해 자동으로 주입받아 생성자 코드를 구현하지 않고도 사용 가능

    /*
    * 새로운 댓글 생성 API
    * @POST /api/comments
    * @param requestDto 생성할 댓글 정보(postId, userId, content)
    * @return 생성된 Comment 정보 (201 CREATED)
    * */
    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(@RequestBody CommentRequestDto requestDto) {
        try {
            Comment newComment = commentService.createComment(
                    requestDto.getPostId(),
                    requestDto.getUserId(),
                    requestDto.getContent()
            );
            return new ResponseEntity<>(new CommentResponseDto(newComment), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /*
    * 특정 게시글의 모든 댓글 목록 조회 API (페이지네이션 적용)
    * GET /api/comments/post/{postId}?page={page}&size={size}
    * @param postId 댓글을 조회할 게시글 ID
    * @param page 페이지 번호 (기본값 0)
    * @param sizze 한 페이지당 댓글 수 (기본값 5)
    * @return 페이지네이션된 Comment 목록 (200 OK)
    * */
    @GetMapping("/post/{postId}")
    public ResponseEntity<CommentPageResponseDto> getCommentsByPostId(@PathVariable Long postId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        try {
            Page<Comment> commentPage = commentService.findCommentsByPostId(postId, page, size);
            return new ResponseEntity<>(new CommentPageResponseDto(commentPage), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /*
    * 특정 ID의 댓글 조회 API
    * GET /api/comments/{commentId}
    * @param commentId 조회할 댓글 ID
    * @return 조회된 Comment 정보 (200 OK)
    * */
    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> getCommentById(@PathVariable Long commentId) {
        try {
            Comment comment = commentService.getCommentById(commentId);
            return new ResponseEntity<>(new CommentResponseDto(comment), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /*
    * 댓글 수정 API
    * PUT /api/comments/{commentId}
    * @param commentId 댓글 ID
    * @param newContent 수정할 내용
    * @return 수정된 Comment 정보 (200 OK)
    * */
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(@PathVariable Long commentId, @RequestBody CommentRequestDto requestDto) {
        try {
            Comment updateComment = commentService.updateComment(commentId,requestDto.getUserId(), requestDto.getContent());  // userId를 통해 권한 확인
            return new ResponseEntity<>(new CommentResponseDto(updateComment), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);  // 권한 없음 또는 댓글을 찾을 수 없음
        }
    }

    /*
    * 특정 ID의 댓글 삭제 API
    * DELETE /api/comments/{commentId}
    * @param commentId 삭제할 댓글 ID
    * @param userId 삭제 요청 사용자 ID (권한 확인용)
    * @return 삭제 성공 메시지
    * */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId, @RequestParam Long userId) {  // Delete 요청의 경우 일반적으로 Body를 포함하지 않는다. userId는 삭제 권한 확인을 위한 정보이므로 URL쿼리 파라미터로 받는 것이 RESTful API관례에 더 적합하다.
        try {
            commentService.deleteComment(commentId, userId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);  // 권한 없음 또는 댓글을 찾을 수 없음
        }
    }
}
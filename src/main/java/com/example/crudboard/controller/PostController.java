package com.example.crudboard.controller;

import com.example.crudboard.domain.Post;
import com.example.crudboard.service.PostService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// 게시글 요청을 받을 DTO
@Getter
@Setter
class PostRequestDto {
    private Long boardId;
    private Long userId;
    private String title;
    private String content;
    private String imageUrl;
}

// 게시글 응답 데이터를 담을 DTO
@Getter
@RequiredArgsConstructor
class PostResponseDto {
    private final Long postId;
    private final Long userId;
    private final Long boardId;
    private final String title;
    private final String content;
    private final String imageUrl;
    private final int likeCount;
    private final int hateCount;
    private final int viewCount;
    private final boolean isDangerous;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public PostResponseDto(Post post){
        this.postId = post.getPostId();
        this.userId = post.getUser().getUserId();  // User 객체에서 ID 추출
        this.boardId = post.getBoard().getBoardId();  // Board 객체에서 ID 추출
        this.title = post.getTitle();
        this.content = post.getContent();
        this.imageUrl = post.getImageUrl();
        this.likeCount = post.getLikeCount();
        this.hateCount = post.getHateCount();
        this.viewCount = post.getViewCount();
        this.isDangerous = post.isDangerous();  // boolean 타입의 getter는 is로 시작한다.
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
    }
}

// 페이지네이션된 게시글 목록 응답을 위한 DTO
@Getter
@RequiredArgsConstructor
class PostPageResponseDto {
    private final List<PostResponseDto> content;
    private final int totalPages;
    private final long totalElements;
    private final int currentPage;
    private final int pageSize;
    private final boolean hasNext;
    private final boolean hasPrevious;

    public PostPageResponseDto(Page<Post> postPage) {
        this.content = postPage.getContent().stream().map(PostResponseDto::new).collect(Collectors.toList());
        this.totalPages = postPage.getTotalPages();
        this.totalElements = postPage.getTotalElements();
        this.currentPage = postPage.getNumber();
        this.pageSize = postPage.getSize();
        this.hasNext = postPage.hasNext();
        this.hasPrevious = postPage.hasPrevious();
    }
}

@RestController  // RESTful API 컨트롤러임을 알림
@RequiredArgsConstructor  // final 필드를 자동으로 주입받는 생성자를 생성
@RequestMapping("/api/posts")  // 이 컨트롤러의 기본 URL 경로
public class PostController {

    private final PostService postService;  // PostService를 주입받아 비즈니스 로직 호출

    /*
    * 새로운 게시글 생성 API
    * POST /api/posts
    * @param requestDto 생성할 게시글 정보 (boardId, userId, title, content, imageUrl)
    * @return 생성된 Post 정보 (201 Created)
    * */
    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(@RequestBody PostRequestDto requestDto) {
        try {
            Post newPost = postService.createPost(
                    requestDto.getBoardId(),
                    requestDto.getUserId(),
                    requestDto.getTitle(),
                    requestDto.getContent(),
                    requestDto.getImageUrl()
            );
            return new ResponseEntity<>(new PostResponseDto(newPost), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /*
    * 모든 게시글 목록 조회 API (페이지네이션 적용)
    * GET /api/posts?page={page}&size={size}
    * @param page 페이지 번호 (기본값 0)
    * @param size 한 페이지당 게시글 수 (기본값 5)
    * @return 페이지네이션된 Post 목록 (200 OK)
    * */
    @GetMapping
    public ResponseEntity<PostPageResponseDto> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Page<Post> postPage = postService.findAllPosts(page, size);
        return new ResponseEntity<>(new PostPageResponseDto(postPage), HttpStatus.OK);
    }

    /*
    * 특정 ID의 게시글 조회 API
    * GET /api/posts/{postId}
    * @param postId 조회할 게시글 ID
    * @return 조회된 Post 정보 (200 OK)
    * @throws 실패할 경우 Not Found 반환
    * */
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> getPostById(@PathVariable Long postId) {
        try {
            Post post = postService.getPostById(postId);
            return new ResponseEntity<>(new PostResponseDto(post), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /*
    * 게시글 수정 API
    * POT /api/posts/{postId}
    * @param postId 수정할 게시글 ID
    * @param requestDto 수정할 게시글 정보 (title, content, imageUrl)
    * @return 수정된 Post 정보 (200 OK)
    * @throws 실패할 경우 Bad Request 반환
    * */
    @PutMapping("/{postId}")
    public ResponseEntity<PostResponseDto> updatePost(@PathVariable Long postId, @RequestBody PostRequestDto requestDto) {
        try {
            Post updatePost = postService.updatePost(
                    postId,
                    requestDto.getTitle(),
                    requestDto.getContent(),
                    requestDto.getImageUrl()  // NULL일 경우 PostService에서 처리
            );
            return new ResponseEntity<>(new PostResponseDto(updatePost), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /*
    * 특정 ID의 게시글 삭제 API
    * @param postId 삭제할 게시글 ID
    * @return 삭제 성공 메시지 (204 No Content)
    * @throw 실패 시 Not Found 404 메시지
    */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        try {
            postService.deletePost(postId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /*
    * 게시글 검색 API (페이지네이션 적용)
    * GET /api/posts/search?type={searchType}&keyword={keyword}&page={page}&size={size}
    * @param searchType 검색 타입 ("title, "title_content", "author")
    * @param keyword 검색 키워드
    * @param page 페이지 번호 (기본값 0)
    * @param size 한 페이지당 게시글 수 (기본값 5)
    * @return 검색 결과에 해당하는 페이지네이션된 Post 목록 (200 OK)
    * @throw 검색에 실패한 경우 Bad Request 반환
    * */
    @GetMapping("/search")
    public ResponseEntity<PostPageResponseDto> searchPosts(
            @RequestParam String type,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        try {
            Page<Post> postPage = postService.searchPosts(type, keyword, page, size);
            return new ResponseEntity<>(new PostPageResponseDto(postPage), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);  // 유효하지 않은 검색 타입 등
        }
    }

    // 좋아요 싫어요 API는 나중에 구현할 예정 (현재는 PostService에만 메서드 존재)
}
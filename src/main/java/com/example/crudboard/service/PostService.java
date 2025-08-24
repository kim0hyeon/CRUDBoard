package com.example.crudboard.service;

import com.example.crudboard.domain.Board;
import com.example.crudboard.domain.Post;
import com.example.crudboard.domain.User;
import com.example.crudboard.repository.BoardRepository;
import com.example.crudboard.repository.PostRepository;
import com.example.crudboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service  // 서비스 계층의 컴포넌트임을 알리는 어노테이션
@RequiredArgsConstructor  // final 필드를 자동으로 주입받는 생성자 생성
@Transactional(readOnly = true)  // 이 클래스의 트랜젝션을 기본적으로 읽기 전용으로 설정
public class PostService {

    private final PostRepository postRepository;  // PostRepository를 주입받아 게시글 데이터에 접근
    private final BoardRepository boardRepository;  // BoardRepository를 주입받아 게시판 데이터에 접근 (게시글이 속할 게시판 확인 등)
    private final UserRepository userRepository;  //  UserRepository를 주입받아 사용자 데이터에 접근 (게시글 작성자 확인 등)

    /*
    * 새로운 게시글 생성
    * @param boardId 게시글이 속할 게시판 ID
    * @param userId 게시글을 작성할 사용자 ID
    * @param title 게시글 제목
    * @param content 게시글 내용
    * @param imageUrl 게시글 이미지 URL (선택 사항)
    * @return 생성된 Post 엔티티
    * @throws IllegalArgumentException 게시판 또는 사용자를 찾을 수 없을 경우 발생
    * */
    @Transactional  // 쓰기 작업까지 트랜젝션에 포함되도록 설정
    public Post createPost(Long boardId, Long userId, String title, String content, String imageUrl) {
        // 게시판과 사용자 엔티티 조회 (외래키 매핑을 위해 객체 필요)
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("해당 ID의 게시판을 찾을 수 없습니다."));
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("해당 ID의 사용자를 찾을 수 없습니다."));

        // Post 엔티티 생성 (imageUrl 유무에 따라 적절한 생성자 사용)
        Post newPost;
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            newPost = new Post(board, user, title, content, imageUrl);
        } else {
            newPost = new Post(board, user, title, content);
        }
        return postRepository.save(newPost);  // 데이터베이스에 저장과 동시에 생성된 Post 엔티티 반환 (코드 설명 필)
    }

    /* 코드 설명 필
    * 모든 게시글을 페이지 단위로 조회한다.
    * @param page 페이지 번호 (0부터 시작)
    * @param size 한 페이지당 게시글 수
    * @return 페이지네이션된 Post 엔티티 목록
    * */
    public Page<Post> findAllPosts(int page, int size) {
        // 최신 게시글이 먼저 오도록 createdAt 기준으로 내림차순 정렬
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return postRepository.findAll(pageable);
    }

    /*
    * 특정 ID의 게시글을 조회하고 조회수를 1 증가시킨다.
    * @param postId 조회할 게시글 ID
    * @return 조회된 Post 엔티티
    * @throws IllegalArgumentException 해당 ID의 게시글을 찾을 수 없을 경우 발생
    * */
    @Transactional
    public Post getPostById(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("해당 ID의 게시글을 찾을 수 없습니다."));
        post.incrementViewCount();  // post 엔티티 내부의 메서드를 사용해 조회수 증가
        return post;  // @Transactional 덕분에 변경 감지 후 자동으로 업데이트
    }

    /*
    * 게시글을 수정한다. (관리자 또는 작성자만 가능 - 권한 로직은 Controller 또는 AOP에서 처리)
    * @param postId 수정할 게시글 ID
    * @param newTitle 새로운 제목
    * @param newContent 새로운 내용
    * @param newImageUrl 새로운 이미지 URL (null이면 기존 이미지 유지)
    * @return 수정된 Post 엔티티
    * throws IllegalArgumentException 해당 ID의 게시글을 찾을 수 없을 경우 발생
    * */
    @Transactional
    public Post updatePost(Long postId, String newTitle, String newContent, String newImageUrl) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("해당 ID의 게시글을 찾을 수 없습니다."));

        // Post 엔티티 내부의 메서드를 사용하여 내용 업데이트 (imageUrl 유무에 따라 오버로딩된 메서드 사용)
        if (newImageUrl != null) {  // newimageUrl이 있다면 imageUrl까지 업데이트
            post.updatePost(newTitle, newContent, newImageUrl);
        } else {  // 없다면 제외하고 업데이트
            post.updatePost(newTitle, newContent);
        }
        return post;  // @Transactional 덕분에 변경 감지 후 자동으로 업데이트
    }

    /*
    * 게시글을 삭제한다. (관리자 또는 작성자만 가능 - 권한 로직은 Controller 또는 AOP에서 처리)
    * @param postId 삭제할 게시글 ID
    * @throws IllegalArgumentException 해당 ID의 게시글을 찾을 수 없을 경우 발생
    * */
    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("해당 ID의 게시글을 찾을 수 없습니다."));
        postRepository.delete(post);
    }

    /*
    * 게시글을 검색한다.
    * @param serchType 검색 타입 ("title", "title_content", "author")
    * @param keyword 검색 키워드
    * @param page 페이지 번호 (0부터 시작)
    * @param size 한 페이지당 게시글 수
    * @return 검색 결과에 해당하는 페이지네이션된 Post 엔티티 목록
    * @throws IllegalArgumentException 유효하지 않은 검색 타입일 경우 발생
    * */
    public Page<Post> searchPosts(String searchType, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());  // 최신순 정렬

        switch (searchType) {
            case "title":
                return postRepository.findByTitleContaining(keyword, pageable);
            case "title_content":
                return postRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
            case "author":
                return postRepository.findByUser_UserNameContaining(keyword, pageable);
            default:
                throw new IllegalArgumentException("유효하지 않은 검색 타입입니다: " + searchType);
        }
    }

    /*
    * 게시글 좋아요 수를 증가시킨다.
    * @param postId 좋아요를 누를 게시글 ID
    * @return 업데이트된 Post 엔티티
    * @throw IllegalArgumentException 해당 ID의 게시글을 찾을 수 없을 경우 발생
    * */
    @Transactional
    public Post addLikeToPost(Long postId){
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("해당 ID의 게시글을 찾을 수 없습니다."));
        post.addLike();  // Post 엔티티 내부의 메서드를 사용하여 좋아요 수 증가
        return post;
    }

    /* 좋아요를 누른 사람만 누를 수 있게끔 확인이 필요하다. - 나중에
    * 게시글 좋아요 수를 감소시킨다.
    * @param postId 좋아요를 취소할 게시글 ID
    * @return 업데이트된 Post 엔티티
    * @throw IllegalArgumentException 해당 ID의 게시글을 찾을 수 없을 경우 발생
    * */
    @Transactional
    public Post removeLikeFromPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("해당 ID의 게시글을 찾을 수 없습니다."));
        post.removeLike();
        return post;
    }

    /*
    * 게시글 싫어요 수를 증가시킨다. (주의 글 표시 로직 포함 - 나중에)
    * @param postId 싫어요를 누를 게시글 ID
    * @throws IllegalArgumentException 해당 ID의 게시글을 찾을 수 없을 경우 발생
    * */
    @Transactional
    public Post addHateToPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("해당 ID의 게시글을 찾을 수 없습니다."));
        post.addHate();
        return post;
    }


    /* 싫어요를 누른 사람만 누를 수 있게끔 확인이 필요하다.
    * 게시글 싫어요 수를 감소시킨다. (싫어요 10개 미만이 된 경우 삭제 로직 포함 - 나중에)
    * @param postId 싫어요를 취소할 게시글 ID
    * @return 업데이트된 Post 엔티티
    * @throws IllegalArgumentException 해당 ID의 게시글을 찾을 수 없을 경우 발생
    * */
    @Transactional
    public Post removeHateFromPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("해당 ID의 게시글을 찾을 수 없습니다."));
        post.removeHate();
        return post;
    }
}

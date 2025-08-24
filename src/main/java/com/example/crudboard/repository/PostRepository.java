package com.example.crudboard.repository;

import com.example.crudboard.domain.Post;
import com.example.crudboard.domain.User;  // 작성자 검색을 위해 필요
import com.example.crudboard.domain.Board;  // 특정 게시판에 속하는 게시글 조회 위해 필요
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;  // 페이지네이션을 위한 Page 객체 import
import org.springframework.data.domain.Pageable;  // 페이지네이션 정보를 위한 Pageable 객체 import

// PostRepository 인터페이스는 JpaRepository를 상속받아 Post 엔티티와 Long 타입의 ID를 관리한다.
public interface PostRepository extends JpaRepository<Post, Long>{

    // 게시글 검색 기능
    // 1. 제목으로 검색
    // Containing은 SQL의 LIKE %keyword%와 유사하게 동작
    Page<Post> findByTitleContaining(String title, Pageable pageable);

    // 2. 제목 + 내용으로 검색
    // OR 조건을 사용하여 제목 또는 내용에 키워드가 포함된 게시글 찾기
    Page<Post> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);

    // 3. 작성자로 검색
    // User엔티티의 userName 필드를 통해 검색
    Page<Post> findByUser_UserNameContaining(String userName, Pageable pageable);

    // 특정 게시판(Board)에 속하는 모든 게시글을 조회 (페이지네이션 포함)
    Page<Post> findByBoard(Board board, Pageable pageable);

    // 특정 사용자가 작성한 모든 게시글을 조회 (페이지네이션 포함)
    Page<Post> findByUser(User user, Pageable pageable);

    // 특정 사용자가 작성한 게시글 중 is_dangerous가 true인 게시글의 개수를 세는 메서드
    long countByUserAndIsDangerousTrue(User user);
}

package com.example.crudboard.repository;

import com.example.crudboard.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// JpaRepository를 상속받아 Board 엔티티와 Long 타입의 ID를 관리한다.
public interface BoardRepository extends JpaRepository<Board, Long> {
    // 게시판 이름(boardName)으로 Board 엔티티를 찾는 메서드를 정의한다.
    // 게시판 중복 확인이나 특정 게시판 조회를 위해 사용될 수 있다.
    Optional<Board> findByBoardName(String boardName);
}

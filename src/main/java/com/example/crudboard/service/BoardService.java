package com.example.crudboard.service;

import com.example.crudboard.domain.Board;
import com.example.crudboard.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service  // 서비스 계층의 컴포넌트임을 알림
@RequiredArgsConstructor  // final 필드를 자동으로 주입받는 생성자 생성 어노테이션
@Transactional(readOnly = true)  // 트랜젝션을 읽기 전용으로 설정해 성능 최적화
public class BoardService {

    private final BoardRepository boardRepository;

    /*
    * 새로운 게시판을 생성한다.
    * @param boardName 생성할 게시판 이름
    * @return 생성된 Board 엔티티
    * throws IllegalArgumentException 이미 존재하는 게시판 이름일 경우 발생
    * */
    @Transactional  // 쓰기 작업도 포함되도록 설정
    public Board createBoard(String boardName){
        // 게시판 이름 중복 확인
        if (boardRepository.findByBoardName(boardName).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 게시판 이름입니다.");
        }
        Board newBoard = new Board(boardName);  // Board 엔티티의 생성자를 사용하여 객체 생성
        return boardRepository.save(newBoard);  // 데이터베이스에 저장
    }

    /*
    * 모든 게시판 목록을 조회
    * @return 모든 Board 엔티티 리스트
    * */
    public List<Board> findAllBoards() {
        return boardRepository.findAll();
    }

    /*
    * 특정 ID의 게시판 조회
    * @param boardId 조회할 게시판 ID
    * @return Board 엔티티
    * @throws IllegalArgumentException 해당 ID의 게시판을 찾을 수 없을 경우 발생
    * */
    public Board findBoardById(Long boardId){
        return boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("해당 ID의 게시판을 찾을 수 없습니다."));
    }

    /*
    * 특정 게시판의 이름을 수정한다.
    * @param boardId 수정할 게시판 ID
    * @param newBoardName 새로운 게시판 이름
    * @return 수정된 Board 엔티티
    * @throws IllegalArgumentException 해당 ID의 게시판을 찾을 수 없거나, 새로운 이름이 중복될 경우 발생
    * */
    @Transactional
    public Board updateBoard(Long boardId, String newBoardName) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("해당 ID의 게시판을 찾을 수 없습니다."));

        // 새로운 게시판 이름 중복 확인 (단, 자기자신의 이름은 허용)
        Optional<Board> existingBoard = boardRepository.findByBoardName(newBoardName);
        if (existingBoard.isPresent() && !existingBoard.get().getBoardId().equals(boardId)) {
            throw new IllegalArgumentException("이미 존재하는 게시판 이름입니다.");
        }

        board.updateBoardName(newBoardName);  // Board 엔티티 내부의 메서드를 사용하여 이름 업데이트
        return board;
    }

    /*
    * 특정 ID의 게시판을 삭제한다.
    * @param boardId 삭제할 게시판 ID
    * @throws IllegalArgumentException 해당 ID의 게시판을 찾을 수 없을 경우 발생
    * */
    @Transactional
    public void deleteBoard(Long boardId){
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("해당 ID의 게시판을 찾을 수 없습니다."));
        boardRepository.delete(board);
    }
}

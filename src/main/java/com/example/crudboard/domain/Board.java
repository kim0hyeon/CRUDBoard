package com.example.crudboard.domain;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "board")
@Getter
@Setter
@NoArgsConstructor
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long boardId;

    @Column(name = "board_name", nullable = false, unique = true, length=30)
    private String boardName;

    // 새로운 게시판을 생성할 때 사용될 생성자
    public Board(String boardName){
        this.boardName = boardName;
    }

    // 게시판 이름을 수정할 때 사용될 메서드
    public void updateBoardName(String newBoardName) {
        // 검증은 Service 클래스에서 이루어진다.
        this.boardName = newBoardName;
    }
}
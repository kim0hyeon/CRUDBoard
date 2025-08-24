package com.example.crudboard.controller;

import com.example.crudboard.domain.Board;
import com.example.crudboard.service.BoardService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;  // HTTP 응답 상태 코드(예: 200 OK, 400 Bad Request 등)
import org.springframework.http.ResponseEntity;  // HTTP 응답 본문과 상태 코드를 함께 담아 반환할 수 있는 객체
import org.springframework.web.bind.annotation.*;  // Spring Web에서 RESTful API를 만들 때 사용하는 다양한 어노테이션

import java.util.List;  // List 타입을 사용하기 위해 가져온다. (getAllBoards 메서드 반환 타입)
import java.util.stream.Collectors;  // Stream API를 사용하여 컬렉션을 변환할 때 필요하다.

// 게시판 요청 데이터를 받을 DTO (Data Transfer Object) 클래스
// 클라이언트로부터 게시판 생성/수정 요청 시 데이터를 담는 용도
@Getter  // 이 클래스의 필드들에 대한 Getter 메서드 자동 생성
@Setter  // 이 클래스의 필드들에 대한 Setter 메서드 자동 생성
class BoardRequestDto {
    private String boardName;
}

// 게시판 응답 데이터를 받을 DTO 클래스
// 서버가 클라이언트에게 게시판 정보(생성/조회/수정 결과)를 보낼 때 사용
@Getter
@Setter
@RequiredArgsConstructor
class BoardResponseDto {
    private final Long boardId;
    private final String boardName;

    public BoardResponseDto(Board board) {  // Board 엔티티 객체를 받아서 BoardResponseDto 객체로 변환하는 생성자
        this.boardId = board.getBoardId();
        this.boardName = board.getBoardName();
    }
}

@RestController // 이 클래스가 RESTful API 컨트롤러임을 Spring에게 알린다. 이 어노테이션으로 메서드의 반환값이 HTTP 응답 본문으로 직접 전송된다.
@RequiredArgsConstructor  // final 필드(boardService)를 주입받는 생성자를 자동으로 생성한다. 의존성 주입을 간결하게 처리한다.
@RequestMapping("/api/boards")  // 이 컨트롤러 내 모든 메서드의 기본 URL 경로를 '/api/boards'로 설정한다.
public class BoardController {  // 게시판과 관련된 HTTP 요청을 처리하는 컨트롤러 클래스이다.

    private final BoardService boardService;  // BoardService 객체를 주입받아 사용한다.

    /*
    * 새로운 게시판 생선 API
    * POST /api/boards
    * @param requestDto 클라이언트로부터 받은 게시판 생성 정보 (boardName)
    * @return 생성된 Board 정보 (201 created)
    * */
    @PostMapping
    public ResponseEntity<BoardResponseDto> createBoard(@RequestBody BoardRequestDto requestDto) {  // @RequestBody: 클라이언트가 보낸 JSON 요청 본문을 BoardRequestDto 객체로 변환하여 받는다.
        try {
            Board newBoard = boardService.createBoard(requestDto.getBoardName());  // 매개변수로 받은 Dto를 가지고 이름을 꺼내 새로운 보드 생성을 시도한다.
            return new ResponseEntity<>(new BoardResponseDto(newBoard), HttpStatus.CREATED);  // 성공하면 201 응답과 함께 생성된 게시판 정보를 반환한다.
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);  // 생성에 실패한 경우 400 Bad Request 응답을 반환한다.
        }
    }

    /*
    * 모든 게시판 목록 조회 API
    * GET /api/boards
    * @return 모든 Board 정보 리스트 (200 OK)
    * */
    @GetMapping  // HTTP GET 요청 중 /api/boards 경로로 들어오는 요청을 처리한다.
    public ResponseEntity<List<BoardResponseDto>> getAllBoards() {  // List<Board<ResponseDto>를 포함한 ResponseEntity를 반환한다.
        List<Board> boards = boardService.findAllBoards();  // BoardService의 findAllBoards 메서드를 호출해 모든 게시판 목록을 조회한다.

        // Board 엔티티 리스트를 BoardResponseDto 리스트로 변환한다. Stream API의 collect를 사용한다.
        List<BoardResponseDto> responseDtos = boards.stream().map(BoardResponseDto::new).collect(Collectors.toList());
        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }

    /*
    * 특정 ID의 게시판 조회 API
    * GET /api/boards/(boardId)
    * @param boardId 조회할 게시판 ID (URL 경로 변수)
    * @return 조회된 Board 정보 (200 OK)
    * */
    @GetMapping("/{boardId}")  // HTTP GET 요청 중 'api/boards/{boardId}' 경로로 들어오는 요청을 처리한다.
    public ResponseEntity<BoardResponseDto> getBoardById(@PathVariable Long boardId){
        try {
            Board board = boardService.findBoardById(boardId);  // boardService의 findBoardById 메서드를 호출해 특정 게시판을 조회한다.
            return new ResponseEntity<>(new BoardResponseDto(board), HttpStatus.OK);  // 성공 시 200 OK 응답과 함께 조회된 게시판 정보를 반환한다.
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // 찾을 수 없을 경우 404 NOT FOUND 응답을 반환한다.
        }
    }

    /*
    * 게시판 이름 수정 API
    * PUT /api/boards/{boardId}
    * @param boardId 수정할 게시판 ID
    * @param requestDto 새로운 게시판 이름 (boardName)
    * @return 수정된 Board 정보 (200 OK)
    * */
    @PutMapping("/{boardId}")
    public ResponseEntity<BoardResponseDto> updateBoard(@PathVariable Long boardId, @RequestBody BoardRequestDto requestDto) {
        try {
            Board updateBoard = boardService.updateBoard(boardId, requestDto.getBoardName());  // 보드를 업데이트 해 본다.
            return new ResponseEntity<>(new BoardResponseDto(updateBoard), HttpStatus.OK);  // 성공 시 200 OK 응답과 함께 수정된 게시판을 반환한다.
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);  // 중복 이름 또는 찾을 수 없을 때 400 Bad Request 응답을 반환한다.
        }
    }

    /*
    * 특정 ID의 게시판 삭제 API
    * DELETE /api/boards/{boardId}
    * @param boardId 삭제할 게시판 ID
    * @return 삭제 성공 시 메시지 (204 No Content)
    * */
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long boardId){  // pathVariable로 boardId를 받는다.
        try {
            boardService.deleteBoard(boardId);  // BoardService의 deleteBoard 메서드를 호출해 게시판을 삭제한다.
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);  // 성공하면 204 No Content를 반환한다.
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // 실패하면 404 Not Found를 반환한다.
        }
    }
}

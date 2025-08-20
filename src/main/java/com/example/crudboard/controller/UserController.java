package com.example.crudboard.controller;

import com.example.crudboard.domain.User;
import com.example.crudboard.service.UserService;
import lombok.Getter;  // DTO 사용을 위한 Lombok Getter
import lombok.RequiredArgsConstructor;
import lombok.Setter;  // DTO 사용을 위한 Lombok Setter
import org.springframework.http.HttpStatus;  // HTTP 상태 코드
import org.springframework.http.ResponseEntity;  // HTTP 응답 객체
import org.springframework.web.bind.annotation.*;  // RESTful API 관련 어노테이션


// 사용자 요청 데이터를 받을 DTO(Data Transfer Object) 클래스
// 클라이언트로부터 회원가입, 로그인 요청 시 데이터를 담는 용도
@Getter
@Setter
class UserRequestDto {
    private String user_name;
    private String password;
    private String nickname;
}

// 사용자 응답 데이터를 담을 DTO 클래스 (로그인 성공 시 반환할 데이터 등)
@Getter
@Setter
@RequiredArgsConstructor
class UserResponseDto {
    private final Long user_id;
    private final String user_name;
    private final String nickname;

    public UserResponseDto(User user){
        this.user_id = user.getUserId();
        this.user_name = user.getUserName();
        this.nickname = user.getNickname();
    }
}

@RestController  // 이 클래스가 RESTful API 컨트롤러임을 알림
@RequiredArgsConstructor  // final 필드를 자동으로 주입받는 생성자를 생성
@RequestMapping("/api/users")  // 이 컨트롤러의 기본 URL 경로를 설정한다.
public class UserController {

    private final UserService userService;  // userService를 주입받아 비즈니스 로직을 호출한다.

    /*
    * 회원가입 API
    * POST /api/users/signup
    * @param requestDto 클라이언트로부터 받은 회원가입 정보 (user_name, password, nickname)
    * @return 회원가입 성공 시 201 Created 상태와 사용자 정보
    * */
    @PostMapping("/signup")  // (/api/users/signup) 경로로 들어오는 요청을 처리한다.
    public ResponseEntity<UserResponseDto> signUp(@RequestBody UserRequestDto requestDto) {
        try {
            User newUser = userService.signUp(requestDto.getUser_name(), requestDto.getPassword(), requestDto.getNickname());
            // 회원가입 성공 시 201 Created 상태 코드와 함께 UserResponseDto 반환
            return new ResponseEntity<>(new UserResponseDto(newUser), HttpStatus.CREATED);
        } catch (IllegalArgumentException e){
            // 사용자 이름 또는 닉네임 중복 등 비즈니스 로직 오류 시 400 Bad Request 반환
            // 실제 서비스에서는 Error Response DTO를 만들어서 더 상세한 오류 메시지를 전달하는 것이 좋다.
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /*
    * 로그인 API
    * POST /api/users/login
    * @param requestDto 클라이언트로부터 받은 로그인 정보 (user_name, password)
    * @return 로그인 성공 시 200 OK 상태와 사용자 정보
    * */
    @PostMapping("/login")  // (/api/users/login) 경로로 들어오는 요청을 처리한다.
    public ResponseEntity<UserResponseDto> login(@RequestBody UserRequestDto requestDto){
        try {
            User loggedInuser = userService.login(requestDto.getUser_name(), requestDto.getPassword());
            // 로그인 성공 시 200 OK 상태 코드와 함께 UserResponseDto 반환
            return new ResponseEntity<>(new UserResponseDto(loggedInuser), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // 로그인 실패 시 (없는 사용자 또는 비밀번호 오류) 401 Unauthorized 또는 400 Bad Request 반환
            // 문제 설명에 "없는 사용자이거나 비밀번호가 틀렸습니다."에 맞춰 401 Unauthorized가 더 적절
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    /*
    * 로그아웃 API
    * POST /api/users/logout
    * 실제 로그아웃은 클라이언트(프론트엔드)에서 JMT 토큰을 삭제하는 방식으로 처리될 수 있습니다.
    * 여기선 단순한 백엔드 로그아웃 메시지를 반환한다.
    * return 200 OK 상태 코드와 로그아웃 메시지
    * */
    @PostMapping("/logout")  // (/api/users/login) 경로로 들어오는 요청을 처리한다.
    public ResponseEntity<String> logout() {
        // 실제 로그아웃 로직 (예: 서버 세션 무효화, 토큰 블랙리스트 추가 등)은 여기에 구현
        // 현재는 간단히 성공 메시지만 반환
        return new ResponseEntity<>("로그아웃 되었습니다.", HttpStatus.OK);
    }
}

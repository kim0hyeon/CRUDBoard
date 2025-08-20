package com.example.crudboard.service;

import com.example.crudboard.domain.User;
import com.example.crudboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 비밀번호 암호화를 위해 PasswordEncoder를 사용
import org.springframework.security.crypto.password.PasswordEncoder;

@Service  // 이 클래스가 서비스 계층의 컴포넌트임을 알린다.
@RequiredArgsConstructor  // final 필드를 자동으로 주입받는 생성자를 생성한다. (final 필드?)
@Transactional(readOnly = true)  // 트랜젝션을 읽기 전용으로 설정해 성능을 최적화한다.
public class UserService {

    private final UserRepository userRepository;  // UserRepository를 주입받아 데이터베이스에 접근
    private final PasswordEncoder passwordEncoder;  // 비밀번호 암호화를 위한 passwordEncoder를 주입

    // 의존성 주입을 위한 생성자 -> RequiredArgsConstrouctor가 대신 만들어줌
    // public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
    //      this.userRepository = userRepository;
    //      this.passwordEncoder = passwordEncoder;
    // }

    /*
    * 회원가입 기능
    * @param user_name 사용자 ID
    * @param password 비밀번호 (암호화 전)
    * @param nickname 닉네임
    * @return 저장된 User 엔티티
    * throws IllegalArgumentException 중복된 user_name 또는 nickname
    * */
    @Transactional
    public User signUp(String user_name, String password, String nickname){
        // 1. 사용자 이름 중복 확인
        if (userRepository.findByUserName(user_name).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자 ID입니다.");
        }
        // 2. 사용자 닉네임 중복 확인
        if (userRepository.findByNickname(nickname).isPresent()){
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        // 3. 비밀번호 암호화
        String encodePassword = passwordEncoder.encode(password);

        // 4. User 엔티티 생성 및 저장
        User new_user = new User(user_name, encodePassword, nickname);
        return userRepository.save(new_user);
    }

    /*
    * 로그인 기능
    * @param user_name 사용자 ID
    * @param password 입력된 비밀번호
    * @return 로그인 성공 시 User 엔티티
    * @thrwos IllegalArgumentException 없는 사용자 ID이거나 틀린 비밀번호
    * */
    @Transactional // 읽기 작업이지만, 경우에 따라 User 엔티티의 생태 변경(ex: 최근 로그인 일시)이 있을 수 있으므로 @Transactional 유지
    public User login(String user_name, String password){
        // 1. 사용자 ID로 user 조회
        User user = userRepository.findByUserName(user_name).orElseThrow(() -> new IllegalArgumentException("없는 사용자이거나 비밀번호가 틀렸습니다."));

        // 2. 비밀번호 일치 여부 확인
        // 입력된 비밀번호와 암호화된 저장된 비밀번호 비교
        if (!passwordEncoder.matches(password, user.getPassword())){
            throw new IllegalArgumentException("없는 사용자이거나 비밀번호가 틀렸습니다.");
        }

        // 3. 로그인 성공 (필요하다면 여기에 로그인 관련 로직 추가, 예: 마지막 로그인 시간 업데이트 등)
        return user;
    }

    /*
    * 사용자 ID로 사용자 조회 (다른 서비스에서 사용 가능)
    * @param user_id 조회할 사용자 고유 id
    * @return User 엔티티
    * thrwos IllegalArgumentException 해당 ID의 사용자를 찾을 수 없을 경우 발생
    * */
    public User findUserById(Long user_id) {
        return userRepository.findById(user_id).orElseThrow(() -> new IllegalArgumentException("해당 ID의 사용자를 찾을 수 없습니다."));
    }

}

package dongguk.osori.domain.user.service;

import dongguk.osori.domain.user.UserRepository;
import dongguk.osori.domain.user.dto.*;
import dongguk.osori.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입 로직
    @Transactional
    public void signup(SignupUserDto signupUserDto) {

        if (userRepository.existsByEmail(signupUserDto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        User user = new User();
        user.updateNickName(signupUserDto.getNickname());
        user.updateEmail(signupUserDto.getEmail());

        // 비밀번호 암호화 후 저장
        String encodedPassword = passwordEncoder.encode(signupUserDto.getPassword());
        user.updatePassword(encodedPassword);

        userRepository.save(user);
    }


    // 로그인 인증 로직
    @Transactional(readOnly = true)
    public Long authenticate(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 유저가 없습니다."));

        // 입력된 비밀번호와 해시된 비밀번호 비교
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }

        // 인증 성공 시 유저 ID 반환
        return user.getUserId();
    }

    // 아이디로 프로필 정보 조회
    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 아이디를 찾을 수 없음: " + userId));
    }

    public UserProfileDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 아이디를 찾을 수 없음: " + userId));
        return new UserProfileDto(
                user.getNickname(),
                user.getEmail(),
                user.getMajor(),
                user.getStudentNumber(),
                user.getIntroduce()
        );
    }

    // 프로필 수정
    @Transactional
    public void updateUserProfile(Long userId, UserProfileEditDto userProfileEditDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 아이디를 찾을 수 없음: " + userId));

        user.updateNickName(userProfileEditDto.getNickname());
        user.updateIntroduce(userProfileEditDto.getIntroduce());
        user.updateMajor(userProfileEditDto.getMajor());
        user.updateStudentNumber(userProfileEditDto.getStudentNumber());

    }

    // 현재 비밀번호 확인
    @Transactional(readOnly = true)
    public boolean verifyPassword(Long userId, PasswordDto passwordDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // 입력된 비밀번호와 해시된 비밀번호 비교
        return passwordEncoder.matches(passwordDto.getPassword(), user.getPassword());
    }


    // 비밀번호 수정
    @Transactional
    public void updatePassword(Long userId, PasswordDto passwordDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        String encodedPassword = passwordEncoder.encode(passwordDto.getPassword());
        user.updatePassword(encodedPassword);

        userRepository.save(user);
    }

    // 닉네임 검색
    public List<UserDto> searchUsersByNickname(String nickname) {
        List<User> users = userRepository.findByNicknameContaining(nickname);
        return users.stream()
                .map(user -> new UserDto(user.getUserId(), user.getNickname(), user.getEmail()))
                .collect(Collectors.toList());
    }

    // 이메일 검색
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }

    // 팔로우하는 사용자 목록 반환 메소드
    @Transactional(readOnly = true)
    public List<UserDto> getFollowingUsers(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 아이디를 찾을 수 없음: " + userId));

        return user.getFollowingUsers().stream()
                .map(followedUser -> new UserDto(followedUser.getUserId(), followedUser.getNickname(), followedUser.getEmail()))
                .collect(Collectors.toList());
    }
}


package dongguk.osori.domain.user.service;

import dongguk.osori.domain.user.UserRepository;
import dongguk.osori.domain.user.dto.UserProfileDto;
import dongguk.osori.domain.user.entity.User;
import dongguk.osori.domain.user.dto.PasswordDto;
import dongguk.osori.domain.user.dto.SignupUserDto;
import dongguk.osori.domain.user.dto.UserProfileEditDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

        // 입력받은 비밀번호를 암호화
        String encodedPassword = passwordEncoder.encode(passwordDto.getPassword());

        // 암호화된 비밀번호로 업데이트
        user.updatePassword(encodedPassword);

        // 업데이트된 유저 정보를 저장
        userRepository.save(user);
    }




}

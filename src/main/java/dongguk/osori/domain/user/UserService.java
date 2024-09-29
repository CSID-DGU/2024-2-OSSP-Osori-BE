package dongguk.osori.domain.user;

import dongguk.osori.domain.user.dto.SignupUserDto;
import dongguk.osori.domain.user.dto.UserProfileDto;
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
                user.getEmail()
        );
    }



}

package dongguk.osori.domain.user;

import dongguk.osori.domain.user.dto.LoginRequestDto;
import dongguk.osori.domain.user.dto.SignupUserDto;
import dongguk.osori.domain.user.dto.UserProfileDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupUserDto signupUserDto) {
        try {
            userService.signup(signupUserDto);
            return ResponseEntity.ok("회원가입이 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            log.error("Signup failed: {}", e.getMessage());
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during signup", e);
            return ResponseEntity.status(500).body("서버 에러 발생. 나중에 다시 시도해주세요.");
        }
    }


    // 유저 프로필 정보 조회
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getUserProfile(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        UserProfileDto userDto = userService.getUserProfile(userId);
        return ResponseEntity.ok(userDto);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto loginRequest, HttpSession session) {
        try {
            Long userId = userService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
            session.setAttribute("userId", userId);
            log.info("로그인 성공, 세션에 userId 저장: {}", userId);
            return ResponseEntity.ok("로그인 성공");
        } catch (IllegalArgumentException e) {
            log.error("Login failed: {}", e.getMessage());
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during login", e);
            return ResponseEntity.status(500).body("서버 에러 발생. 나중에 다시 시도해주세요.");
        }
    }


    // 로그아웃 시 세션 무효화
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }



}

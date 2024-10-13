package dongguk.osori.domain.user;

import dongguk.osori.domain.user.dto.*;
import dongguk.osori.domain.user.service.EmailService;
import dongguk.osori.domain.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final EmailService emailService;


    // 이메일 인증 코드 전송
    @PostMapping("/signup/send-email")
    public ResponseEntity<String> sendSignupEmail(@RequestBody Map<String,String> request) {
        String email = request.get("email"); // 이메일 주소 추출
        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(400).body("이메일이 올바르지 않습니다.");
        }
        emailService.sendEmail(email);
        return ResponseEntity.ok("인증 코드가 이메일로 전송되었습니다.");
    }

    // 이메일 인증 코드 확인
    @PostMapping("/signup/verify-code")
    public ResponseEntity<String> verifyEmailCode(@RequestBody EmailVerificationDto verificationDto) {
        boolean isVerified = emailService.verifyAuthCode(verificationDto.getEmail(), verificationDto.getCode());

        if (isVerified) {
            return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
        } else {
            return ResponseEntity.status(400).body("잘못된 인증 코드입니다.");
        }
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupUserDto signupUserDto) {
        try {
            // 이메일 인증이 완료된 사용자만 회원가입 가능
            if (!emailService.verifyAuthCode(signupUserDto.getEmail(), signupUserDto.getAuthCode())) {
                return ResponseEntity.status(400).body("이메일 인증이 완료되지 않았습니다.");
            }

            userService.signup(signupUserDto);
            return ResponseEntity.ok("회원가입이 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
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
            log.error("로그인 실패: {}", e.getMessage());
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

    // 유저 프로필 수정
    @PatchMapping("/profile")
    public ResponseEntity<String> updateUserProfile(@RequestBody UserProfileEditDto userProfileEditDto, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        try {
            userService.updateUserProfile(userId, userProfileEditDto);
            return ResponseEntity.ok("프로필 수정이 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during profile update", e);
            return ResponseEntity.status(500).body("서버 에러 발생. 나중에 다시 시도해주세요.");
        }
    }


    // 현재 비밀번호 확인
    @PostMapping("/verify-password")
    public ResponseEntity<String> verifyPassword(@RequestBody PasswordDto passwordDto, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        if (passwordDto.getPassword() == null || passwordDto.getPassword().isEmpty()) {
            return ResponseEntity.status(400).body("비밀번호가 입력되지 않았습니다.");
        }

        try {
            boolean isVerified = userService.verifyPassword(userId, passwordDto);
            if (isVerified) {
                return ResponseEntity.ok("비밀번호 확인 완료");
            } else {
                return ResponseEntity.status(400).body("비밀번호가 올바르지 않습니다.");
            }
        } catch (Exception e) {
            log.error("Unexpected error during password verification", e);
            return ResponseEntity.status(500).body("서버 에러 발생. 나중에 다시 시도해주세요.");
        }
    }

    // 비밀번호 변경
    @PatchMapping("/password")
    public ResponseEntity<String> updatePassword(@RequestBody Map<String, String> request, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");

        if (currentPassword == null || newPassword == null || currentPassword.isEmpty() || newPassword.isEmpty()) {
            return ResponseEntity.status(400).body("비밀번호가 올바르게 입력되지 않았습니다.");
        }

        try {
            // 현재 비밀번호 확인
            boolean isVerified = userService.verifyPassword(userId, new PasswordDto(currentPassword));
            if (!isVerified) {
                return ResponseEntity.status(400).body("현재 비밀번호가 일치하지 않습니다.");
            }

            // 비밀번호 변경
            userService.updatePassword(userId, new PasswordDto(newPassword));
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } catch (Exception e) {
            log.error("Unexpected error during password update", e);
            return ResponseEntity.status(500).body("서버 에러 발생. 나중에 다시 시도해주세요.");
        }
    }
}


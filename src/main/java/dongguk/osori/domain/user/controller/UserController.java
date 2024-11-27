package dongguk.osori.domain.user.controller;

import dongguk.osori.domain.user.dto.*;
import dongguk.osori.domain.user.service.EmailService;
import dongguk.osori.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "이메일 인증 코드 전송", description = "회원가입을 위해 이메일로 인증 코드를 전송합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증 코드 전송 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 이메일 입력")
    })
    @PostMapping("/signup/send-email")
    public ResponseEntity<String> sendSignupEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(400).body("이메일이 올바르지 않습니다.");
        }
        emailService.sendEmail(email);
        return ResponseEntity.ok("인증 코드가 이메일로 전송되었습니다.");
    }

    @Operation(summary = "회원가입", description = "이메일 인증을 완료한 사용자가 회원가입을 진행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "이메일 인증 실패 또는 잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupUserDto signupUserDto) {
        try {
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

    @Operation(summary = "유저 프로필 조회", description = "로그인한 사용자의 프로필 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getUserProfile(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        UserProfileDto userDto = userService.getUserProfile(userId);
        return ResponseEntity.ok(userDto);
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 실패"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
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

    @Operation(summary = "로그아웃", description = "사용자를 로그아웃하고 세션을 무효화합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "유저 프로필 수정", description = "로그인한 사용자의 프로필 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
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

    @Operation(summary = "현재 비밀번호 확인", description = "비밀번호 확인용 엔드포인트로, 현재 비밀번호가 맞는지 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 확인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 비밀번호"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
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

    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호를 확인 후 새로운 비밀번호로 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 현재 비밀번호 불일치"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
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
            boolean isVerified = userService.verifyPassword(userId, new PasswordDto(currentPassword));
            if (!isVerified) {
                return ResponseEntity.status(400).body("현재 비밀번호가 일치하지 않습니다.");
            }
            userService.updatePassword(userId, new PasswordDto(newPassword));
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } catch (Exception e) {
            log.error("Unexpected error during password update", e);
            return ResponseEntity.status(500).body("서버 에러 발생. 나중에 다시 시도해주세요.");
        }
    }
}
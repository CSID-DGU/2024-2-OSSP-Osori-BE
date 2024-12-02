package dongguk.osori.domain.follow;

import dongguk.osori.domain.follow.dto.*;
import dongguk.osori.domain.follow.service.FollowService;
import dongguk.osori.domain.user.entity.User;
import dongguk.osori.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/follows")
public class FollowController {

    private final FollowService followService;
    private final UserService userService;

    @Operation(summary = "내 팔로잉 목록 및 팔로잉 수 조회", description = "로그인한 사용자의 팔로잉 목록과 팔로잉 수를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "팔로잉 목록 및 수 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @GetMapping("/following")
    public ResponseEntity<MyFollowResponse> getMyFollowingList(@SessionAttribute(name = "userId", required = false) Long userId) {
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        List<FollowDto> followings = followService.getMyFollowings(userId);
        int followingCount = followService.getFollowingCount(userId);

        MyFollowResponse response = new MyFollowResponse(followings, followingCount);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내 팔로워 목록 및 팔로워 수 조회", description = "로그인한 사용자의 팔로워 목록과 팔로워 수를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "팔로워 목록 및 수 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @GetMapping("/followers")
    public ResponseEntity<MyFollowResponse> getMyFollowerList(@SessionAttribute(name = "userId", required = false) Long userId) {
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        List<FollowDto> followers = followService.getMyFollowers(userId);
        int followerCount = followService.getFollowerCount(userId);

        MyFollowResponse response = new MyFollowResponse(followers, followerCount);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "이메일로 사용자 팔로우", description = "이메일로 사용자를 검색해 팔로우합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "팔로우 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "해당 이메일의 사용자를 찾을 수 없음")
    })
    @PostMapping()
    public ResponseEntity<Void> followUserByEmail(@SessionAttribute(name = "userId", required = false) Long userId,
                                                  @RequestBody FollowRequestDto followRequestDto) {
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            followService.followUserByEmail(userId, followRequestDto.getEmail());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    @Operation(summary = "언팔로우", description = "사용자를 언팔로우합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "언팔로우 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @DeleteMapping()
    public ResponseEntity<Void> unfollowUser(@SessionAttribute(name = "userId", required = false) Long userId,
                                             @RequestBody UnfollowRequestDto unfollowRequestDto) {
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        followService.unfollowUser(userId, unfollowRequestDto.getFollowingId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "팔로워 끊기", description = "팔로워를 끊습니다. (차단 기능)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "팔로워 끊기 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @DeleteMapping("/block")
    public ResponseEntity<Void> blockFollower(@SessionAttribute(name = "userId", required = false) Long userId,
                                              @RequestBody BlockFollowerRequestDto blockFollowerRequestDto) {
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        followService.blockFollower(userId, blockFollowerRequestDto.getFollowerId());
        return ResponseEntity.ok().build();
    }
}

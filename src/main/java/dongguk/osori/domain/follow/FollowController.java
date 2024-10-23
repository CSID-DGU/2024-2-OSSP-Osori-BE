package dongguk.osori.domain.follow;

import dongguk.osori.domain.follow.dto.BlockFollowerRequestDto;
import dongguk.osori.domain.follow.dto.FollowDto;
import dongguk.osori.domain.follow.dto.FollowRequestDto;
import dongguk.osori.domain.follow.dto.UnfollowRequestDto;
import dongguk.osori.domain.user.entity.User;
import dongguk.osori.domain.user.service.UserService;
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

    // 내 팔로잉 목록 조회
    @GetMapping
    public ResponseEntity<List<FollowDto>> getMyFollowings(@SessionAttribute(name = "userId", required = false) Long userId) {
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        List<FollowDto> followings = followService.getMyFollowings(userId);
        return ResponseEntity.ok(followings);
    }


    // 이메일로 팔로우
    @PostMapping()
    public ResponseEntity<Void> followUserByEmail(@SessionAttribute(name = "userId", required = false) Long userId, @RequestBody FollowRequestDto followRequestDto) {
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        User userToFollow = userService.findUserByEmail(followRequestDto.getEmail());
        if (userToFollow == null) {
            return ResponseEntity.status(404).build();
        }

        followService.followUser(userId, userToFollow.getUserId());
        return ResponseEntity.ok().build();
    }


    // 언팔로우
    @DeleteMapping()
    public ResponseEntity<Void> unfollowUser(@SessionAttribute(name = "userId", required = false) Long userId,
                                             @RequestBody UnfollowRequestDto unfollowRequestDto) {
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        followService.unfollowUser(userId, unfollowRequestDto.getFollowingId());
        return ResponseEntity.ok().build();
    }


    // 팔로워 끊기
    // TODO: 팔로워 끊기와 언팔로우 통합 방안 고려
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

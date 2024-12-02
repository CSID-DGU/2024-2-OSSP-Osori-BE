package dongguk.osori.domain.follow.service;

import dongguk.osori.domain.follow.FollowRepository;
import dongguk.osori.domain.follow.dto.FollowDto;
import dongguk.osori.domain.follow.entity.Follow;
import dongguk.osori.domain.user.repository.UserRepository;
import dongguk.osori.domain.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    // 내 팔로잉 목록 조회
    public List<FollowDto> getMyFollowings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        return followRepository.findByFollower(user).stream()
                .map(f -> new FollowDto(
                        f.getFollowing().getUserId(),
                        f.getFollowing().getNickname(),
                        f.getFollowing().getEmail()
                ))
                .collect(Collectors.toList());
    }

    // 내 팔로워 목록 조회
    public List<FollowDto> getMyFollowers(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        return followRepository.findByFollowing(user).stream()
                .map(f -> new FollowDto(
                        f.getFollower().getUserId(),
                        f.getFollower().getNickname(),
                        f.getFollower().getEmail()
                ))
                .collect(Collectors.toList());
    }

    // 팔로워 및 팔로잉 수 조회
    public int getFollowingCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        return followRepository.findByFollower(user).size();
    }

    public int getFollowerCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        return followRepository.findByFollowing(user).size();
    }



    @Transactional
    public void followUser(Long userId, Long followingUserId) {
        User loggedInUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        User followingUser = userRepository.findById(followingUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + followingUserId));

        // 이미 팔로우 관계가 존재하는지 확인
        boolean alreadyFollowing = followRepository.findByFollowerAndFollowing(loggedInUser, followingUser).isPresent();
        if (alreadyFollowing) {
            throw new IllegalArgumentException("You are already following this user.");
        }

        Follow follow = new Follow(loggedInUser, followingUser);
        followRepository.save(follow);
    }

    @Transactional
    public void followUserByEmail(Long userId, String email) {
        User follower = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Logged-in user not found with id: " + userId));

        User following = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        boolean alreadyFollowing = followRepository.findByFollowerAndFollowing(follower, following).isPresent();
        if (alreadyFollowing) {
            throw new IllegalArgumentException("You are already following this user.");
        }

        followRepository.save(new Follow(follower, following));
    }


    // 언팔로우
    @Transactional
    public void unfollowUser(Long userId, Long followingId) {
        // 현재 로그인된 사용자 가져오기
        User follower = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Follower not found"));
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new IllegalArgumentException("Following not found"));

        followRepository.deleteByFollowerAndFollowing(follower, following);
    }

    // 팔로워 끊기
    @Transactional
    public void blockFollower(Long userId, Long followerId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("Follower not found"));
        User following = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        followRepository.deleteByFollowerAndFollowing(follower, following);
    }

}

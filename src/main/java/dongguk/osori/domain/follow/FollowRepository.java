package dongguk.osori.domain.follow;

import dongguk.osori.domain.follow.entity.Follow;
import dongguk.osori.domain.user.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    // 사용자가 팔로우하는 사람 목록 조회
    List<Follow> findByFollower(User follower);

    // 사용자를 팔로우하는 사람 목록 조회
    List<Follow> findByFollowing(User following);

    // 특정 팔로우 관계 존재 확인
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    // 팔로우 관계 삭제
    void deleteByFollowerAndFollowing(User follower, User following);
}
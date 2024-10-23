package dongguk.osori.domain.follow;

import dongguk.osori.domain.follow.entity.Follow;
import dongguk.osori.domain.user.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    List<Follow> findByFollower(User follower);
    void deleteByFollowerAndFollowing(User follower, User following);
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

}

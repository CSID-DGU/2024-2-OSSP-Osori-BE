package dongguk.osori.domain.goal.repository;

import dongguk.osori.domain.goal.entity.Goal;
import dongguk.osori.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    Optional<Goal> findByGoalId(Long goalId);
    Optional<Goal> findByGoalIdAndUser(Long goalId, User user);
    List<Goal> findByUserInAndCreatedAtBetween(List<User> users, LocalDateTime start, LocalDateTime end);
    List<Goal> findByUserAndCreatedAtBetween(User user, LocalDateTime start, LocalDateTime end);


}


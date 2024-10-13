package dongguk.osori.domain.goal;

import dongguk.osori.domain.goal.entity.Goal;
import dongguk.osori.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GoalRepository extends JpaRepository <Goal,Long> {
    Optional<Goal> findByGoalIdAndUser(Long goalId, User user);
}

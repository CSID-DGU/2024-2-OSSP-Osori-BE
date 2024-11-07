package dongguk.osori.domain.goal.repository;

import dongguk.osori.domain.goal.entity.GoalComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalCommentRepository extends JpaRepository<GoalComment, Long> {
    List<GoalComment> findByGoal_GoalId(Long goalId);
}

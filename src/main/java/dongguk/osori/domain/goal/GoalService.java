package dongguk.osori.domain.goal;

import dongguk.osori.domain.goal.dto.GoalCompletionDto;
import dongguk.osori.domain.goal.dto.GoalDto;
import dongguk.osori.domain.goal.entity.Goal;
import dongguk.osori.domain.user.entity.User;
import dongguk.osori.domain.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    // 로그인된 사용자 가져오기
    private User getLoggedInUser(Long userId) {
        log.debug("Fetching user with ID: {}", userId); // 디버깅용
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


    // 로그인된 사용자의 모든 목표 조회
    public List<Goal> getUserGoals(Long userId) {
        User user = getLoggedInUser(userId);
        log.debug("Goals retrieved: {}", user.getGoals()); // 디버깅용
        return user.getGoals();
    }

    // 로그인된 사용자의 목표 생성
    @Transactional
    public Goal createGoal(GoalDto goalDto, Long userId) {
        log.debug("Creating goal for user ID: {}", userId); // 디버깅용
        User user = getLoggedInUser(userId);
        log.debug("User found: {}", user); // 디버깅용

        // Goal Builder 사용하여 새로운 목표 생성
        Goal goal = Goal.builder()
                .context(goalDto.getContext())
                .user(user)
                .build();
        log.debug("New goal created: {}", goal); // 디버깅용

        user.getGoals().add(goal);
        return goalRepository.save(goal);
    }

    // 로그인된 사용자의 목표 수정
    @Transactional
    public Optional<Goal> updateGoal(Long goalId, GoalDto goalDto, Long userId) {
        User user = getLoggedInUser(userId);
        return goalRepository.findByGoalIdAndUser(goalId, user)
                .map(goal -> {
                    goal.updateContext(goalDto.getContext());
                    return goal;
                });
    }


    // 로그인된 사용자의 목표 달성 여부 업데이트
    @Transactional
    public Optional<Goal> updateGoalCompletionStatus(Long goalId, GoalCompletionDto goalCompletionDto, Long userId) {
        User user = getLoggedInUser(userId);
        return goalRepository.findById(goalId)
                .filter(goal -> user.getGoals().contains(goal))
                .map(goal -> {
                    if (goalCompletionDto.isCompleted()) {
                        goal.markAsCompleted();
                    } else {
                        goal.markAsIncomplete();
                    }
                    return goal;
                });
    }

    // 목표 삭제
    @Transactional
    public String deleteGoal(Long userId, Long goalId) {
        log.debug("Deleting goal ID: {} for user ID: {}", goalId, userId); // 디버깅용
        Goal goal = goalRepository.findById(goalId)
                        .orElseThrow(() -> new RuntimeException("존재하지 않는 목표"));

        User user = getLoggedInUser(userId);
        log.debug("User found: {}, Goal user: {}", user, goal.getUser()); // 디버깅용

        if(!user.equals(goal.getUser())) {
            log.warn("User mismatch: {} is not the owner of goal ID: {}", userId, goalId); // 디버깅용
            return "잘못된 사용자, 삭제 실패";
        }
        user.getGoals().remove(goal); // User 엔티티에서 해당 목표를 제거
        goalRepository.deleteById(goalId); // Goal에서 목표 삭제
        log.debug("Goal deleted successfully: {}", goalId); // 디버깅용
        return "아코자국 삭제 완료";
    }


}

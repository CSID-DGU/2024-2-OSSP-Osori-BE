package dongguk.osori.domain.goal;

import dongguk.osori.domain.goal.dto.GoalCompletionDto;
import dongguk.osori.domain.goal.dto.GoalDto;
import dongguk.osori.domain.goal.entity.Goal;
import dongguk.osori.domain.goal.service.GoalService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;

    // 로그인한 사용자의 목표 조회
    @GetMapping
    public ResponseEntity<List<Goal>> getUserGoals(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        List<Goal> goals = goalService.getUserGoals(userId);
        return ResponseEntity.ok(goals);
    }

    // 로그인한 사용자의 목표 생성
    @PostMapping
    public ResponseEntity<Goal> createGoal(@RequestBody GoalDto goalDto, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        Goal createdGoal = goalService.createGoal(goalDto, userId);
        return ResponseEntity.ok(createdGoal);
    }

    // 로그인한 사용자의 목표 수정
    @PatchMapping("/{goalId}")
    public ResponseEntity<Goal> updateGoal(@PathVariable("goalId") Long goalId, @RequestBody GoalDto goalDto, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        Optional<Goal> updatedGoal = goalService.updateGoal(goalId, goalDto, userId);
        return updatedGoal.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 로그인한 사용자의 목표 삭제
    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(@PathVariable("goalId") Long goalId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        goalService.deleteGoal(goalId, userId);
        return ResponseEntity.noContent().build();
    }

    // 로그인한 사용자의 목표 달성 여부 수정
    @PatchMapping("/{goalId}/completion")
    public ResponseEntity<Goal> updateGoalCompletion(@PathVariable("goalId") Long goalId, @RequestBody GoalCompletionDto goalCompletionDto, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        Optional<Goal> updatedGoal = goalService.updateGoalCompletionStatus(goalId, goalCompletionDto, userId);
        return updatedGoal.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

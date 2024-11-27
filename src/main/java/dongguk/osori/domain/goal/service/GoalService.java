package dongguk.osori.domain.goal.service;

import dongguk.osori.domain.goal.dto.*;
import dongguk.osori.domain.goal.repository.GoalCommentRepository;
import dongguk.osori.domain.goal.repository.GoalRepository;
import dongguk.osori.domain.goal.entity.Goal;
import dongguk.osori.domain.user.entity.User;
import dongguk.osori.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final GoalCommentRepository goalCommentRepository;

    // 로그인된 사용자 가져오기
    private User getLoggedInUser(Long userId) {
        log.debug("Fetching user with ID: {}", userId); // 디버깅용
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // 로그인된 사용자의 모든 목표 조회
    public List<GoalResponseDto> getUserGoals(Long userId) {
        User user = getLoggedInUser(userId);
        log.debug("Goals retrieved: {}", user.getGoals()); // 디버깅용
        return user.getGoals().stream()
                .map(goal -> new GoalResponseDto(goal.getGoalId(), goal.getContent(), goal.getCreatedAt(), goal.isCompleted()))
                .collect(Collectors.toList());
    }

    // 로그인된 사용자의 목표 생성
    @Transactional
    public GoalResponseDto createGoal(GoalDto goalDto, Long userId) {
        log.debug("Creating goal for user ID: {}", userId); // 디버깅용
        User user = getLoggedInUser(userId);
        log.debug("User found: {}", user); // 디버깅용

        // Goal Builder 사용하여 새로운 목표 생성
        Goal goal = Goal.builder()
                .content(goalDto.getContent())
                .user(user)
                .build();
        log.debug("New goal created: {}", goal); // 디버깅용

        goalRepository.save(goal);
        return new GoalResponseDto(goal.getGoalId(), goal.getContent(), goal.getCreatedAt(), goal.isCompleted());
    }

    // 로그인된 사용자의 목표 수정
    @Transactional
    public Optional<GoalResponseDto> updateGoal(Long goalId, GoalDto goalDto, Long userId) {
        User user = getLoggedInUser(userId);
        return goalRepository.findByGoalIdAndUser(goalId, user)
                .map(goal -> {
                    goal.updateContent(goalDto.getContent());
                    return new GoalResponseDto(goal.getGoalId(), goal.getContent(), goal.getCreatedAt(), goal.isCompleted());
                });
    }

    // 로그인된 사용자의 목표 달성 여부 업데이트
    @Transactional
    public Optional<GoalResponseDto> updateGoalCompletionStatus(Long goalId, GoalCompletionDto goalCompletionDto, Long userId) {
        User user = getLoggedInUser(userId);
        return goalRepository.findById(goalId)
                .filter(goal -> user.getGoals().contains(goal))
                .map(goal -> {
                    if (goalCompletionDto.isCompleted()) {
                        goal.markAsCompleted();
                    } else {
                        goal.markAsIncomplete();
                    }
                    return new GoalResponseDto(goal.getGoalId(), goal.getContent(), goal.getCreatedAt(), goal.isCompleted());
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
        goalRepository.deleteById(goalId); // Goal에서 목표 삭제
        log.debug("Goal deleted successfully: {}", goalId); // 디버깅용
        return "목표 삭제 완료";
    }

    // 피드에서 오늘 날짜의 팔로우한 사람들의 목표 조회
    @Transactional
    public List<FeedGoalDto> getTodayFeedGoals(Long userId) {
        User loggedInUser = getLoggedInUser(userId); // 로그인된 사용자 가져오기
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        List<User> followingUsers = loggedInUser.getFollowingUsers(); // 팔로우한 사용자 목록 가져오기

        // 오늘 날짜에 생성된 팔로우한 사용자의 목표 조회
        return goalRepository.findByUserInAndCreatedAtBetween(followingUsers, startOfDay, endOfDay)
                .stream()
                .map(goal -> new FeedGoalDto(
                        goal.getGoalId(),
                        goal.getUser().getNickname(),
                        goal.getCreatedAt(),
                        goal.isCompleted(),
                        goal.getContent(),
                        goal.getComments().size()
                ))
                .collect(Collectors.toList());
    }

    // Goal ID를 기준으로 목표 상세 조회 (댓글 포함)
    @Transactional
    public Optional<GoalDetailResponseDto> getGoalDetailsWithComments(Long goalId) {
        return goalRepository.findById(goalId)
                .map(goal -> {
                    List<GoalCommentResponseDto> comments = goal.getComments().stream()
                            .map(comment -> new GoalCommentResponseDto(
                                    comment.getCommentId(),
                                    comment.getUser().getNickname(),
                                    comment.getContent(),
                                    comment.getCreatedAt(),
                                    comment.getEmoji()
                            ))
                            .collect(Collectors.toList());

                    return new GoalDetailResponseDto(
                            goal.getGoalId(),
                            goal.getContent(),
                            goal.getUser().getNickname(),
                            goal.getCreatedAt(),
                            goal.isCompleted(),
                            comments
                    );
                });
    }
}

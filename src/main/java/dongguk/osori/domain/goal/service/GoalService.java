package dongguk.osori.domain.goal.service;

import dongguk.osori.domain.goal.dto.*;
import dongguk.osori.domain.goal.repository.GoalCommentRepository;
import dongguk.osori.domain.goal.repository.GoalRepository;
import dongguk.osori.domain.goal.entity.Goal;
import dongguk.osori.domain.quest.entity.MissionType;
import dongguk.osori.domain.quest.service.QuestService;
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
    private final QuestService questService;

    // 로그인된 사용자 가져오기
    private User getLoggedInUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // 로그인된 사용자의 모든 목표 조회
    public List<GoalResponseDto> getUserGoals(Long userId) {
        User user = getLoggedInUser(userId);
        return user.getGoals().stream()
                .map(goal -> new GoalResponseDto(goal.getGoalId(), goal.getContent(), goal.getCreatedAt(), goal.isCompleted()))
                .collect(Collectors.toList());
    }

    // 로그인된 사용자의 목표 생성
    @Transactional
    public GoalResponseDto createGoal(GoalDto goalDto, Long userId) {
        User user = getLoggedInUser(userId);

        Goal goal = Goal.builder()
                .content(goalDto.getContent())
                .user(user)
                .build();

        goalRepository.save(goal);

        // 퀘스트 업데이트
        questService.updateMissionStatus(userId, MissionType.GOAL_WRITTEN);

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
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 목표"));

        User user = getLoggedInUser(userId);

        if(!user.equals(goal.getUser())) {
            log.warn("User mismatch: {} is not the owner of goal ID: {}", userId, goalId); // 디버깅용
            return "잘못된 사용자, 삭제 실패";
        }
        goalRepository.deleteById(goalId);
        return "목표 삭제 완료";
    }

    @Transactional
    public List<GoalDetailResponseDto> getTodayFeedGoalsAsDetails(Long userId) {
        User loggedInUser = getLoggedInUser(userId); // 로그인된 사용자 정보 로드
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        // 1. 자신의 목표 조회
        List<Goal> myGoals = goalRepository.findByUserAndCreatedAtBetween(loggedInUser, startOfDay, endOfDay);
        List<GoalDetailResponseDto> myGoalsDtos = myGoals.stream()
                .map(goal -> {
                    List<GoalCommentResponseDto> comments = goal.getComments().stream()
                            .map(comment -> new GoalCommentResponseDto(
                                    comment.getCommentId(),
                                    comment.getUser().getNickname(),
                                    comment.getContent(),
                                    comment.getCreatedAt(),
                                    comment.getEmoji(),
                                    comment.getUser().getUserId().equals(userId) // 댓글 작성자와 로그인된 사용자 ID 비교
                            ))
                            .collect(Collectors.toList());

                    return new GoalDetailResponseDto(
                            goal.getGoalId(),
                            goal.getContent(),
                            goal.getUser().getNickname(),
                            goal.getCreatedAt(),
                            goal.isCompleted(),
                            true, // 내 목표이므로 항상 true
                            comments
                    );
                })
                .collect(Collectors.toList());

        // 2. 팔로우한 사용자들의 목표 조회
        List<User> followingUsers = loggedInUser.getFollowingUsers();
        List<Goal> followingGoals = goalRepository.findByUserInAndCreatedAtBetween(followingUsers, startOfDay, endOfDay);
        List<GoalDetailResponseDto> followingGoalsDtos = followingGoals.stream()
                .map(goal -> {
                    List<GoalCommentResponseDto> comments = goal.getComments().stream()
                            .map(comment -> new GoalCommentResponseDto(
                                    comment.getCommentId(),
                                    comment.getUser().getNickname(),
                                    comment.getContent(),
                                    comment.getCreatedAt(),
                                    comment.getEmoji(),
                                    comment.getUser().getUserId().equals(userId) // 댓글 작성자와 로그인된 사용자 ID 비교
                            ))
                            .collect(Collectors.toList());

                    return new GoalDetailResponseDto(
                            goal.getGoalId(),
                            goal.getContent(),
                            goal.getUser().getNickname(),
                            goal.getCreatedAt(),
                            goal.isCompleted(),
                            false, // 팔로우한 사람의 목표이므로 false
                            comments
                    );
                })
                .collect(Collectors.toList());

        // 3. 결과 리스트 생성: 자신의 목표를 먼저 추가한 후 팔로우한 사람들의 목표 추가
        myGoalsDtos.addAll(followingGoalsDtos);
        return myGoalsDtos;
    }

    @Transactional
    public Optional<GoalDetailResponseDto> getGoalDetailsWithComments(Long goalId, Long userId) {
        User loggedInUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return goalRepository.findById(goalId)
                .map(goal -> {
                    // 댓글 리스트 생성
                    List<GoalCommentResponseDto> comments = goal.getComments().stream()
                            .map(comment -> new GoalCommentResponseDto(
                                    comment.getCommentId(),
                                    comment.getUser().getNickname(),
                                    comment.getContent(),
                                    comment.getCreatedAt(),
                                    comment.getEmoji(),
                                    comment.getUser().getUserId().equals(userId) // 작성자와 로그인된 사용자 비교
                            ))
                            .collect(Collectors.toList());

                    // 목표 생성
                    return new GoalDetailResponseDto(
                            goal.getGoalId(),
                            goal.getContent(),
                            goal.getUser().getNickname(),
                            goal.getCreatedAt(),
                            goal.isCompleted(),
                            goal.getUser().getUserId().equals(userId), // 목표 작성자와 로그인된 사용자 비교
                            comments
                    );
                });
    }

}

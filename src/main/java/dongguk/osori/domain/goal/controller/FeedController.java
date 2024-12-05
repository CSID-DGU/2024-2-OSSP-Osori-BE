package dongguk.osori.domain.goal.controller;

import dongguk.osori.domain.goal.dto.FeedGoalDto;
import dongguk.osori.domain.goal.dto.GoalDetailResponseDto;
import dongguk.osori.domain.goal.service.GoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/feeds")
public class FeedController {

    private final GoalService goalService;

    @Operation(summary = "팔로우한 사람들의 오늘 날짜 목표 조회", description = "팔로우한 사람들의 오늘 날짜 목표를 피드에서 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "목표 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @GetMapping
    public ResponseEntity<List<GoalDetailResponseDto>> getTodayFeedGoals(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        List<GoalDetailResponseDto> feedGoals = goalService.getTodayFeedGoalsAsDetails(userId);
        return ResponseEntity.ok(feedGoals);
    }


    @Operation(summary = "단일 목표 상세 조회", description = "단일 목표와 그에 달린 모든 댓글을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "목표 상세 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "목표를 찾을 수 없음")
    })
    @GetMapping("/{goalId}")
    public ResponseEntity<GoalDetailResponseDto> getGoalDetailsWithComments(
            @PathVariable("goalId") Long goalId,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        Optional<GoalDetailResponseDto> goalDetails = goalService.getGoalDetailsWithComments(goalId, userId);
        return goalDetails.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}

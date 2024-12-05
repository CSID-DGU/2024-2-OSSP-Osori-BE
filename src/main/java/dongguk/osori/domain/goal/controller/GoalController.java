package dongguk.osori.domain.goal.controller;

import dongguk.osori.domain.goal.dto.*;
import dongguk.osori.domain.goal.service.GoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "로그인한 사용자의 목표 조회", description = "세션의 userId를 사용하여 로그인한 사용자의 목표 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "목표 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @GetMapping
    public ResponseEntity<List<GoalResponseDto>> getUserGoals(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        List<GoalResponseDto> goals = goalService.getUserGoals(userId);
        return ResponseEntity.ok(goals);
    }

    @Operation(summary = "목표 단일 조회", description = "특정 목표의 내용을 조회하고 연관된 댓글을 함께 보여줍니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "목표 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "목표를 찾을 수 없음")
    })
    @GetMapping("/{goalId}")
    public ResponseEntity<GoalDetailResponseDto> getGoalById(@PathVariable("goalId") Long goalId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        // isMine
        Optional<GoalDetailResponseDto> goalDetail = goalService.getGoalDetailsWithComments(goalId, userId);
        return goalDetail.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }



    @Operation(summary = "로그인한 사용자의 목표 생성", description = "로그인한 사용자의 새로운 목표를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "목표 생성 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @PostMapping
    public ResponseEntity<GoalResponseDto> createGoal(@RequestBody GoalDto goalDto, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        GoalResponseDto createdGoal = goalService.createGoal(goalDto, userId);
        return ResponseEntity.ok(createdGoal);
    }

    @Operation(summary = "로그인한 사용자의 목표 수정", description = "로그인한 사용자의 기존 목표를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "목표 수정 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "목표를 찾을 수 없음")
    })
    @PatchMapping("/{goalId}")
    public ResponseEntity<GoalResponseDto> updateGoal(@PathVariable("goalId") Long goalId, @RequestBody GoalDto goalDto, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        Optional<GoalResponseDto> updatedGoal = goalService.updateGoal(goalId, goalDto, userId);
        return updatedGoal.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "로그인한 사용자의 목표 삭제", description = "로그인한 사용자의 기존 목표를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "목표 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(@PathVariable("goalId") Long goalId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        goalService.deleteGoal(userId, goalId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "로그인한 사용자의 목표 달성 여부 수정", description = "로그인한 사용자의 목표 달성 상태를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "목표 달성 상태 수정 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "목표를 찾을 수 없음")
    })
    @PatchMapping("/{goalId}/completion")
    public ResponseEntity<GoalResponseDto> updateGoalCompletion(@PathVariable("goalId") Long goalId, @RequestBody GoalCompletionDto goalCompletionDto, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        Optional<GoalResponseDto> updatedGoal = goalService.updateGoalCompletionStatus(goalId, goalCompletionDto, userId);
        return updatedGoal.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

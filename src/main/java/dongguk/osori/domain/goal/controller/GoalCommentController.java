package dongguk.osori.domain.goal.controller;

import dongguk.osori.domain.goal.dto.GoalCommentDto;
import dongguk.osori.domain.goal.dto.GoalCommentRequestDto;
import dongguk.osori.domain.goal.dto.GoalCommentResponseDto;
import dongguk.osori.domain.goal.service.GoalCommentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/goals/{goalId}/comments")
@RequiredArgsConstructor
@Tag(name = "목표 댓글", description = "목표에 대한 댓글 관리 API")
public class GoalCommentController {

    private final GoalCommentService goalCommentService;

    // 댓글 추가
    @Operation(summary = "댓글 추가", description = "특정 목표에 댓글을 추가합니다.")
    @PostMapping
    public ResponseEntity<GoalCommentResponseDto> addComment(
            @Parameter(description = "댓글을 추가할 목표의 ID", required = true)
            @PathVariable("goalId") Long goalId,
            @Parameter(description = "댓글의 내용 (이모지와 내용 포함)", required = true)
            @RequestBody GoalCommentRequestDto goalRequestDto,
            @Parameter(hidden = true) HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        GoalCommentResponseDto response = goalCommentService.addComment(goalId, userId, goalRequestDto); // goalRequestDto로 변경
        return ResponseEntity.ok(response);
    }


    // 댓글 삭제
    @Operation(summary = "댓글 삭제", description = "특정 목표의 댓글을 삭제합니다.")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "댓글이 달린 목표의 ID", required = true)
            @PathVariable("goalId") Long goalId,
            @Parameter(description = "삭제할 댓글의 ID", required = true)
            @PathVariable("commentId") Long commentId,
            @Parameter(description = "댓글 삭제를 요청하는 사용자의 ID", hidden = true)
            @RequestAttribute("userId") Long userId) {

        goalCommentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }
}

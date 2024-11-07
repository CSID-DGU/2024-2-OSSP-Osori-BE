package dongguk.osori.domain.goal.controller;

import dongguk.osori.domain.goal.dto.GoalCommentDto;
import dongguk.osori.domain.goal.dto.GoalCommentResponseDto;
import dongguk.osori.domain.goal.service.GoalCommentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/goals/{goalId}/comments")
@RequiredArgsConstructor
public class GoalCommentController {

    private final GoalCommentService goalCommentService;

    // 댓글 추가
    @PostMapping
    public ResponseEntity<GoalCommentResponseDto> addComment(
            @PathVariable("goalId") Long goalId,
            @RequestBody GoalCommentDto commentDto,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        GoalCommentResponseDto response = goalCommentService.addComment(goalId, userId, commentDto);
        return ResponseEntity.ok(response);
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable("goalId") Long goalId,
            @PathVariable("commentId") Long commentId,
            @RequestAttribute("userId") Long userId) {
        goalCommentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }
}

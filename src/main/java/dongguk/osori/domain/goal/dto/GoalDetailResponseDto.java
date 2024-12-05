package dongguk.osori.domain.goal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class GoalDetailResponseDto {
    private Long goalId;
    private String content;
    private String nickname;
    private LocalDateTime createdAt;
    private boolean completed;
    private boolean isMine;
    private List<GoalCommentResponseDto> comments;
}

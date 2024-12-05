package dongguk.osori.domain.goal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class GoalCommentResponseDto {
    private Long commentId;
    private String nickname;
    private String content;
    private LocalDateTime createdAt;
    private int emoji;
    private boolean isMine;
}


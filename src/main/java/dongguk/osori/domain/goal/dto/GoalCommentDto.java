package dongguk.osori.domain.goal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GoalCommentDto {
    private Long commentId;
    private String nickname;
    private String content;
    private LocalDateTime createdAt;
    private int emoji;
}

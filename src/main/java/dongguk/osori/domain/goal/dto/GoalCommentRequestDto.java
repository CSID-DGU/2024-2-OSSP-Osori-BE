package dongguk.osori.domain.goal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GoalCommentRequestDto {
    private String content;
    private int emoji;
}

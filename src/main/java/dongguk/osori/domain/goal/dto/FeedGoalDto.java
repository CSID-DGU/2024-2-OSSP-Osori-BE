package dongguk.osori.domain.goal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class FeedGoalDto {
    private Long goalId;
    private String authorNickname;
    private LocalDateTime createdAt;
    private boolean completed;
    private String content;
    private int commentCount;
}

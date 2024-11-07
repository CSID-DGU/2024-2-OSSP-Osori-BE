package dongguk.osori.domain.goal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoalResponseDto {
    private Long goalId;
    private String content;
    private LocalDateTime createdAt;
    private boolean completed;
}

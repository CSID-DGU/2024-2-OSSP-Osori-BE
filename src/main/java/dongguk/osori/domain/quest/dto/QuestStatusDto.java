package dongguk.osori.domain.quest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuestStatusDto {
    private boolean attended;
    private boolean goalWritten;
    private boolean commentedOnFriendGoal;
    private boolean portfolioWritten;
}


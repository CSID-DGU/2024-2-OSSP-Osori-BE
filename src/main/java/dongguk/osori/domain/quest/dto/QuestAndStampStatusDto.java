package dongguk.osori.domain.quest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuestAndStampStatusDto {
    private QuestStatusDto questStatus;
    private int stampCount;
    private boolean fullyCompleted;
}

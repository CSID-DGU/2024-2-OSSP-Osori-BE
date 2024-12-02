package dongguk.osori.domain.quest.dto;

import dongguk.osori.domain.quest.entity.MissionType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MissionRequestDto {
    private LocalDate date;
    private MissionType missionType;
}

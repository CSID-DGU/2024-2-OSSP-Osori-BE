package dongguk.osori.domain.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExperienceDto {
    private String situation;
    private String task;
    private String action;
    private String result;
}

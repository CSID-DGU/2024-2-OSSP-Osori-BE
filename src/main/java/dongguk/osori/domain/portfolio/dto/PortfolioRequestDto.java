package dongguk.osori.domain.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioRequestDto {
    private String name;
    private LocalDate startDate;
    private List<String> tags;
    private ExperienceDto experience;
    private PmiDto pmi;
    private List<String> photoUrls;
}

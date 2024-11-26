package dongguk.osori.domain.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioDetailDto {
    private PortfolioBaseDto baseInfo;
    private ExperienceDto experience;
    private PmiDto pmi;

}

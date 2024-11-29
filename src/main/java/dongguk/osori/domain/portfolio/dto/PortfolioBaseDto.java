package dongguk.osori.domain.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
/*
목록 조회 dto
 */
public class PortfolioBaseDto {
    private Long portfolioId;
    private String name;
    private LocalDateTime updatedAt;
    private LocalDate startDate;
    private List<String> tags;
}

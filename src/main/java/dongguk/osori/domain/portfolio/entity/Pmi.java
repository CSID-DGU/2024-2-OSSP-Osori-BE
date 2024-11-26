package dongguk.osori.domain.portfolio.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Pmi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pmiId;

    private String plus;
    private String minus;
    private String interesting;

    public Pmi(String plus, String minus, String interesting) {
        this.plus = plus;
        this.minus = minus;
        this.interesting = interesting;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public void update(String plus, String minus, String interesting) {
        this.plus = plus;
        this.minus = minus;
        this.interesting = interesting;
    }

    @OneToOne
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;
}

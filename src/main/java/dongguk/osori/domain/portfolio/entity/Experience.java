package dongguk.osori.domain.portfolio.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long experienceId;

    private String situation;
    private String task;
    private String action;
    private String result;



    public Experience(String situation, String task, String action, String result) {
        this.situation = situation;
        this.task = task;
        this.action = action;
        this.result = result;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public void update(String situation, String task, String action, String result) {
        this.situation = situation;
        this.task = task;
        this.action = action;
        this.result = result;
    }

    @OneToOne
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;
}

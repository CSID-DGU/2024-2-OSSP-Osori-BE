package dongguk.osori.domain.quest.entity;

import dongguk.osori.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class Quest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date; // 퀘스트가 속한 날짜

    @Column(nullable = false)
    private boolean attended; // 출석 여부

    @Column(nullable = false)
    private boolean goalWritten; // 목표 작성 여부

    @Column(nullable = false)
    private boolean commentedOnFriendGoal; // 친구 목표에 댓글 단 여부

    @Column(nullable = false)
    private boolean portfolioWritten; // 포트폴리오 작성 여부

    public Quest(User user, LocalDate date) {
        this.user = user;
        this.date = date;
    }

    public boolean isAllMissionsCompleted() {
        return attended && goalWritten && commentedOnFriendGoal && portfolioWritten;
    }

    public void markAttended() {
        this.attended = true;
    }

    public void markGoalWritten() {
        this.goalWritten = true;
    }

    public void markCommentedOnFriendGoal() {
        this.commentedOnFriendGoal = true;
    }

    public void markPortfolioWritten() {
        this.portfolioWritten = true;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}


package dongguk.osori.domain.quest.entity;

import dongguk.osori.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Stamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int weekNumber; // 몇 주차 스탬프인지

    @Column(nullable = false)
    private int year; // 해당 연도

    @Column(nullable = false)
    private int count; // 스탬프 개수 (최대 7)

    public Stamp(User user, int weekNumber, int year) {
        this.user = user;
        this.weekNumber = weekNumber;
        this.year = year;
    }

    public void incrementStamp() {
        if (count < 7) {
            count++;
        }
    }

    public boolean isFullyCompleted() {
        return count == 7;
    }

    public void resetStampCount() {
        this.count = 0;
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}


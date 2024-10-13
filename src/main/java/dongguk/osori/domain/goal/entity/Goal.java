package dongguk.osori.domain.goal.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dongguk.osori.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Entity
public class Goal {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long goalId;

    @Column(nullable = false)
    private String context;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean completed = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;


    public void updateContext(String context) { this.context = context; }
    public void markAsCompleted() { this.completed = true; }
    public void markAsIncomplete() { this.completed = false; }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.completed = false;
    }

    @Builder
    public Goal(String context, User user) {
        this.context = context;
        this.user = user;
    }

}

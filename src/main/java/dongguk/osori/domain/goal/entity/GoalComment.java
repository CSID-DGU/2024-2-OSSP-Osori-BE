package dongguk.osori.domain.goal.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dongguk.osori.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class GoalComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int emoji;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id")
    @JsonIgnore
    private Goal goal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Builder
    public GoalComment(String content, int emoji, Goal goal, User user) {
        this.content = content;
        this.emoji = emoji;
        this.goal = goal;
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }

    public void updateCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void updateCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateEmoji(int emoji) {
        this.emoji = emoji;
    }

    public void updateGoal(Goal goal) {
        this.goal = goal;
    }

    public void updateUser(User user) {
        this.user = user;
    }
}

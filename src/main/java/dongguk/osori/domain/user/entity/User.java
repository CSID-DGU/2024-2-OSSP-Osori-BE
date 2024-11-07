package dongguk.osori.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import dongguk.osori.domain.follow.entity.Follow;
import dongguk.osori.domain.goal.entity.Goal;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Entity
@Table(name = "users")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private String major;

    @Column(nullable = true)
    private int studentNumber;

    @Column(nullable = true)
    private String introduce;

    private Integer balance;

    public void updateNickName(String nickname) {
        this.nickname = nickname;
    }
    public void updateEmail(String email) { this.email = email; }
    public void updatePassword(String password) { this.password = password; }
    public void updateMajor(String major) { this.major = major; }
    public void updateStudentNumber(int studentNumber) { this.studentNumber = studentNumber; }
    public void updateIntroduce(String introduce) { this.introduce = introduce; }
    public void updateBalance(int amount) {
        if (this.balance == null) {
            this.balance = 0;
        }
        this.balance += amount;
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Goal> goals = new ArrayList<>();

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> following = new ArrayList<>();

    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followers = new ArrayList<>();

    // 팔로우하는 사용자 목록 반환
    public List<User> getFollowingUsers() {
        return following.stream()
                .map(Follow::getFollowing)
                .collect(Collectors.toList());
    }
}

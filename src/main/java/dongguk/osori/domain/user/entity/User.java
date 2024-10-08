package dongguk.osori.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

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



}

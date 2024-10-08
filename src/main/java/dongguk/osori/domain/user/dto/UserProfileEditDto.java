package dongguk.osori.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileEditDto {

    private String nickname;
    private String major;
    private int studentNumber;
    private String introduce;

}

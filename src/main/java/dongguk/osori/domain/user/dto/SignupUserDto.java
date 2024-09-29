package dongguk.osori.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupUserDto {
    private String nickname;
    private String email;
    private String password;
}

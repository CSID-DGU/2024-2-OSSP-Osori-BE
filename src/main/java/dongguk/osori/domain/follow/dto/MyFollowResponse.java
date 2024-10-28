package dongguk.osori.domain.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyFollowResponse {
    private List<FollowDto> follows;
    private int count;
}

package zxcv.asdf.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class page3_solve_1 {
    private Long lectureId;
    private String title;

    private String description;

    private String hwTest1;
    private String hwTestAnswer1;
}
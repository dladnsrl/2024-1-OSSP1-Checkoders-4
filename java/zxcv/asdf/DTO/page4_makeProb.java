package zxcv.asdf.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class page4_makeProb {
    private String title;

    private String description;
    private LocalDateTime deadline;

    private String hwTest1;
    private String hwTestAnswer1;
    private String hwTest2;
    private String hwTestAnswer2;
    private String hwTest3;
    private String hwTestAnswer3;
    private String hwTest4;
    private String hwTestAnswer4;
    private String hwTest5;
    private String hwTestAnswer5;
}

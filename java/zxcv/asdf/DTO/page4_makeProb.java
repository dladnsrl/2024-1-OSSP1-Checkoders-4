package zxcv.asdf.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class page4_makeProb {
    private Long lectureId;
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

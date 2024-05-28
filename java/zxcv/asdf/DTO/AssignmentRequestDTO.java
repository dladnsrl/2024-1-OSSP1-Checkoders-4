package zxcv.asdf.DTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AssignmentRequestDTO {
    private Long lectureId;
    private String title;
    private String description;
    private LocalDateTime deadline;

    private String HwTest1;
    private String HwTestAnswer1;
    private String HwTest2;
    private String HwTestAnswer2;
    private String HwTest3;
    private String HwTestAnswer3;
    private String HwTest4;
    private String HwTestAnswer4;
    private String HwTest5;
    private String HwTestAnswer5;

}
package zxcv.asdf.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentDTO {
    private String title;
    private String description;
    private LocalDateTime deadline;
    private boolean isDone;
}

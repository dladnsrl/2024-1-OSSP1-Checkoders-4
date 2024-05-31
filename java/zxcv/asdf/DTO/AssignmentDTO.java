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
public class AssignmentDTO {
    private String title;
    private String description;
    private LocalDateTime deadline;
    private boolean isDone;
}

package zxcv.asdf.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentListDTO {
    private List<AssignmentDTO> task;           // 과제 리스트
    private List<AssignmentDTO> exercise;  // 학생들이 제출한 문제 리스트
}

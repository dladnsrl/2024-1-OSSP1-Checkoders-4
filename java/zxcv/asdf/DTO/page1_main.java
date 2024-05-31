package zxcv.asdf.DTO;

import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Data;
import zxcv.asdf.domain.Answer;
import zxcv.asdf.domain.Enrollment;
import zxcv.asdf.domain.Lecture;

import java.util.List;

@Data
@Builder
public class page1_main {
    private String token;
    private String name;

    private List<LectureDTO> lectures;
}

package zxcv.asdf.domain;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Lecture {

    Long lecture_id;

    String lectureName;

    List<Assignment> assignments;

    @Builder
    public Lecture(Long lecture_id, String lectureName) {
        this.lecture_id = lecture_id;
        this.lectureName = lectureName;
        this.assignments = new ArrayList<>();
    }


}

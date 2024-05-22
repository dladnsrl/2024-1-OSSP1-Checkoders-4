package zxcv.asdf.domain;


import lombok.Builder;
import lombok.Data;

@Data
public class Assignment {

    String assignmentName;

    Long assignment_madeby;

    @Builder
    public Assignment(String assignmentName, Long assignment_madeby) {
        this.assignmentName = assignmentName;
        this.assignment_madeby = assignment_madeby;
    }
}

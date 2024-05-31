package zxcv.asdf.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LectureAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;


    @ManyToOne
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;

    @OneToMany(mappedBy = "assignment")
    private List<Answer> answers;

    private LocalDateTime deadline;

    private boolean isDone;


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

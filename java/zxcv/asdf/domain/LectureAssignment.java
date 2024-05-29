package zxcv.asdf.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
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

    @ManyToOne
    @JoinColumn(name = "User_token", nullable = false)
    private User user;

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

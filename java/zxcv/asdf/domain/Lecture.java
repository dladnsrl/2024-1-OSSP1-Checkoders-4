package zxcv.asdf.domain;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import lombok.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String course;

    private String madeby;
    private String madeby_name;

    @OneToMany(mappedBy = "lecture")
    private List<Enrollment> enrollments;

    @OneToMany(mappedBy = "lecture")
    private List<LectureAssignment> assignments;
}

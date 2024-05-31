package zxcv.asdf.domain;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @Column(length = 255)
    private String token;

    private String name;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<Enrollment> enrollments;

    @OneToMany(mappedBy = "user")
    private List<Answer> answers;

}

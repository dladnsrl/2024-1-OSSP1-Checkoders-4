package zxcv.asdf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import zxcv.asdf.domain.Lecture;

import java.util.List;
import java.util.Optional;


public interface LectureRepository extends JpaRepository<Lecture, Long> {
    Optional<Lecture> findById(Long id);

    Lecture findByName(String name);

    @Query("SELECT l FROM Lecture l JOIN Enrollment e ON l.id = e.lecture.id WHERE e.user.token = :token")
    List<Lecture> findLecturesByUserToken(@Param("token") String token);
}
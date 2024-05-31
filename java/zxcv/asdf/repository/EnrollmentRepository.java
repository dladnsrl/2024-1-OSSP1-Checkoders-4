package zxcv.asdf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import zxcv.asdf.domain.Enrollment;
import zxcv.asdf.domain.Lecture;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByLectureId(Long lectureId);
    @Query("SELECT e FROM Enrollment e WHERE e.user.token = :token")
    List<Enrollment> findByUserToken(@Param("token") String token);;


}
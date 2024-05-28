package zxcv.asdf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zxcv.asdf.domain.LectureAssignmentMapping;

import java.util.List;

public interface LectureAssignmentMappingRepository extends JpaRepository<LectureAssignmentMapping, Long> {
    List<LectureAssignmentMapping> findByLectureId(Long lectureId);
    List<LectureAssignmentMapping> findByLectureIdAndUserToken(Long lectureId, String userToken);
}
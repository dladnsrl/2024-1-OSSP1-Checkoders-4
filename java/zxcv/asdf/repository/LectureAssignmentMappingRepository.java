package zxcv.asdf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zxcv.asdf.domain.LectureAssignmentMapping;

public interface LectureAssignmentMappingRepository extends JpaRepository<LectureAssignmentMapping, Long> {
    // 필요한 경우 추가적인 커스텀 쿼리 메서드 작성
}
package zxcv.asdf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zxcv.asdf.domain.Lecture;

import java.util.List;
import java.util.Optional;


public interface LectureRepository extends JpaRepository<Lecture, Long> {

    Lecture findByName(String name);
}

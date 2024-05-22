package zxcv.asdf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zxcv.asdf.domain.Comment;

@Repository
public interface commentRepository extends JpaRepository<Comment, Long> {
}

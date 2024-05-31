package zxcv.asdf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import zxcv.asdf.domain.Enrollment;
import zxcv.asdf.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByToken(String token);

    @Query("SELECT u FROM User u WHERE u.token IN :tokens")
    List<User> findAllByTokenIn(@Param("tokens") List<String> tokens);
}

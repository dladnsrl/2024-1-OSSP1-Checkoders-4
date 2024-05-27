package zxcv.asdf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zxcv.asdf.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByToken(String token);
}

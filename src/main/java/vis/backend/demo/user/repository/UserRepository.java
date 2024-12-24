package vis.backend.demo.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import vis.backend.demo.user.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

}

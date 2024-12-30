package vis.backend.demo.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import vis.backend.demo.user.domain.InvestToken;
import vis.backend.demo.user.domain.User;

public interface InvestTokenRepository extends JpaRepository<InvestToken, Long> {
    Optional<InvestToken> findByUser(User user);
}

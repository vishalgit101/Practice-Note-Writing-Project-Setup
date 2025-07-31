package repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import entity.PasswordResetToken;

@Repository
public interface PasswordRestTokenRepo extends JpaRepository<PasswordResetToken, Long> {

	Optional<PasswordResetToken> findByToken(String token);

}

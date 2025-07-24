package repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import entity.Users;

public interface UserRepo extends JpaRepository<Users, Long> {
	
	Optional<Users> findByUsername(String username);
	
	Boolean existsByUsername(String username);

}

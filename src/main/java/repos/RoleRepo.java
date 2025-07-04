package repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import entity.Role;

public interface RoleRepo extends JpaRepository<Role, Long>{
	Optional<Role> findByRole(String role);
}

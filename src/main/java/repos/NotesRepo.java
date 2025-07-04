package repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import entity.Note;

@Repository
public interface NotesRepo extends JpaRepository<Note, Long>{
	List<Note> findByOwnerUsername(String username);
}

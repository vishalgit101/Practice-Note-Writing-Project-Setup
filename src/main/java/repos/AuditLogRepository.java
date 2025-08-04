package repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import entity.AuditLogs;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogs, Long>{
	List<AuditLogs> findByNoteId(Long noteid);
}

package repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import entity.AuditLogs;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogs, Long>{

}

package restControllers;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import entity.AuditLogs;
import services.AuditLogsService;

@RestController
@RequestMapping("/api/audit")
public class AuditLogsController {
	// DI
	private final AuditLogsService auditLogsService;

	public AuditLogsController(AuditLogsService auditLogsService) {
		super();
		this.auditLogsService = auditLogsService;
	}
	
	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public List<AuditLogs> getAuditLogs(){
		return this.auditLogsService.getAllAuditLogs();
	}
	
	@GetMapping("/note/{id}") // this is for when id is essential and @RequestParam when you want some sort of filtering or optional params
	@PreAuthorize("hasRole('ADMIN')")
	public List<AuditLogs> getNoteAuditLog(@PathVariable("id") Long id){
		return this.auditLogsService.getAuditLogsForNoteId(id);
	}
}

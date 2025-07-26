package services;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import entity.AuditLogs;
import entity.Note;
import repos.AuditLogRepository;

@Service
public class AuditLogsServiceImpl implements AuditLogsService {
	
	private final AuditLogRepository auditLogRepo;
	
	public AuditLogsServiceImpl(AuditLogRepository auditLogRepo) {
		super();
		this.auditLogRepo = auditLogRepo;
	}

	@Override
	public void logNoteCreation(String username, Note note) {
		AuditLogs log = new AuditLogs();
		log.setAction("CREATION");
		log.setUsername(username);
		log.setNoteId(note.getId());
		log.setNoteContent(note.getContent());
		log.setTimestamp(LocalDateTime.now());
		this.auditLogRepo.save(log);
	}
	
	@Override
	public void logNoteUpdate(String username, Note note) {
		AuditLogs log = new AuditLogs();
		log.setAction("UPDATE");
		log.setUsername(username);
		log.setNoteId(note.getId());
		log.setNoteContent(note.getContent());
		log.setTimestamp(LocalDateTime.now());
		this.auditLogRepo.save(log);
	}
	
	@Override
	public void logNoteDeletion(String username, Long noteId) {
		AuditLogs log = new AuditLogs();
		log.setAction("DELETE");
		log.setUsername(username);
		log.setNoteId(noteId);
		log.setTimestamp(LocalDateTime.now());
		this.auditLogRepo.save(log);
	}
}

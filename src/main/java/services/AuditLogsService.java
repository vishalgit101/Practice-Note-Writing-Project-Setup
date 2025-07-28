package services;

import java.util.List;

import entity.AuditLogs;
import entity.Note;

public interface AuditLogsService {

	void logNoteCreation(String username, Note note);

	void logNoteUpdate(String username, Note note);

	void logNoteDeletion(String username, Long noteId);

	List<AuditLogs> getAllAuditLogs();

	List<AuditLogs> getAuditLogsForNoteId(Long id);

}

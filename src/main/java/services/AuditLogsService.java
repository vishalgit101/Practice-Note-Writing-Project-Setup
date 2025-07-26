package services;

import entity.Note;

public interface AuditLogsService {

	void logNoteCreation(String username, Note note);

	void logNoteUpdate(String username, Note note);

	void logNoteDeletion(String username, Long noteId);

}

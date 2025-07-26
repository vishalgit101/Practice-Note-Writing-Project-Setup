package services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import entity.Note;
import repos.NotesRepo;

@Service
public class NoteServiceImpl implements NoteService {
	
	private final NotesRepo notesRepo;
	
	private final AuditLogsService auditLogsService;
	
	@Autowired
	public NoteServiceImpl(NotesRepo notesRepo, AuditLogsService auditLogsService) {
		super();
		this.notesRepo = notesRepo;
		this.auditLogsService = auditLogsService;
	}

	@Override
	public Note createNoteForUser(String username, String content) {
		Note tempNote = new Note();
		tempNote.setOwnerUsername(username);
		tempNote.setContent(content);
		this.auditLogsService.logNoteCreation(username, tempNote);
		// add time stamp
		return this.notesRepo.save(tempNote);
	}

	@Override
	public Note updateNoteForUser(Long id, String content, String username) {
		Optional<Note> tempNote = this.notesRepo.findById(id);
		Note note = tempNote.orElseThrow(()-> new RuntimeException("Note not found"));
		note.setContent(content);
		note.setOwnerUsername(username); // this can be omitted
		return this.notesRepo.save(note);
	}

	@Override
	public void deleteNoteForUser(Long id, String username) {
		
		Optional<Note> tempNote = this.notesRepo.findById(id);
		
		Note note = tempNote.orElseThrow(()-> new RuntimeException("Note not found"));
		this.notesRepo.delete(note);
		
	}
	
	public List<Note> getNotesForUser(String username){
		return this.notesRepo.findByOwnerUsername(username);
	}
	
}

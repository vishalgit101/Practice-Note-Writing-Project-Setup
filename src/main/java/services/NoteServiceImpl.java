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
		// add time stamp
		Note savedNote =  this.notesRepo.save(tempNote); // need to flush the note in the dB first a; as the saved note with noteId is returned
		
		//Long noteId = savedNote.getId();  // and then its id becomes available;
		this.auditLogsService.logNoteCreation(username, savedNote);
		
		return savedNote;
	}

	@Override
	public Note updateNoteForUser(Long id, String content, String username) {
		Optional<Note> tempNote = this.notesRepo.findById(id);
		Note note = tempNote.orElseThrow(()-> new RuntimeException("Note not found"));
		note.setContent(content);
		note.setOwnerUsername(username); // this can be omitted
		this.auditLogsService.logNoteUpdate(username, note);
		return this.notesRepo.save(note);
	}

	@Override
	public void deleteNoteForUser(Long id, String username) {
		Optional<Note> tempNote = this.notesRepo.findById(id);
		
		Note note = tempNote.orElseThrow(()-> new RuntimeException("Note not found"));
		this.notesRepo.delete(note);
		this.auditLogsService.logNoteDeletion(username, id);
	}
	
	public List<Note> getNotesForUser(String username){
		return this.notesRepo.findByOwnerUsername(username);
	}
	
}

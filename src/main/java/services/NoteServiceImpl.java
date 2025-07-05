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
	
	@Autowired
	public NoteServiceImpl(NotesRepo notesRepo) {
		super();
		this.notesRepo = notesRepo;
	}

	@Override
	public Note createNoteForUser(String username, String content) {
		Note tempNote = new Note();
		tempNote.setOwnerUsername(username);
		tempNote.setContent(content);
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

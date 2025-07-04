package services;

import entity.Note;

import java.util.*;

import org.springframework.stereotype.Service;

@Service
public interface NoteService {

	Note createNoteForUser(String username, String content);
	
	Note updateNoteForUser(Long id, String content, String username);
	
	void deleteNoteForUser(Long id, String username);
	
	List<Note> getNotesForUser(String username);
	
}

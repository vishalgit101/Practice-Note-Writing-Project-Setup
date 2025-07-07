package restControllers;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import entity.Note;
import services.NoteService;

@RestController
@RequestMapping("/api/notes")
public class NoteController {
	
	private final NoteService noteService;
	
	@Autowired
	public NoteController(NoteService noteService) {
		super();
		this.noteService = noteService;
	}

	@PostMapping
	public Note createNote(@RequestBody String content, @AuthenticationPrincipal UserDetails userDetails ) {
		String username = userDetails.getUsername();
		System.out.println("Username: " + username);
		return this.noteService.createNoteForUser(username, content);
	}
	
	@GetMapping
	public List<Note> getUserNotes(@AuthenticationPrincipal UserDetails userDetails){
		
		String username = userDetails.getUsername();
		System.out.println("Username: In Get Notes Principal: " + username);
		return this.noteService.getNotesForUser(username);
		
	}
	
	@PutMapping("/{noteId}") // for updating
	public Note updateUserNote(@PathVariable Long noteId,  @RequestBody String content, @AuthenticationPrincipal UserDetails userDetails) {
		String username = userDetails.getUsername(); 
		System.out.println("Username: " + username);
		return this.noteService.updateNoteForUser(noteId, content, username);
	}
	
	@DeleteMapping("/{noteId}")
	public void deleteUserNote(@PathVariable Long noteId, @AuthenticationPrincipal UserDetails userDetails ) {
		String username = userDetails.getUsername();
		System.out.println("Username: " + username);
		this.noteService.deleteNoteForUser(noteId, username);
	}
	
}

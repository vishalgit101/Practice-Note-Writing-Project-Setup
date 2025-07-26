package entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name="notes")
public class Note {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;
	
	@Column(name = "content", columnDefinition = "TEXT")
	@Lob
	private String content;
	
	@Column(name="owner_username")
	private String ownerUsername;
	
	// add createation time
	// updation time

	public Note(String content, String ownerUsername) {
		super();
		this.content = content;
		this.ownerUsername = ownerUsername;
	}

	public Note() {
		super();
	}
	
	public Long getId() {
		return id;
	}


	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getOwnerUsername() {
		return ownerUsername;
	}

	public void setOwnerUsername(String ownerUsername) {
		this.ownerUsername = ownerUsername;
	}

	@Override
	public String toString() {
		return "Note [id=" + id + ", content=" + content + ", ownerUsername=" + ownerUsername + "]";
	}
	
	
	
}

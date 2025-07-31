package entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="password_reset")
public class PasswordResetToken {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;
	
	@Column(name="used")
	private boolean used;
	
	@Column(name="token",unique = true)
	private String token;
	
	@JsonIgnore
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="user_id") // referencing the table field name of table password_reset
	private Users user; // owning relationship
	
	@Column(name = "created_at", columnDefinition = "TIMESTAMP")
	private LocalDateTime createdAt;
	
	@Column(name = "expiry", columnDefinition = "TIMESTAMP")
	private LocalDateTime expiry; // set 2h

	public PasswordResetToken() {
		super();
	}


	public PasswordResetToken(Users user) {
		super();
		this.user = user;
		this.createdAt = LocalDateTime.now();
		this.used = false;
		this.token = UUID.randomUUID().toString();
		this.expiry = this.createdAt.plusHours(2);
	}



	public PasswordResetToken(Long id, boolean used, Users user, LocalDateTime createdAt, LocalDateTime expiry) {
		super();
		this.id = id;
		this.used = used;
		this.user = user;
		this.createdAt = createdAt;
		this.expiry = expiry;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getExpiry() {
		return expiry;
	}

	public void setExpiry(LocalDateTime expiry) {
		this.expiry = expiry;
	}
	
	public String getToken() {
		return token;
	}



	public void setToken(String token) {
		this.token = token;
	}



	@Override
	public String toString() {
		return "PasswordResetToken [id=" + id + ", used=" + used + ", token=" + token + ", user=" + user
				+ ", createdAt=" + createdAt + ", expiry=" + expiry + "]";
	}
	
	
	
}

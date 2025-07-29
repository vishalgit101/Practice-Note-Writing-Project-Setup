package entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="users")
public class Users {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name="username")
	private String username;
	
	/*@Column(name="email")
	private String email;*/
	
	@Column(name="password")
	private String password;
	
	@Column(name="created_time")
	private LocalDateTime createdDate;
	
	@Column(name="updated_time")
	private LocalDateTime updatedDate;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name="user_role", joinColumns = @JoinColumn(name="user_id", referencedColumnName = "id"),
	inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
	private Set<Role> roles = new HashSet<Role>();
	
	@Column(name = "account_non_locked")
	private boolean accountNonLocked = true;

	@Column(name = "account_non_expired")
	private boolean accountNonExpired = true;

	@Column(name = "credentials_non_expired")
	private boolean credentialsNonExpired = true;

	@Column(name = "enabled")
	private boolean enabled = true;

	@Column(name = "credentials_expiry_date")
	private LocalDate credentialsExpiryDate;

	@Column(name = "account_expiry_date")
	private LocalDate accountExpiryDate;

	@Column(name = "two_factor_secret")
	private String twoFactorSecret;

	@Column(name = "is_two_factor_enabled")
	private boolean isTwoFactorEnabled = false;

	@Column(name = "sign_up_method")
	private String signUpMethod;	
    
    

	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public LocalDate getCredentialsExpiryDate() {
		return credentialsExpiryDate;
	}

	public void setCredentialsExpiryDate(LocalDate credentialsExpiryDate) {
		this.credentialsExpiryDate = credentialsExpiryDate;
	}

	public LocalDate getAccountExpiryDate() {
		return accountExpiryDate;
	}

	public void setAccountExpiryDate(LocalDate accountExpiryDate) {
		this.accountExpiryDate = accountExpiryDate;
	}

	public String getTwoFactorSecret() {
		return twoFactorSecret;
	}

	public void setTwoFactorSecret(String twoFactorSecret) {
		this.twoFactorSecret = twoFactorSecret;
	}

	public boolean isTwoFactorEnabled() {
		return isTwoFactorEnabled;
	}

	public void setTwoFactorEnabled(boolean isTwoFactorEnabled) {
		this.isTwoFactorEnabled = isTwoFactorEnabled;
	}

	public String getSignUpMethod() {
		return signUpMethod;
	}

	public void setSignUpMethod(String signUpMethod) {
		this.signUpMethod = signUpMethod;
	}

	public Users() {
		super();
	}

	public Users(Long id, String username, String email, String password, LocalDateTime createdDate,
			LocalDateTime updatedDate, Set<Role> roles) {
		super();
		this.id = id;
		this.username = username;
		//this.email = email;
		this.password = password;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
		this.roles = roles;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	/*public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}*/

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDateTime getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(LocalDateTime updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	
	public void addRole(Role role) {
		this.roles.add(role);
		role.getUsers().add(this);
	}

	@Override
	public String toString() {
		return "Users [id=" + id + ", username=" + username + ", password=" + password + ", createdDate=" + createdDate
				+ ", updatedDate=" + updatedDate + "]";
	}
	
	
	
}

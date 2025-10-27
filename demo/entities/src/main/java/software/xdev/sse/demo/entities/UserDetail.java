package software.xdev.sse.demo.entities;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.Optional;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


@Entity
@Table(name = UserDetail.TABLE_NAME)
public class UserDetail extends IdentifiableEntity
{
	public static final String TABLE_NAME = "userdetail";
	
	public static final String COL_EMAIL_ADDRESS = "email_address";
	public static final String COL_DISABLED_AT = "disabled_at";
	
	/**
	 * Voller Name des Nutzers (selbst gesetzt von Nutzer), kann leer sein
	 */
	@NotNull
	@Size(max = DEFAULT_STRING_LENGTH)
	@Column(name = "fullname", length = DEFAULT_STRING_LENGTH, nullable = false)
	private String fullName = "";
	
	@NotNull
	@Size(min = 1, max = DEFAULT_STRING_LENGTH)
	@Column(name = COL_EMAIL_ADDRESS, length = DEFAULT_STRING_LENGTH, nullable = false, unique = true)
	private String emailAddress;
	
	@NotNull
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;
	
	@Nullable
	@Column(name = COL_DISABLED_AT)
	private LocalDateTime disabledAt;
	
	@Nullable
	@Column(name = "last_login_at")
	private LocalDateTime lastLoginAt;
	
	public UserDetail()
	{
		// this is here for JPA
	}
	
	public static UserDetail createNew(
		final String fullName,
		final String email
	)
	{
		final UserDetail user = new UserDetail();
		user.setFullName(fullName);
		user.setEmailAddress(email);
		user.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
		return user;
	}
	
	// Just for tests
	public UserDetail(final long id)
	{
		super(id);
	}
	
	public String getFullName()
	{
		return this.fullName;
	}
	
	public void setFullName(final String fullName)
	{
		this.fullName = Objects.requireNonNull(fullName);
	}
	
	public String getEmailAddress()
	{
		return this.emailAddress;
	}
	
	public void setEmailAddress(final String emailAddress)
	{
		this.emailAddress = Objects.requireNonNull(emailAddress);
	}
	
	public LocalDateTime getCreatedAt()
	{
		return this.createdAt;
	}
	
	public void setCreatedAt(final LocalDateTime createdAt)
	{
		this.createdAt = Objects.requireNonNull(createdAt);
	}
	
	@Nullable
	public LocalDateTime getDisabledAt()
	{
		return this.disabledAt;
	}
	
	public void setDisabledAt(@Nullable final LocalDateTime disabledAt)
	{
		this.disabledAt = disabledAt;
	}
	
	@Nullable
	public LocalDateTime getLastLoginAt()
	{
		return this.lastLoginAt;
	}
	
	public boolean isDisabled()
	{
		return Optional.ofNullable(this.getDisabledAt())
			.filter(d -> d.isBefore(LocalDateTime.now(ZoneOffset.UTC)))
			.isPresent();
	}
	
	public void setLastLoginAt(@Nullable final LocalDateTime lastLoginAt)
	{
		this.lastLoginAt = lastLoginAt;
	}
	
	@Override
	public boolean equals(final Object o)
	{
		if(this == o)
		{
			return true;
		}
		if(!(o instanceof final UserDetail that))
		{
			return false;
		}
		return this.getId() == that.getId();
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hashCode(this.getId());
	}
}

package software.xdev.sse.demo.entities;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import software.xdev.sse.demo.service.validation.StringValidator;


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
	@Column(name = "fullname", length = DEFAULT_STRING_LENGTH, nullable = false)
	private String fullName = "";
	
	@Column(name = COL_EMAIL_ADDRESS, length = DEFAULT_STRING_LENGTH, nullable = false, unique = true)
	private String emailAddress;
	
	@Column(name = "created_at", nullable = false)
	private Instant createdAt;
	
	@Nullable
	@Column(name = COL_DISABLED_AT)
	private Instant disabledAt;
	
	@Nullable
	@Column(name = "last_login_at")
	private Instant lastLoginAt;
	
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
		user.setCreatedAt(Instant.now());
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
		this.fullName = StringValidator.notTooLongDefault("fullName", fullName);
	}
	
	public String getEmailAddress()
	{
		return this.emailAddress;
	}
	
	public void setEmailAddress(final String emailAddress)
	{
		StringValidator.notTooLongDefault("emailAddress", emailAddress);
		if(emailAddress.isEmpty())
		{
			throw new IllegalArgumentException("emailAddress is empty");
		}
		this.emailAddress = emailAddress;
	}
	
	public Instant getCreatedAt()
	{
		return this.createdAt;
	}
	
	public void setCreatedAt(final Instant createdAt)
	{
		this.createdAt = Objects.requireNonNull(createdAt);
	}
	
	@Nullable
	public Instant getDisabledAt()
	{
		return this.disabledAt;
	}
	
	public void setDisabledAt(@Nullable final Instant disabledAt)
	{
		this.disabledAt = disabledAt;
	}
	
	@Nullable
	public Instant getLastLoginAt()
	{
		return this.lastLoginAt;
	}
	
	public boolean isDisabled()
	{
		return Optional.ofNullable(this.getDisabledAt())
			.filter(d -> d.isBefore(Instant.now()))
			.isPresent();
	}
	
	public void setLastLoginAt(@Nullable final Instant lastLoginAt)
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

package software.xdev.sse.demo.entities.auth;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;

import software.xdev.sse.demo.entities.IdentifiableEntity;
import software.xdev.sse.demo.entities.UserDetail;


@MappedSuperclass
public abstract class Secret extends IdentifiableEntity
{
	public static final String COL_IDENTIFIER = "identifier";
	public static final String COL_CRYPTO_ALGORITHM = "crypto_algorithm";
	public static final String COL_SECRET = "secret";
	public static final String COL_CREATED_AT = "created_at";
	public static final String COL_USER = "user_id";
	
	@NotNull
	@Column(name = COL_IDENTIFIER, nullable = false, unique = true)
	private String identifier;
	
	@NotNull
	@Column(name = COL_CRYPTO_ALGORITHM, nullable = false)
	private String cryptoAlgorithm;
	
	@NotNull
	@Column(name = COL_SECRET, nullable = false)
	@SuppressWarnings("java:S1700")
	private byte[] secret;
	
	@NotNull
	@Column(name = COL_CREATED_AT, nullable = false)
	private LocalDateTime createdAt = LocalDateTime.now(ZoneOffset.UTC);
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COL_USER)
	private UserDetail user;
	
	public String getIdentifier()
	{
		return this.identifier;
	}
	
	public void setIdentifier(final String identifier)
	{
		this.identifier = identifier;
	}
	
	public String getCryptoAlgorithm()
	{
		return this.cryptoAlgorithm;
	}
	
	public void setCryptoAlgorithm(final String cryptoAlgorithm)
	{
		this.cryptoAlgorithm = cryptoAlgorithm;
	}
	
	public byte[] getSecret()
	{
		return this.secret;
	}
	
	public void setSecret(final byte[] secret)
	{
		this.secret = secret;
	}
	
	public LocalDateTime getCreatedAt()
	{
		return this.createdAt;
	}
	
	public void setCreatedAt(final LocalDateTime createdAt)
	{
		this.createdAt = createdAt;
	}
	
	public UserDetail getUser()
	{
		return this.user;
	}
	
	public void setUser(final UserDetail user)
	{
		this.user = user;
	}
}

package software.xdev.sse.demo.entities.auth;

import java.time.Instant;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;

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
	
	@Column(name = COL_IDENTIFIER, nullable = false, unique = true)
	private String identifier;
	
	@Column(name = COL_CRYPTO_ALGORITHM, nullable = false)
	private String cryptoAlgorithm;
	
	@Column(name = COL_SECRET, nullable = false)
	@SuppressWarnings("java:S1700")
	private byte[] secret;
	
	@Column(name = COL_CREATED_AT, nullable = false)
	private Instant createdAt = Instant.now();
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = COL_USER)
	private UserDetail user;
	
	public String getIdentifier()
	{
		return this.identifier;
	}
	
	public void setIdentifier(final String identifier)
	{
		this.identifier = Objects.requireNonNull(identifier);
	}
	
	public String getCryptoAlgorithm()
	{
		return this.cryptoAlgorithm;
	}
	
	public void setCryptoAlgorithm(final String cryptoAlgorithm)
	{
		this.cryptoAlgorithm = Objects.requireNonNull(cryptoAlgorithm);
	}
	
	public byte[] getSecret()
	{
		return this.secret;
	}
	
	public void setSecret(final byte[] secret)
	{
		this.secret = Objects.requireNonNull(secret);
	}
	
	public Instant getCreatedAt()
	{
		return this.createdAt;
	}
	
	public void setCreatedAt(final Instant createdAt)
	{
		this.createdAt = Objects.requireNonNull(createdAt);
	}
	
	public UserDetail getUser()
	{
		return this.user;
	}
	
	public void setUser(final UserDetail user)
	{
		this.user = Objects.requireNonNull(user);
	}
}

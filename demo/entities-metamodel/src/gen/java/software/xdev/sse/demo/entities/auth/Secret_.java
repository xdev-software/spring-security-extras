package software.xdev.sse.demo.entities.auth;

import jakarta.persistence.metamodel.MappedSuperclassType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.LocalDateTime;
import software.xdev.sse.demo.entities.UserDetail;

@StaticMetamodel(Secret.class)
public abstract class Secret_ extends software.xdev.sse.demo.entities.IdentifiableEntity_ {

	public static final String IDENTIFIER = "identifier";
	public static final String CREATED_AT = "createdAt";
	public static final String SECRET = "secret";
	public static final String USER = "user";
	public static final String CRYPTO_ALGORITHM = "cryptoAlgorithm";

	
	/**
	 * @see software.xdev.sse.demo.entities.auth.Secret#identifier
	 **/
	public static volatile SingularAttribute<Secret, String> identifier;
	
	/**
	 * @see software.xdev.sse.demo.entities.auth.Secret#createdAt
	 **/
	public static volatile SingularAttribute<Secret, LocalDateTime> createdAt;
	
	/**
	 * @see software.xdev.sse.demo.entities.auth.Secret#secret
	 **/
	public static volatile SingularAttribute<Secret, byte[]> secret;
	
	/**
	 * @see software.xdev.sse.demo.entities.auth.Secret
	 **/
	public static volatile MappedSuperclassType<Secret> class_;
	
	/**
	 * @see software.xdev.sse.demo.entities.auth.Secret#user
	 **/
	public static volatile SingularAttribute<Secret, UserDetail> user;
	
	/**
	 * @see software.xdev.sse.demo.entities.auth.Secret#cryptoAlgorithm
	 **/
	public static volatile SingularAttribute<Secret, String> cryptoAlgorithm;

}


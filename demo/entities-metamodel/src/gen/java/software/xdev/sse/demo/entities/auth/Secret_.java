package software.xdev.sse.demo.entities.auth;

import jakarta.persistence.metamodel.MappedSuperclassType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.LocalDateTime;
import software.xdev.sse.demo.entities.IdentifiableEntity_;
import software.xdev.sse.demo.entities.UserDetail;

/**
 * Static metamodel for {@link software.xdev.sse.demo.entities.auth.Secret}
 **/
@StaticMetamodel(Secret.class)
public abstract class Secret_ extends IdentifiableEntity_ {

	
	/**
	 * @see #identifier
	 **/
	public static final String IDENTIFIER = "identifier";
	
	/**
	 * @see #cryptoAlgorithm
	 **/
	public static final String CRYPTO_ALGORITHM = "cryptoAlgorithm";
	
	/**
	 * @see #secret
	 **/
	public static final String SECRET = "secret";
	
	/**
	 * @see #createdAt
	 **/
	public static final String CREATED_AT = "createdAt";
	
	/**
	 * @see #user
	 **/
	public static final String USER = "user";

	
	/**
	 * Static metamodel type for {@link software.xdev.sse.demo.entities.auth.Secret}
	 **/
	public static volatile MappedSuperclassType<Secret> class_;
	
	/**
	 * Static metamodel for attribute {@link software.xdev.sse.demo.entities.auth.Secret#identifier}
	 **/
	public static volatile SingularAttribute<Secret, String> identifier;
	
	/**
	 * Static metamodel for attribute {@link software.xdev.sse.demo.entities.auth.Secret#cryptoAlgorithm}
	 **/
	public static volatile SingularAttribute<Secret, String> cryptoAlgorithm;
	
	/**
	 * Static metamodel for attribute {@link software.xdev.sse.demo.entities.auth.Secret#secret}
	 **/
	public static volatile SingularAttribute<Secret, byte[]> secret;
	
	/**
	 * Static metamodel for attribute {@link software.xdev.sse.demo.entities.auth.Secret#createdAt}
	 **/
	public static volatile SingularAttribute<Secret, LocalDateTime> createdAt;
	
	/**
	 * Static metamodel for attribute {@link software.xdev.sse.demo.entities.auth.Secret#user}
	 **/
	public static volatile SingularAttribute<Secret, UserDetail> user;

}


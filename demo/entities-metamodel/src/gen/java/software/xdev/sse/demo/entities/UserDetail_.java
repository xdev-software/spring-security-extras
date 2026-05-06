package software.xdev.sse.demo.entities;

import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.LocalDateTime;

/**
 * Static metamodel for {@link software.xdev.sse.demo.entities.UserDetail}
 **/
@StaticMetamodel(UserDetail.class)
public abstract class UserDetail_ extends IdentifiableEntity_ {

	
	/**
	 * @see #fullName
	 **/
	public static final String FULL_NAME = "fullName";
	
	/**
	 * @see #emailAddress
	 **/
	public static final String EMAIL_ADDRESS = "emailAddress";
	
	/**
	 * @see #createdAt
	 **/
	public static final String CREATED_AT = "createdAt";
	
	/**
	 * @see #disabledAt
	 **/
	public static final String DISABLED_AT = "disabledAt";
	
	/**
	 * @see #lastLoginAt
	 **/
	public static final String LAST_LOGIN_AT = "lastLoginAt";

	
	/**
	 * Static metamodel type for {@link software.xdev.sse.demo.entities.UserDetail}
	 **/
	public static volatile EntityType<UserDetail> class_;
	
	/**
	 * Static metamodel for attribute {@link software.xdev.sse.demo.entities.UserDetail#fullName}
	 **/
	public static volatile SingularAttribute<UserDetail, String> fullName;
	
	/**
	 * Static metamodel for attribute {@link software.xdev.sse.demo.entities.UserDetail#emailAddress}
	 **/
	public static volatile SingularAttribute<UserDetail, String> emailAddress;
	
	/**
	 * Static metamodel for attribute {@link software.xdev.sse.demo.entities.UserDetail#createdAt}
	 **/
	public static volatile SingularAttribute<UserDetail, LocalDateTime> createdAt;
	
	/**
	 * Static metamodel for attribute {@link software.xdev.sse.demo.entities.UserDetail#disabledAt}
	 **/
	public static volatile SingularAttribute<UserDetail, LocalDateTime> disabledAt;
	
	/**
	 * Static metamodel for attribute {@link software.xdev.sse.demo.entities.UserDetail#lastLoginAt}
	 **/
	public static volatile SingularAttribute<UserDetail, LocalDateTime> lastLoginAt;

}


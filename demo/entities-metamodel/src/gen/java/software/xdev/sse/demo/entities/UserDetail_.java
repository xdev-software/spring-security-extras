package software.xdev.sse.demo.entities;

import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.LocalDateTime;

@StaticMetamodel(UserDetail.class)
public abstract class UserDetail_ extends software.xdev.sse.demo.entities.IdentifiableEntity_ {

	public static final String CREATED_AT = "createdAt";
	public static final String EMAIL_ADDRESS = "emailAddress";
	public static final String LAST_LOGIN_AT = "lastLoginAt";
	public static final String FULL_NAME = "fullName";
	public static final String DISABLED_AT = "disabledAt";

	
	/**
	 * @see software.xdev.sse.demo.entities.UserDetail#createdAt
	 **/
	public static volatile SingularAttribute<UserDetail, LocalDateTime> createdAt;
	
	/**
	 * @see software.xdev.sse.demo.entities.UserDetail#emailAddress
	 **/
	public static volatile SingularAttribute<UserDetail, String> emailAddress;
	
	/**
	 * @see software.xdev.sse.demo.entities.UserDetail#lastLoginAt
	 **/
	public static volatile SingularAttribute<UserDetail, LocalDateTime> lastLoginAt;
	
	/**
	 * @see software.xdev.sse.demo.entities.UserDetail#fullName
	 **/
	public static volatile SingularAttribute<UserDetail, String> fullName;
	
	/**
	 * @see software.xdev.sse.demo.entities.UserDetail
	 **/
	public static volatile EntityType<UserDetail> class_;
	
	/**
	 * @see software.xdev.sse.demo.entities.UserDetail#disabledAt
	 **/
	public static volatile SingularAttribute<UserDetail, LocalDateTime> disabledAt;

}


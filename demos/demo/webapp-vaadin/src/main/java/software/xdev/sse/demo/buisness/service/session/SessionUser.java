package software.xdev.sse.demo.buisness.service.session;

import java.io.Serializable;
import java.util.Objects;

import software.xdev.sse.demo.entities.UserDetail;


/**
 * User in the session
 */
@SuppressWarnings("java:S107")
public class SessionUser implements Serializable
{
	private final long userId;
	private final String email;
	private String fullName;
	
	public SessionUser(final UserDetail userDetail)
	{
		Objects.requireNonNull(userDetail);
		
		this.userId = userDetail.getId();
		this.email = userDetail.getEmailAddress();
		this.fullName = userDetail.getFullName();
	}
	
	public long userID()
	{
		return this.userId;
	}
	
	public String email()
	{
		return this.email;
	}
	
	public String fullName()
	{
		return this.fullName;
	}
	
	public SessionUser setFullName(final String fullName)
	{
		this.fullName = fullName;
		return this;
	}
}

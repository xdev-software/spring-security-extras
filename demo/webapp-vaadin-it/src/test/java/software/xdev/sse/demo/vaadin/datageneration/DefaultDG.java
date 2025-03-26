package software.xdev.sse.demo.vaadin.datageneration;

import jakarta.persistence.EntityManager;

import software.xdev.sse.demo.entities.UserDetail;
import software.xdev.sse.demo.tci.db.datageneration.AbstractDBDataGenerator;
import software.xdev.sse.demo.tci.oidc.OIDCTCI;


public class DefaultDG extends AbstractDBDataGenerator
{
	public static final String DEFAULT_USER_MAIL = OIDCTCI.DEFAULT_USER_EMAIL;
	public static final String DEFAULT_USER_FULL_NAME = OIDCTCI.DEFAULT_USER_NAME;
	
	public DefaultDG(final EntityManager em)
	{
		super(em);
	}
	
	public UserDetail generateDefaultUser()
	{
		return new UserDG(this.em()).generate(DEFAULT_USER_FULL_NAME, DEFAULT_USER_MAIL);
	}
	
	public DefaultDGResult generateAll()
	{
		final UserDetail userDetail = this.generateDefaultUser();
		
		return new DefaultDGResult(userDetail);
	}
}

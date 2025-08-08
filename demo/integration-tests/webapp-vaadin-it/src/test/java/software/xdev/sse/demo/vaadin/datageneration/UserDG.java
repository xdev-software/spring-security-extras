package software.xdev.sse.demo.vaadin.datageneration;

import java.time.LocalDate;

import jakarta.persistence.EntityManager;

import software.xdev.sse.demo.entities.UserDetail;
import software.xdev.sse.demo.persistence.jpa.dao.UserDetailDAO;
import software.xdev.sse.demo.tci.db.datageneration.AbstractDBDataGenerator;


public class UserDG extends AbstractDBDataGenerator
{
	public UserDG(final EntityManager em)
	{
		super(em);
	}
	
	public UserDetail generate(
		final String fullName,
		final String emailAddress)
	{
		final UserDetail user = UserDetail.createNew(fullName, emailAddress);
		user.setCreatedAt(LocalDate.of(2020, 1, 1).atStartOfDay());
		
		return this.update(user);
	}
	
	public UserDetail update(final UserDetail user)
	{
		return this.save(UserDetailDAO::new, user);
	}
}

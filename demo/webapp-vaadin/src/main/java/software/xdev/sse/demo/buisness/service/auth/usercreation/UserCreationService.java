package software.xdev.sse.demo.buisness.service.auth.usercreation;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import software.xdev.sse.demo.entities.UserDetail;
import software.xdev.sse.demo.persistence.jpa.dao.UserDetailDAO;


@Service
public class UserCreationService
{
	@Autowired
	private UserDetailDAO userdetailDAO;
	
	public UserDetail createBasicUserOnDataBase(final String email, final String fullName)
	{
		Objects.requireNonNull(email);
		Objects.requireNonNull(fullName);
		
		final UserDetail newuser = UserDetail.createNew(fullName, email);
		
		this.userdetailDAO.save(newuser);
		
		// ID not set; reload from DB
		return this.userdetailDAO.getUserByEmail(email)
			.orElseThrow(() -> new IllegalStateException(
				"User[email='" + email + "'] was created but could not be found"));
	}
}

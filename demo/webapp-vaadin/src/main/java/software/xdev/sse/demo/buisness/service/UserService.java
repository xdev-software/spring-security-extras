package software.xdev.sse.demo.buisness.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import software.xdev.sse.demo.entities.UserDetail;
import software.xdev.sse.demo.persistence.jpa.dao.UserDetailDAO;


@Service
public class UserService
{
	private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
	
	@Autowired
	UserDetailDAO userDetailDAO;
	
	@Async
	public void updateLastLoginToNowAsync(final long userId)
	{
		try
		{
			this.userDetailDAO.updateLastLoginAtToNow(userId);
		}
		catch(final Exception ex)
		{
			LOG.error("Failed to updateLastLoginAtToNowAsync[userId={}]", userId, ex);
		}
	}
	
	public Optional<UserDetail> getUserByEmail(final String email)
	{
		return this.userDetailDAO.getUserByEmail(email);
	}
	
	public boolean isDisabled(final String userEmail)
	{
		return this.userDetailDAO.isDisabled(userEmail);
	}
}

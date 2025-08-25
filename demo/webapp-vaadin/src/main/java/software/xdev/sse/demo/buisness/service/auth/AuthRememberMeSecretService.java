package software.xdev.sse.demo.buisness.service.auth;

import java.time.LocalDateTime;
import java.util.Optional;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import software.xdev.sse.demo.persistence.jpa.dao.AuthRememberMeSecretDAO;
import software.xdev.sse.demo.persistence.jpa.dto.AuthRememberMeSecretDTO;


@Service
public class AuthRememberMeSecretService
{
	// Can't use HasLogger here as @Async would require setting proxyTargetClass=true in @EnableAsync
	private static final Logger LOG = LoggerFactory.getLogger(AuthRememberMeSecretService.class);
	
	@Autowired
	AuthRememberMeSecretDAO authRememberMeSecretDAO;
	
	public Optional<AuthRememberMeSecretDTO> findByIdentifier(
		final String identifier,
		final LocalDateTime createdAfterUtc)
	{
		LOG.debug("Starting findBy");
		final StopWatch sw = StopWatch.createStarted();
		
		try
		{
			return this.authRememberMeSecretDAO.findBy(identifier, createdAfterUtc);
		}
		finally
		{
			sw.stop();
			LOG.debug("Finished findBy, took {}ms", sw.getTime());
		}
	}
	
	@Async
	public void insert(final AuthRememberMeSecretDTO secret)
	{
		LOG.debug("Starting insert");
		final StopWatch sw = StopWatch.createStarted();
		
		try
		{
			this.authRememberMeSecretDAO.insert(secret);
		}
		catch(final Exception ex)
		{
			LOG.warn("Failed to insert", ex);
		}
		finally
		{
			sw.stop();
			LOG.debug("Finished insert, took {}ms", sw.getTime());
		}
	}
	
	@Async
	public void delete(final String identifier)
	{
		LOG.debug("Starting delete");
		final StopWatch sw = StopWatch.createStarted();
		
		try
		{
			this.authRememberMeSecretDAO.delete(identifier);
		}
		catch(final Exception ex)
		{
			LOG.warn("Failed to delete", ex);
		}
		finally
		{
			sw.stop();
			LOG.debug("Finished delete, took {}ms", sw.getTime());
		}
	}
	
	public int cleanUp(final LocalDateTime createdBeforeUtc, final int maxPerUser)
	{
		LOG.debug("Starting cleanup");
		final StopWatch sw = StopWatch.createStarted();
		try
		{
			int deleted = this.authRememberMeSecretDAO.deleteAllCreatedBefore(createdBeforeUtc);
			deleted += this.authRememberMeSecretDAO.deleteAllOverUserMaxAmount(maxPerUser);
			
			return deleted;
		}
		finally
		{
			sw.stop();
			LOG.debug("Finished cleanup, took {}ms", sw.getTime());
		}
	}
}

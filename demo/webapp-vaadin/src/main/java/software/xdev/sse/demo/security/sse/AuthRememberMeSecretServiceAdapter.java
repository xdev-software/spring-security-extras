package software.xdev.sse.demo.security.sse;

import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import software.xdev.sse.demo.buisness.service.auth.AuthRememberMeSecretService;
import software.xdev.sse.demo.persistence.jpa.dto.AuthRememberMeSecretDTO;
import software.xdev.sse.oauth2.rememberme.secrets.AuthRememberMeSecret;
import software.xdev.sse.oauth2.rememberme.secrets.DefaultAuthRememberMeSecret;


// Could also be implemented directly into Service however we simulate that the service is in another package
// and this is therefore not possible
@Service
public class AuthRememberMeSecretServiceAdapter
	implements software.xdev.sse.oauth2.rememberme.secrets.AuthRememberMeSecretService
{
	@Autowired
	protected AuthRememberMeSecretService authRememberMeSecretService;
	
	@Override
	public Optional<AuthRememberMeSecret> findByIdentifier(
		final String identifier,
		final Instant createdAfter)
	{
		return this.authRememberMeSecretService.findByIdentifier(identifier, createdAfter)
			.map(AuthRememberMeSecretAdapter::new);
	}
	
	@Override
	public void insert(final AuthRememberMeSecret secret)
	{
		this.authRememberMeSecretService.insert(new AuthRememberMeSecretDTO(
			secret.identifier(),
			secret.cryptoAlgorithm(),
			secret.secret(),
			secret.userEmailAddress()));
	}
	
	@Override
	public void delete(final String identifier)
	{
		this.authRememberMeSecretService.delete(identifier);
	}
	
	@Override
	public int cleanUp(final Instant createdBefore, final int maxPerUser)
	{
		return this.authRememberMeSecretService.cleanUp(createdBefore, maxPerUser);
	}
	
	public static class AuthRememberMeSecretAdapter extends DefaultAuthRememberMeSecret
	{
		public AuthRememberMeSecretAdapter(final AuthRememberMeSecretDTO dto)
		{
			super(dto.identifier(), dto.cryptoAlgorithm(), dto.secret(), dto.userEmailAddress());
		}
	}
}

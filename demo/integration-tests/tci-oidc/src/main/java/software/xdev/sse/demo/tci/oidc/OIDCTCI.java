package software.xdev.sse.demo.tci.oidc;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.Timeout;
import org.rnorth.ducttape.unreliables.Unreliables;

import software.xdev.sse.demo.tci.oidc.containers.OIDCServerContainer;
import software.xdev.tci.TCI;


public class OIDCTCI extends TCI<OIDCServerContainer>
{
	protected static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);
	
	public static final String DEFAULT_DOMAIN = "example.org";
	public static final String CLIENT_ID = OIDCServerContainer.DEFAULT_CLIENT_ID;
	public static final String CLIENT_SECRET = OIDCServerContainer.DEFAULT_CLIENT_SECRET;
	
	public static final String DEFAULT_USER_EMAIL = "test@" + DEFAULT_DOMAIN;
	public static final String DEFAULT_USER_NAME = "Testuser";
	public static final String DEFAULT_USER_PASSWORD = "pwd";
	
	public OIDCTCI(final OIDCServerContainer container, final String networkAlias)
	{
		super(container, networkAlias);
	}
	
	@Override
	public void start(final String containerName)
	{
		super.start(containerName);
		this.addUser(DEFAULT_USER_EMAIL, DEFAULT_USER_NAME, DEFAULT_USER_PASSWORD);
		
		// Otherwise app server response may time out as initial requests needs a few seconds
		this.warmUpWellKnownJWKsEndpoint();
	}
	
	public String getDefaultUserEmail()
	{
		return DEFAULT_USER_EMAIL;
	}
	
	public String getDefaultUserName()
	{
		return DEFAULT_USER_NAME;
	}
	
	public String getDefaultUserPassword()
	{
		return DEFAULT_USER_PASSWORD;
	}
	
	public static String getInternalHttpBaseEndPoint(final String networkAlias)
	{
		return "http://" + networkAlias + ":" + OIDCServerContainer.PORT;
	}
	
	public String getInternalHttpBaseEndPoint()
	{
		return getInternalHttpBaseEndPoint(this.getNetworkAlias());
	}
	
	public String getExternalHttpBaseEndPoint()
	{
		return this.getContainer().getExternalHttpBaseEndPoint();
	}
	
	public void warmUpWellKnownJWKsEndpoint()
	{
		final HttpClient httpClient = HttpClient.newBuilder()
			.connectTimeout(Duration.ofSeconds(2L))
			.build();
		
		Unreliables.retryUntilSuccess(
			5,
			() ->
				httpClient.send(
					HttpRequest.newBuilder(URI.create(
							this.getExternalHttpBaseEndPoint() + "/.well-known/openid-configuration/jwks"))
						.timeout(Duration.ofSeconds(10L))
						.GET()
						.build(),
					HttpResponse.BodyHandlers.discarding()));
	}
	
	public void addUser(
		final String email,
		final String name,
		final String pw)
	{
		addUser(this.getContainer(), email, name, pw);
	}
	
	protected static void addUser(
		final OIDCServerContainer container,
		final String email,
		final String name,
		final String pw)
	{
		try(final CloseableHttpClient client = createDefaultHttpClient())
		{
			final HttpPost post = new HttpPost(container.getExternalHttpBaseEndPoint() + "/api/v1/user");
			post.setEntity(new StringEntity("""
				{
				  "SubjectId":"%s",
				  "Username":"%s",
				  "Password":"%s",
				  "Claims": [
				    {
				      "Type": "name",
				      "Value": "%s",
				      "ValueType": "string"
				    },
				    {
				      "Type": "email",
				      "Value": "%s",
				      "ValueType": "string"
				    }
				  ]
				}
				""".formatted(
				UUID.randomUUID().toString(),
				email,
				pw,
				name,
				email
			)));
			post.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
			post.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
			
			final ClassicHttpResponse response = client.execute(post, r -> r);
			if(response.getCode() != HttpStatus.SC_OK)
			{
				throw new IllegalStateException("Unable to create user; Expected statuscode 200 but got "
					+ response.getCode()
					+ "; Reason: " + response.getReasonPhrase());
			}
		}
		catch(final IOException ioe)
		{
			throw new UncheckedIOException(ioe);
		}
	}
	
	protected static CloseableHttpClient createDefaultHttpClient()
	{
		return HttpClientBuilder.create()
			.setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
				.setDefaultConnectionConfig(ConnectionConfig.custom()
					.setConnectTimeout(Timeout.of(DEFAULT_TIMEOUT))
					.setSocketTimeout(Timeout.of(DEFAULT_TIMEOUT))
					.build())
				.build())
			.build();
	}
}

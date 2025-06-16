package software.xdev.sse.demo.tci.oidc.containers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;


public class OIDCServerContainer extends GenericContainer<OIDCServerContainer>
{
	public static final int PORT = 8080;
	
	public static final String DEFAULT_CLIENT_ID = "client-id1";
	public static final String DEFAULT_CLIENT_SECRET = "client-secret1";
	
	public OIDCServerContainer()
	{
		super(DockerImageName.parse("xdevsoftware/oidc-server-mock:1"));
		this.addExposedPort(PORT);
	}
	
	public OIDCServerContainer withDefaultEnvConfig()
	{
		this.addEnv("ASPNETCORE_ENVIRONMENT", "Development");
		this.addEnv("ASPNET_SERVICES_OPTIONS_INLINE", """
			{
			  "ForwardedHeadersOptions": {
			    "ForwardedHeaders" : "All"
			  }
			}
			""");
		this.addEnv("SERVER_OPTIONS_INLINE", """
			{
			  "AccessTokenJwtType": "JWT",
			  "Discovery": {
			    "ShowKeySet": true
			  },
			  "Authentication": {
			    "CookieSameSiteMode": "Lax",
			    "CheckSessionCookieSameSiteMode": "Lax"
			  }
			}
			""");
		this.addEnv("LOGIN_OPTIONS_INLINE", """
			{
			  "AllowRememberLogin": false
			}
			""");
		this.addEnv("LOGOUT_OPTIONS_INLINE", """
			{
			  "AutomaticRedirectAfterSignOut": true
			}
			""");
		this.addEnv("CLIENTS_CONFIGURATION_INLINE", """
			[
			  {
			      "ClientId": "%s",
			      "ClientSecrets": [
			          "%s"
			      ],
			      "Description": "Desc",
			      "AllowedGrantTypes": [
			          "authorization_code",
			          "refresh_token"
			      ],
			      "RedirectUris": [
			          "*"
			      ],
			      "AllowedScopes": [
			          "openid",
			          "profile",
			          "email",
			          "offline_access"
			      ],
			      "AlwaysIncludeUserClaimsInIdToken": true,
			      "AllowOfflineAccess": true,
			      "RequirePkce": false
			  }
			]
			""".formatted(DEFAULT_CLIENT_ID, DEFAULT_CLIENT_SECRET));
		return this.self();
	}
	
	public String getExternalHttpBaseEndPoint()
	{
		// noinspection HttpUrlsUsage
		return "http://"
			+ this.getHost()
			+ ":"
			+ this.getMappedPort(PORT);
	}
}

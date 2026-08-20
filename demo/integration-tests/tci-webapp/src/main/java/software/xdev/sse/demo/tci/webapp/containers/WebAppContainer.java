package software.xdev.sse.demo.tci.webapp.containers;

import java.time.Duration;

import org.testcontainers.containers.GenericContainer;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;

import software.xdev.tci.startup.error.java.fatal.HsErrPidStartUpCrashReporter;
import software.xdev.tci.startup.wait.FastAbortOnContainerDeathWaitStrategy;
import software.xdev.tci.startup.wait.strategy.HostPortWaitAbortableStrategy;
import software.xdev.tci.startup.wait.strategy.HttpWaitAbortableStrategy;


@SuppressWarnings({"java:S2160", "java:S119"})
public abstract class WebAppContainer<SELF extends WebAppContainer<SELF>> extends GenericContainer<SELF>
{
	public static final int DEFAULT_HTTP_PORT = 8080;
	
	protected final boolean connectionlessStart;
	protected final HsErrPidStartUpCrashReporter hsErrPidStartUpCrashReporter;
	
	protected WebAppContainer(final String dockerImageName, final boolean connectionlessStart)
	{
		super(dockerImageName);
		this.connectionlessStart = connectionlessStart;
		if(connectionlessStart)
		{
			this.withConnectionlessStart();
		}
		this.addExposedPort(DEFAULT_HTTP_PORT);
		this.hsErrPidStartUpCrashReporter = new HsErrPidStartUpCrashReporter(this);
	}
	
	public SELF withDebugRootLogger()
	{
		// Root logger level in Production = INFO and in dev-log = DEBUG
		// -> Clashes and "random" log level is selected causing inconsistent behavior
		// -> Set explicitly
		return this.withEnv("LOGGING_LEVEL_ROOT", "debug");
	}
	
	public SELF withDB(final String jdbcUrl, final String username, final String password)
	{
		return this.withEnv("SPRING_DATASOURCE_URL", jdbcUrl)
			.withEnv("SPRING_DATASOURCE_USERNAME", username)
			.withEnv("SPRING_DATASOURCE_PASSWORD", password);
	}
	
	public SELF withAuth(
		final String oidcClientId,
		final String oidcClientSecret,
		final String oidcServerUrl)
	{
		final String registration = "SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_LOCAL_";
		final String provider = "SPRING_SECURITY_OAUTH2_CLIENT_PROVIDER_LOCAL_";
		if(this.connectionlessStart)
		{
			this.addEnv(registration + "AUTHORIZATION-GRANT-TYPE", "authorization_code");
			this.addEnv(registration + "REDIRECT-URI", "{baseUrl}/{action}/oauth2/code/{registrationId}");
			this.addEnv(provider + "AUTHORIZATION-URI", oidcServerUrl + "/connect/authorize");
			this.addEnv(provider + "TOKEN-URI", oidcServerUrl + "/connect/token");
			this.addEnv(provider + "JWK-SET-URI", oidcServerUrl + "/.well-known/openid-configuration/jwks");
			this.addEnv(provider + "USER-INFO-URI", oidcServerUrl + "/connect/userinfo");
			this.addEnv(provider + "USER-INFO-AUTHENTICATION-METHOD", "header");
			this.addEnv(provider + "USER-NAME-ATTRIBUTE", "sub");
		}
		else
		{
			this.addEnv(provider + "ISSUER-URI", oidcServerUrl);
		}
		
		return this.withEnv(registration + "CLIENT-NAME", "Local")
			.withEnv(registration + "CLIENT-ID", oidcClientId)
			.withEnv(registration + "CLIENT-SECRET", oidcClientSecret)
			// offline_access is required to get the refresh token in the initial response!
			.withEnv(registration + "SCOPE", "openid,profile,email,offline_access");
	}
	
	public SELF withActuator(final String username, final String hash)
	{
		return this.withEnv("SSE_ACTUATOR_USERS_0_USERNAME", username)
			.withEnv("SSE_ACTUATOR_USERS_0_PASSWORD-HASH", hash);
	}
	
	public SELF withDisableHTTPS()
	{
		return this.withEnv("SERVER_SERVLET_SESSION_COOKIE_SECURE", false);
	}
	
	/**
	 * Starts the application without establishing any connections.<br/> Connection/Initialization is done on demand.
	 */
	public SELF withConnectionlessStart()
	{
		final String springJpa = "SPRING_JPA_";
		return this.withDisableFlyway()
			// Disable DB for actuator endpoint or complete endpoint will fail otherwise
			.withEnv("MANAGEMENT_HEALTH_DB_ENABLED", false)
			.withEnv(
				springJpa + "DATABASE-PLATFORM",
				"software.xdev.sse.demo.persistence.config.dialect.DefaultDialect")
			.withEnv(springJpa + "PROPERTIES_HIBERNATE_BOOT_ALLOW_JDBC_METADATA_ACCESS", false);
	}
	
	public SELF withDisableFlyway()
	{
		return this.withEnv("SPRING_FLYWAY_ENABLED", false);
	}
	
	public SELF withEnv(final String key, final boolean value)
	{
		return this.withEnv(key, String.valueOf(value));
	}
	
	public SELF withDefaultWaitStrategy(
		final Duration startUpTimeout,
		final String actuatorUsername,
		final String actuatorPassword)
	{
		return this.waitingFor(FastAbortOnContainerDeathWaitStrategy.waitAll(s -> s
			.withStartupTimeout(startUpTimeout)
			.withStrategy(new HostPortWaitAbortableStrategy())
			.withStrategy(new HttpWaitAbortableStrategy()
				.forPort(WebAppContainer.DEFAULT_HTTP_PORT)
				.forPath("/robots.txt")
				.forStatusCode(HttpStatus.SC_OK)
				.withReadTimeout(Duration.ofSeconds(10))
			)
			.withStrategy(new HttpWaitAbortableStrategy()
				.forPort(WebAppContainer.DEFAULT_HTTP_PORT)
				.forPath("/actuator/health")
				.withBasicCredentials(actuatorUsername, actuatorPassword)
				.forStatusCode(HttpStatus.SC_OK)
				.withReadTimeout(Duration.ofSeconds(10))
			))
		);
	}
	
	@Override
	protected void containerIsStarted(final InspectContainerResponse containerInfo, final boolean reused)
	{
		this.hsErrPidStartUpCrashReporter.containerIsStarted();
		super.containerIsStarted(containerInfo, reused);
	}
	
	@Override
	protected void containerIsStopping(final InspectContainerResponse containerInfo)
	{
		this.hsErrPidStartUpCrashReporter.containerIsStopping(this.logger());
		super.containerIsStopping(containerInfo);
	}
	
	@SuppressWarnings("unused")
	protected void remoteDebug(final int port)
	{
		this.addEnv("JAVA_OPTS", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:" + port);
		this.addFixedExposedPort(port, port);
	}
}

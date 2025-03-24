package software.xdev.sse.demo.tci.oidc.factory;

import java.time.Duration;

import org.apache.hc.core5.http.HttpStatus;
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;

import software.xdev.sse.demo.tci.oidc.OIDCTCI;
import software.xdev.sse.demo.tci.oidc.containers.OIDCServerContainer;
import software.xdev.sse.demo.tci.util.ContainerMemory;
import software.xdev.sse.factory.prestart.PreStartableTCIFactory;


public class OIDCTCIFactory extends PreStartableTCIFactory<OIDCServerContainer, OIDCTCI>
{
	@SuppressWarnings("resource")
	public OIDCTCIFactory()
	{
		super(
			OIDCTCI::new,
			() -> new OIDCServerContainer()
				.withCreateContainerCmdModifier(cmd -> cmd.getHostConfig().withMemory(ContainerMemory.M512M))
				.waitingFor(
					new WaitAllStrategy()
						.withStartupTimeout(Duration.ofMinutes(1))
						.withStrategy(new HostPortWaitStrategy())
						.withStrategy(
							new HttpWaitStrategy()
								.forPort(OIDCServerContainer.PORT)
								.forPath("/")
								.forStatusCode(HttpStatus.SC_OK)
								.withReadTimeout(Duration.ofSeconds(10))
						)
				)
				.withDefaultEnvConfig(),
			"oidc",
			"container.oidc",
			"OIDC");
	}
}

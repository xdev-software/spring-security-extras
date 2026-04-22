package software.xdev.sse.vaadin.sessioncleaner.task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class SessionUIsVaadinSessionCleanerTaskTest
{
	@Test
	void checkReflectionWorking()
	{
		Assertions.assertDoesNotThrow(
			SessionUIsVaadinSessionCleanerTask::vaadinServiceCleanupSessionDelegate);
	}
}

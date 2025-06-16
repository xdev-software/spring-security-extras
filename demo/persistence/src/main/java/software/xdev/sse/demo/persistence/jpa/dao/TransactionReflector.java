package software.xdev.sse.demo.persistence.jpa.dao;

import java.util.function.Supplier;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;


@Service
public class TransactionReflector
{
	@Transactional
	public void runWithTransaction(final Runnable runnable)
	{
		runnable.run();
	}
	
	@Transactional
	public <T> T runWithTransaction(final Supplier<T> supplier)
	{
		return supplier.get();
	}
}

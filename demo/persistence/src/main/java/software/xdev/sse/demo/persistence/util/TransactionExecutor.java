package software.xdev.sse.demo.persistence.util;

import java.util.Objects;
import java.util.function.Supplier;

import jakarta.persistence.EntityManager;


/**
 * Manually executes a transaction.
 * <p/>
 * Can be used when no transaction framework (e.g. delivered with an AppSever) is present.
 */
public class TransactionExecutor
{
	protected final EntityManager em;
	
	public TransactionExecutor(final EntityManager em)
	{
		this.em = Objects.requireNonNull(em);
	}
	
	public void execWithTransaction(final Runnable run)
	{
		this.em.getTransaction().begin();
		
		try
		{
			run.run();
			this.em.getTransaction().commit();
		}
		catch(final Exception e)
		{
			this.em.getTransaction().rollback();
			throw e;
		}
	}
	
	public <T> T execWithTransaction(final Supplier<T> supplier)
	{
		this.em.getTransaction().begin();
		
		try
		{
			final T result = supplier.get();
			this.em.getTransaction().commit();
			
			return result;
		}
		catch(final Exception e)
		{
			this.em.getTransaction().rollback();
			throw e;
		}
	}
	
	public void close()
	{
		this.em.close();
	}
}

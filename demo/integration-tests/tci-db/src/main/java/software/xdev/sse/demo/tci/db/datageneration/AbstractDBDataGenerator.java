package software.xdev.sse.demo.tci.db.datageneration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import jakarta.persistence.EntityManager;

import software.xdev.sse.demo.entities.IdentifiableEntity;
import software.xdev.sse.demo.persistence.jpa.dao.BaseEntityDAO;
import software.xdev.sse.demo.persistence.util.TransactionExecutor;


/**
 * Base class for all data generators. Holds an {@link EntityManager} and a {@link TransactionExecutor} to save data.
 *
 * @author AB
 */
public abstract class AbstractDBDataGenerator implements DataGenerator
{
	private final EntityManager em;
	private final TransactionExecutor transactor;
	
	protected AbstractDBDataGenerator(final EntityManager em)
	{
		this(em, null);
	}
	
	protected AbstractDBDataGenerator(final EntityManager em, final TransactionExecutor transactor)
	{
		this.em = Objects.requireNonNull(em, "EntityManager can't be null!");
		this.transactor = transactor != null ? transactor : new TransactionExecutor(em);
	}
	
	/**
	 * Returns the {@link EntityManager}-Instance of this generator, which can be used to save data.
	 */
	protected EntityManager em()
	{
		return this.em;
	}
	
	/**
	 * Returns the {@link TransactionExecutor}-Instance of this generator, which can be used to save data with a
	 * transaction.
	 */
	protected TransactionExecutor transactor()
	{
		return this.transactor;
	}
	
	/**
	 * Returns a {@link LocalDate} in the past. By default 01.01.1970 is used.
	 */
	@SuppressWarnings("checkstyle:MagicNumber")
	public LocalDate getLocalDateInPast()
	{
		return LocalDate.of(1970, 1, 1);
	}
	
	/**
	 * Returns a {@link LocalDate} in the past.
	 */
	@SuppressWarnings("checkstyle:MagicNumber")
	public LocalDate getLocalDateInFuture()
	{
		return LocalDate.of(3000, 1, 1).plusYears(1);
	}
	
	@SafeVarargs
	public final <T extends IdentifiableEntity> List<T> saveBatch(
		final Function<EntityManager, BaseEntityDAO<T>> daoSupplier,
		final T... elements)
	{
		return this.transactor()
			.execWithTransaction(
				() -> daoSupplier.apply(this.em()).saveBatch(new ArrayList<>(Arrays.asList(elements))));
	}
	
	public <T extends IdentifiableEntity> List<T> saveBatch(
		final Function<EntityManager, BaseEntityDAO<T>> daoSupplier,
		final Collection<T> elements)
	{
		return this.transactor()
			.execWithTransaction(() -> daoSupplier.apply(this.em()).saveBatch(elements));
	}
	
	public <T extends IdentifiableEntity> T save(
		final Function<EntityManager, BaseEntityDAO<T>> daoSupplier,
		final T element)
	{
		return this.transactor().execWithTransaction(() -> daoSupplier.apply(this.em()).save(element));
	}
}

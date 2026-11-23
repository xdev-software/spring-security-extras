package software.xdev.sse.demo.tci.db.datageneration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import jakarta.persistence.EntityManager;

import software.xdev.sse.demo.entities.IdentifiableEntity;
import software.xdev.sse.demo.persistence.jpa.dao.BaseEntityDAO;
import software.xdev.tci.db.datageneration.BaseDBDataGenerator;


public abstract class AbstractDBDataGenerator extends BaseDBDataGenerator
{
	public AbstractDBDataGenerator(final EntityManager em)
	{
		super(em);
	}
	
	public AbstractDBDataGenerator(
		final EntityManager em,
		final software.xdev.tci.db.persistence.TransactionExecutor transactor)
	{
		super(em, transactor);
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

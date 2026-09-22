package software.xdev.sse.demo.persistence.jpa.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import software.xdev.sse.demo.entities.IdentifiableEntity;


public abstract class BaseEntityDAO<T extends IdentifiableEntity> extends BaseDAO
{
	protected BaseEntityDAO(final EntityManager em)
	{
		super(em);
	}
	
	@Transactional
	public T save(final T entity)
	{
		Objects.requireNonNull(entity);
		
		final EntityManager entityManager = this.getEntityManager();
		
		if(entity.getId() == 0L)
		{
			entityManager.persist(entity);
		}
		
		final T mergedEntity = entityManager.merge(entity);
		
		entityManager.flush();
		
		return mergedEntity;
	}
	
	@Transactional
	public List<T> saveBatch(final Collection<T> entities)
	{
		final EntityManager entityManager = this.getEntityManager();
		
		final List<T> mergedEntities = new ArrayList<>();
		for(final T e : entities)
		{
			if(e.getId() == 0)
			{
				entityManager.persist(e);
			}
			mergedEntities.add(entityManager.merge(e));
		}
		entityManager.flush(); // DB-Sync
		
		return mergedEntities;
	}
	
	protected T getById(final Class<T> type, final long id)
	{
		return this.getEntityManager().find(type, id);
	}
}

package software.xdev.sse.demo.persistence.jpa.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;


public abstract class BaseDAO
{
	final EntityManager em;
	
	protected BaseDAO(final EntityManager em)
	{
		this.em = em;
	}
	
	public EntityManager getEntityManager()
	{
		return this.em;
	}
	
	public CriteriaBuilder getCriteriaBuilder()
	{
		return this.getEntityManager().getCriteriaBuilder();
	}
}

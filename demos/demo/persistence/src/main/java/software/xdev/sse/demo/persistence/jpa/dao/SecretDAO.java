package software.xdev.sse.demo.persistence.jpa.dao;

import jakarta.persistence.EntityManager;

import software.xdev.sse.demo.entities.auth.Secret;


public abstract class SecretDAO<T extends Secret> extends BaseEntityDAO<T>
{
	protected final Class<T> clazz;
	protected final String tableName;
	
	protected SecretDAO(final EntityManager em, final Class<T> clazz, final String tableName)
	{
		super(em);
		this.clazz = clazz;
		this.tableName = tableName;
	}
}

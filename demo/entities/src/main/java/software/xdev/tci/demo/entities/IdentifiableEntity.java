package software.xdev.sse.demo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;


@MappedSuperclass
public abstract class IdentifiableEntity
{
	public static final String COL_ID = "id";
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = IdentifiableEntity.COL_ID, unique = true, nullable = false)
	private long id;
	
	protected IdentifiableEntity()
	{
	}
	
	protected IdentifiableEntity(final long id)
	{
		this.id = id;
	}
	
	public long getId()
	{
		return this.id;
	}
	
	public void setId(final long id)
	{
		this.id = id;
	}
}


package software.xdev.sse.demo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


@Entity
@Table(name = "product")
public class Product extends IdentifiableEntity
{
	@NotNull
	@Size(max = 255)
	@Column(name = "name", nullable = false, unique = true)
	private String name;
	
	public Product()
	{
	}
	
	public Product(final String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public void setName(final String name)
	{
		this.name = name;
	}
}

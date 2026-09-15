package software.xdev.sse.demo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import software.xdev.sse.demo.service.validation.StringValidator;


@Entity
@Table(name = "product")
public class Product extends IdentifiableEntity
{
	@Column(name = "name", nullable = false, unique = true)
	private String name;
	
	public Product()
	{
	}
	
	public Product(final String name)
	{
		this.setName(name);
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public void setName(final String name)
	{
		this.name = StringValidator.notTooLongDefault("name", name);
	}
}

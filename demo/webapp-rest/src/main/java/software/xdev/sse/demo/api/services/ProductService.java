package software.xdev.sse.demo.api.services;

import java.util.List;
import java.util.Objects;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import org.springframework.stereotype.Service;

import software.xdev.sse.demo.entities.Product;
import software.xdev.sse.demo.persistence.jpa.dao.ProductDAO;


@Service
public record ProductService(ProductDAO productDAO)
{
	public List<Product> getAll()
	{
		return this.productDAO.findAll();
	}
	
	public Product getById(final long id)
	{
		if(id < 1)
		{
			return null;
		}
		
		return this.productDAO.getById(id);
	}
	
	public Product getByName(final String name)
	{
		if(name == null)
		{
			return null;
		}
		return this.productDAO.findByName(name);
	}
	
	public boolean deleteById(final long id)
	{
		if(id < 1)
		{
			return false;
		}
		
		return this.productDAO.deleteById(id) == 1;
	}
	
	public Product create(final ProductCreateDTO dto)
	{
		Objects.requireNonNull(dto);
		if(this.productDAO.findByName(dto.name()) != null)
		{
			return null;
		}
		
		return this.productDAO.save(new Product(dto.name()));
	}
	
	public record ProductCreateDTO(
		@NotNull
		String name)
	{
	}
	
	public Product update(final ProductUpdateDTO dto)
	{
		Objects.requireNonNull(dto);
		
		final Product product = this.productDAO.getById(dto.id());
		if(product == null)
		{
			return null;
		}
		
		product.setName(dto.name());
		
		return this.productDAO.save(product);
	}
	
	public record ProductUpdateDTO(
		@Min(1)
		long id,
		@NotNull
		String name
	)
	{
	}
}

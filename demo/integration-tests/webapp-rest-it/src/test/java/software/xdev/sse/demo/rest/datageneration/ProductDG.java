package software.xdev.sse.demo.rest.datageneration;

import jakarta.persistence.EntityManager;

import software.xdev.sse.demo.entities.Product;
import software.xdev.sse.demo.persistence.jpa.dao.ProductDAO;
import software.xdev.sse.demo.tci.db.datageneration.AbstractDBDataGenerator;


public class ProductDG extends AbstractDBDataGenerator
{
	public ProductDG(final EntityManager em)
	{
		super(em);
	}
	
	public Product generateProduct(final String name)
	{
		return this.save(ProductDAO::new, new Product(name));
	}
}

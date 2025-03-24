package software.xdev.sse.demo.entities;

import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Product.class)
public abstract class Product_ extends software.xdev.sse.demo.entities.IdentifiableEntity_ {

	public static final String NAME = "name";

	
	/**
	 * @see software.xdev.sse.demo.entities.Product#name
	 **/
	public static volatile SingularAttribute<Product, String> name;
	
	/**
	 * @see software.xdev.sse.demo.entities.Product
	 **/
	public static volatile EntityType<Product> class_;

}


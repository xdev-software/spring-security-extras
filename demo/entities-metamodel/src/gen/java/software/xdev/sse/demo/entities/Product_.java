package software.xdev.sse.demo.entities;

import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

/**
 * Static metamodel for {@link software.xdev.sse.demo.entities.Product}
 **/
@StaticMetamodel(Product.class)
public abstract class Product_ extends IdentifiableEntity_ {

	
	/**
	 * @see #name
	 **/
	public static final String NAME = "name";

	
	/**
	 * Static metamodel type for {@link software.xdev.sse.demo.entities.Product}
	 **/
	public static volatile EntityType<Product> class_;
	
	/**
	 * Static metamodel for attribute {@link software.xdev.sse.demo.entities.Product#name}
	 **/
	public static volatile SingularAttribute<Product, String> name;

}


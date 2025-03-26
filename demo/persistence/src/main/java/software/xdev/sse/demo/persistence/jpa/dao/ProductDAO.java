package software.xdev.sse.demo.persistence.jpa.dao;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Repository;

import software.xdev.sse.demo.entities.IdentifiableEntity_;
import software.xdev.sse.demo.entities.Product;
import software.xdev.sse.demo.entities.Product_;


@Repository
public class ProductDAO extends BaseEntityDAO<Product>
{
	public ProductDAO(final EntityManager em)
	{
		super(em);
	}
	
	public Product findByName(final String name)
	{
		Objects.requireNonNull(name);
		
		return this.find((cb, root) -> List.of(cb.equal(root.get(Product_.name), name)))
			.stream()
			.findFirst()
			.orElse(null);
	}
	
	public List<Product> findAll()
	{
		return this.find(null);
	}
	
	protected List<Product> find(final BiFunction<CriteriaBuilder, Root<Product>, List<Predicate>> wheresBuilder)
	{
		final CriteriaBuilder cb = this.getCriteriaBuilder();
		final CriteriaQuery<Product> cq = cb.createQuery(Product.class);
		
		final Root<Product> root = cq.from(Product.class);
		
		cq.select(root);
		
		if(wheresBuilder != null)
		{
			cq.where(wheresBuilder.apply(cb, root).toArray(Predicate[]::new));
		}
		
		return this.getEntityManager().createQuery(cq).getResultList();
	}
	
	public Product getById(final long id)
	{
		return this.getById(Product.class, id);
	}
	
	@Transactional
	public int deleteById(final long id)
	{
		final CriteriaBuilder cb = this.getCriteriaBuilder();
		final CriteriaDelete<Product> cd = cb.createCriteriaDelete(Product.class);
		
		final Root<Product> root = cd.from(Product.class);
		
		cd.where(cb.equal(root.get(IdentifiableEntity_.id), id));
		
		return this.getEntityManager().createQuery(cd).executeUpdate();
	}
}

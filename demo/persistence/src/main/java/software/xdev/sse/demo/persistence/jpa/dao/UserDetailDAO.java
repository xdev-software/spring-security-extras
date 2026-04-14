package software.xdev.sse.demo.persistence.jpa.dao;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Repository;

import software.xdev.sse.demo.entities.IdentifiableEntity_;
import software.xdev.sse.demo.entities.UserDetail;
import software.xdev.sse.demo.entities.UserDetail_;


@Repository
public class UserDetailDAO extends BaseEntityDAO<UserDetail>
{
	public UserDetailDAO(final EntityManager em)
	{
		super(em);
	}
	
	public Optional<UserDetail> getUserByEmail(final String email)
	{
		final CriteriaBuilder cb = this.getCriteriaBuilder();
		final CriteriaQuery<UserDetail> cq = cb.createQuery(UserDetail.class);
		final Root<UserDetail> r = cq.from(UserDetail.class);
		
		cq.select(r);
		cq.where(
			cb.equal(r.get(UserDetail_.emailAddress), email));
		
		return this.getEntityManager().createQuery(cq).getResultList().stream().findFirst();
	}
	
	@Transactional
	public void updateLastLoginAtToNow(final long userId)
	{
		final EntityManager em = this.getEntityManager();
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaUpdate<UserDetail> cu = cb.createCriteriaUpdate(UserDetail.class);
		final Root<UserDetail> r = cu.from(UserDetail.class);
		
		cu.set(r.get(UserDetail_.lastLoginAt), LocalDateTime.now(ZoneOffset.UTC));
		cu.where(cb.equal(r.get(IdentifiableEntity_.id), userId));
		
		em.createQuery(cu).executeUpdate();
	}
	
	public boolean isDisabled(final String userEmail)
	{
		final CriteriaBuilder cb = this.getCriteriaBuilder();
		final CriteriaQuery<Boolean> cq = cb.createQuery(Boolean.class);
		final Root<UserDetail> r = cq.from(UserDetail.class);
		
		// not null -> disabled
		cq.select(cb.isNotNull(r.get(UserDetail_.disabledAt)));
		
		cq.where(cb.equal(r.get(UserDetail_.emailAddress), userEmail));
		
		return this.getEntityManager().createQuery(cq)
			.getResultList()
			.stream()
			.findFirst()
			.orElse(true);
	}
}

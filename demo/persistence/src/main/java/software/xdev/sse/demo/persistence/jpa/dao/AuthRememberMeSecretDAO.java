package software.xdev.sse.demo.persistence.jpa.dao;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Repository;

import software.xdev.sse.demo.entities.IdentifiableEntity;
import software.xdev.sse.demo.entities.IdentifiableEntity_;
import software.xdev.sse.demo.entities.UserDetail;
import software.xdev.sse.demo.entities.UserDetail_;
import software.xdev.sse.demo.entities.auth.AuthRememberMeSecret;
import software.xdev.sse.demo.entities.auth.Secret;
import software.xdev.sse.demo.entities.auth.Secret_;
import software.xdev.sse.demo.persistence.jpa.dto.AuthRememberMeSecretDTO;


@Repository
public class AuthRememberMeSecretDAO extends SecretDAO<AuthRememberMeSecret>
{
	public AuthRememberMeSecretDAO(final EntityManager em)
	{
		super(em, AuthRememberMeSecret.class, AuthRememberMeSecret.TABLE_NAME);
	}
	
	public Optional<AuthRememberMeSecretDTO> findBy(
		final String identifier,
		final LocalDateTime createdAfterUtc)
	{
		final CriteriaBuilder cb = this.getCriteriaBuilder();
		final CriteriaQuery<AuthRememberMeSecretDTO> cq = cb.createQuery(AuthRememberMeSecretDTO.class);
		final Root<AuthRememberMeSecret> root = cq.from(AuthRememberMeSecret.class);
		final Join<AuthRememberMeSecret, UserDetail> joinUser = root.join(Secret_.user);
		
		cq.multiselect(
			root.get(Secret_.identifier),
			root.get(Secret_.cryptoAlgorithm),
			root.get(Secret_.secret),
			joinUser.get(UserDetail_.emailAddress));
		
		cq.where(
			cb.equal(root.get(Secret_.identifier), identifier),
			cb.greaterThan(root.get(Secret_.createdAt), createdAfterUtc),
			// User must be active
			cb.isNull(joinUser.get(UserDetail_.disabledAt)));
		
		return this.getEntityManager().createQuery(cq).getResultList().stream().findFirst();
	}
	
	@SuppressWarnings("java:S1192")
	@Transactional
	public void insert(final AuthRememberMeSecretDTO dto)
	{
		this.getEntityManager().createNativeQuery(
				// @formatter:off
					"INSERT INTO "
						+ AuthRememberMeSecret.TABLE_NAME
						+ "("
						+ String.join(",", List.of(
						Secret.COL_IDENTIFIER,
						Secret.COL_CRYPTO_ALGORITHM,
						Secret.COL_SECRET,
						Secret.COL_CREATED_AT,
						Secret.COL_USER))
						+ ")"
						+ " SELECT "
						+ String.join(",", List.of(
							":identifier",
							":crypto_algorithm",
							":secret",
							":created_at",
							UserDetail.TABLE_NAME + "." + IdentifiableEntity.COL_ID
						))
						+ " FROM " + UserDetail.TABLE_NAME
						+ " WHERE " + UserDetail.TABLE_NAME + "." + UserDetail.COL_EMAIL_ADDRESS + " = :email_adr"
						+ " AND " + UserDetail.TABLE_NAME + "." + UserDetail.COL_DISABLED_AT + " IS NULL")
				// @formatter:on
			.setParameter("identifier", dto.identifier())
			.setParameter("crypto_algorithm", dto.cryptoAlgorithm())
			.setParameter("secret", dto.secret())
			.setParameter("created_at", LocalDateTime.now(ZoneOffset.UTC))
			.setParameter("email_adr", dto.userEmailAddress())
			.executeUpdate();
	}
	
	@Transactional
	public void delete(final String identifier)
	{
		final CriteriaBuilder cb = this.getCriteriaBuilder();
		final CriteriaDelete<AuthRememberMeSecret> cd = cb.createCriteriaDelete(AuthRememberMeSecret.class);
		final Root<AuthRememberMeSecret> root = cd.from(AuthRememberMeSecret.class);
		
		cd.where(cb.equal(root.get(Secret_.identifier), identifier));
		
		this.getEntityManager().createQuery(cd).executeUpdate();
	}
	
	@Transactional
	public int deleteAllCreatedBefore(final LocalDateTime createdBeforeUtc)
	{
		final CriteriaBuilder cb = this.getCriteriaBuilder();
		final CriteriaDelete<AuthRememberMeSecret> cd = cb.createCriteriaDelete(this.clazz);
		final Root<AuthRememberMeSecret> root = cd.from(this.clazz);
		
		cd.where(cb.lessThan(root.get(Secret_.createdAt), createdBeforeUtc));
		
		return this.getEntityManager().createQuery(cd).executeUpdate();
	}
	
	@SuppressWarnings("java:S1192")
	@Transactional
	public int deleteAllOverUserMaxAmount(final int maxPerUser)
	{
		return this.findUserIdsOverUserMaximum(maxPerUser)
			.stream()
			.mapToInt(userId ->
				// @formatter:off
				this.getEntityManager().createNativeQuery("DELETE"
						+ " FROM " + this.tableName
						+ " WHERE " + this.tableName + "." + Secret.COL_USER + " = :user_id"
						+ " AND " + this.tableName + "." + IdentifiableEntity.COL_ID
						+ " < ("
						+ " SELECT " + this.tableName + "." + IdentifiableEntity.COL_ID
						+ " FROM " + this.tableName
						+ " WHERE " + this.tableName + "." + Secret.COL_USER + " = :user_id"
						+ " ORDER BY " + this.tableName + "." + IdentifiableEntity.COL_ID + " DESC"
						+ " LIMIT 1"
						+ " OFFSET :offset"
						+ " )")
					// @formatter:on
					.setParameter("user_id", userId)
					.setParameter("offset", maxPerUser - 1) // Offset is 0 based
					.executeUpdate()
			)
			.sum();
	}
	
	protected Set<Long> findUserIdsOverUserMaximum(final int maxPerUser)
	{
		final CriteriaBuilder cb = this.getCriteriaBuilder();
		
		final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		final Root<AuthRememberMeSecret> root = cq.from(this.clazz);
		final Join<AuthRememberMeSecret, UserDetail> joinUser = root.join(Secret_.user);
		
		cq.select(joinUser.get(IdentifiableEntity_.id));
		cq.groupBy(joinUser.get(IdentifiableEntity_.id));
		cq.having(cb.greaterThan(cb.count(root.get(IdentifiableEntity_.id)), (long)maxPerUser));
		
		return new HashSet<>(this.getEntityManager().createQuery(cq).getResultList());
	}
}

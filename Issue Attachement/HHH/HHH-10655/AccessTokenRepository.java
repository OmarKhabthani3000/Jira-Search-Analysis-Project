package cz.audatex.audanext.auth.repository;

import cz.audatex.audanext.auth.domain.oauth.AccessTokenVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;

public interface AccessTokenRepository extends JpaRepository<AccessTokenVO, String> {

	@Transactional(readOnly = true)
	AccessTokenVO findByAuthenticationHash(@Nonnull String authenticationHash);

	@Transactional
	@Modifying
	@Query("DELETE FROM AccessTokenVO at WHERE at.authentication.hash = :hash")
	void deleteByAuthenticationHash(@Nonnull @Param("hash") String authenticationHash);

	@Transactional(readOnly = true)
	AccessTokenVO findByTokenHash(@Nonnull String tokenHash);

	@Transactional
	void deleteByTokenHash(@Nonnull String tokenHash);

	@Transactional
	void deleteByClientId(@Nonnull String clientId);
}

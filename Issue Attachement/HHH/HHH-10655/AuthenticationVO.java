package cz.audatex.audanext.auth.domain.oauth;

import cz.audatex.audanext.auth.domain.AbstractSequenceEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "a_authentication", uniqueConstraints = {
	 @UniqueConstraint(name = "uq__authentication__hash", columnNames = "hash")
})
@Getter
@Setter
public class AuthenticationVO extends AbstractSequenceEntity {

	private static final long serialVersionUID = -1304228274805232375L;

	@Column(name = "hash", nullable = false, unique = true)
	private String hash;

	@Column(name = "stored_request", nullable = true, length = 4096)
	private OAuth2Request storedRequest;

	@Column(name = "user_authentication", nullable = true, length = 4096)
	private Authentication userAuthentication;
}

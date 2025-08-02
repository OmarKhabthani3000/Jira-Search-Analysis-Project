package cz.audatex.audanext.auth.domain.oauth;

import cz.audatex.audanext.auth.domain.member.UserVO;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Persistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.CascadeType.DETACH;
import static javax.persistence.CascadeType.REFRESH;

@Entity
@Table(name = "a_access_token", indexes = {
	 @Index(name = "idx__access_token__user_id", columnList = "user_id"),
	 @Index(name = "idx__access_token__client_id", columnList = "client_id"),
	 @Index(name = "idx__access_token__client_id__user_id", columnList = "client_id, user_id")
})
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
public class AccessTokenVO implements Persistable<String> {

	private static final long serialVersionUID = -6756173942426242139L;

	@Id
	@Setter(AccessLevel.PRIVATE)
	private String id;

	@OneToOne(cascade = {REFRESH, DETACH})
	@JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk__access_token__member_user"))
	private UserVO user;

	@Column(name = "client_id")
	private String clientId;

	@OneToOne(cascade = ALL)
	@JoinColumn(name = "token_id", nullable = false, foreignKey = @ForeignKey(name = "fk__access_token__token"))
	private TokenVO token;

	@OneToOne(cascade = ALL)
	@JoinColumn(name = "authentication_id", nullable = false, foreignKey = @ForeignKey(name = "fk__access_token__authentication"))
	private AuthenticationVO authentication;

	public static AccessTokenVO createWithId(String id) {
		AccessTokenVO accessToken = new AccessTokenVO();
		accessToken.setId(id);
		return accessToken;
	}

	@Override
	@Transient
	public boolean isNew() {
		return null == getId();
	}
}

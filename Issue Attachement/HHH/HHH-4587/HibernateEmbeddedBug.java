package test;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.Criteria;
import org.hibernate.QueryException;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;

import br.com.sistram.database.hibernate.HibernateUtil;

public class HibernateEmbeddedBug {
	@Test(expected = QueryException.class)
	public void testAlias() {
		Session s = HibernateUtil.openSession();// Returns a Session to the
		// database
		Criteria crit = s.createCriteria(Motorista.class);
		crit.createAlias("contato", "contato");
		crit.add(Restrictions.like("contato.nome", "jo", MatchMode.ANYWHERE));
		crit.list();
	}

	@Entity
	@Table
	private static class Motorista {
		@Id
		@GeneratedValue
		private int id;
		@Embedded
		private Contato contato;
	}

	@Embeddable
	private static class Contato {
		private String name;
	}
}

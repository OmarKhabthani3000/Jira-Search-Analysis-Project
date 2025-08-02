package de.hska;

import static javax.ejb.TransactionAttributeType.REQUIRED;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@Stateless
@Remote(Kundenverwaltung.class)
public class KundenverwaltungBean implements Kundenverwaltung {
	private static final String PERSISTENCE_CONTEXT_HSKA = "hskaPersistence";

	@PersistenceContext(name=PERSISTENCE_CONTEXT_HSKA)
	private EntityManager em;


	@TransactionAttribute(REQUIRED)
	public Kunde findKunde(Long id) {
		final Kunde kunde = em.find(Kunde.class, id);
		return kunde;
	}


	@TransactionAttribute(REQUIRED)
	public Kunde updateKunde(Kunde kunde) {
//TODO
//		final Kunde tmp = em.find(Kunde.class, kunde.getId());
//		if (tmp == null) {
//			return null;
//		}

		// TODO Bug von JBoss, falls das zu aendernde Entity "detached" ist und eine 1:1-Bez hat
		// Workaround: das referenzierte Objekt vor dem Aufruf von merge() explizit laden
		//final Betreuer tmpBetreuer = em.find(Betreuer.class, kunde.getBetreuer().getId());
		//kunde.setBetreuer(tmpBetreuer);

		kunde = em.merge(kunde);
		return kunde;
	}


	public Betreuer findBetreuer(Long id) {
		final Betreuer betreuer = em.find(Betreuer.class, id);
		return betreuer;
	}


	public Betreuer updateBetreuer(Betreuer betreuer) {
		betreuer = em.merge(betreuer);
		return betreuer;
	}
}

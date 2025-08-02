/**
 *
 * @author Christophe Hertigers
 * @version 1.0
 */
package nl.mcb.sfa.dao.gebruiker;

import be.realsoftware.ff.common.Logger;
import be.realsoftware.ff.dao.DAOException;
import be.realsoftware.ff.data.valuelist.ValueListManager;
import nl.mcb.sfa.dao.HibernateDAO;
import nl.mcb.sfa.util.Parameter;
import nl.mcb.sfa.vo.gebruiker.GebruikerVO;
import nl.mcb.sfa.vo.gebruiker.OrderDefaultsGroepVO;
import nl.mcb.sfa.vo.gebruiker.GebruikerAfdelingVO;
import nl.mcb.sfa.vo.vestiging.VestigingVO;
import nl.mcb.sfa.vo.util.TaalVO;
import nl.mcb.sfa.application.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HIBGebruikerDAO extends HibernateDAO implements GebruikerDAO {

    /** Singleton instance */
    private static GebruikerDAO instance;

    /**
     * Private constructor; this is a singleton. Use the getInstance() method.
     */
    private HIBGebruikerDAO() {
        super();
    }

    /**
     * Get an instance of GebruikerDAO
     * @return an instance
     */
    public static GebruikerDAO getInstance() {
        if (instance == null) {
            synchronized (HIBGebruikerDAO.class) {
                if (instance == null) {
                    instance = new HIBGebruikerDAO();
                }
            }
        }
        return instance;
    }

    /**
     * @see GebruikerDAO#findGebruiker
     */
    public GebruikerVO findGebruiker(String gebruikerId) throws DAOException {
        return (GebruikerVO) this.load(GebruikerVO.class, gebruikerId);
    }

    /**
     * @see GebruikerDAO#findGebruiker
     */
    public GebruikerVO findGebruikerByUserName(String userName) throws DAOException {
        List params = new ArrayList();
        params.add(new Parameter(userName, Parameter.TYPE_STRING, "userName"));

        StringBuffer query = new StringBuffer();
        query.append("select gebruiker.gebruikerId, gebruiker.version, gebruiker.paswoord, ");
        query.append("gebruiker.achternaam, gebruiker.voornaam, gebruiker.voorletters, gebruiker.tussenvoegsels, ");
        query.append("gebruiker.referentie, gebruiker.email, gebruiker.orderDefaultsGroep.id, gebruiker.gebruikerAfdeling.id, ");
        query.append("gebruiker.gebruikerAfdeling.naam, gebruiker.gebruikerAfdeling.vestiging.id, ");
        query.append("gebruiker.vestiging.id, gebruiker.taal.id ");
        query.append("from nl.mcb.sfa.vo.gebruiker.GebruikerVO gebruiker ");
        query.append("where gebruiker.actief = 1 and gebruiker.gebruikerNaam = :userName");

        List results = this.find(query.toString(), params);

        if (results == null) {
            throw new DAOException("Geen gebruiker met userName=" + userName + " gevonden in de database");
        }
        if (results.size() != 1) {
            throw new DAOException("Meer dan 1 gebruiker met userName=" + userName + " gevonden in de database");
        }

        Object[] o = (Object[]) results.get(0);
        GebruikerVO gvo = new GebruikerVO();
        int index = 0;
        gvo.setGebruikerId((String) o[index++]);
        gvo.setVersion((Integer) o[index++]);
        gvo.setGebruikerNaam(userName);
        gvo.setPaswoord((String) o[index++]);
        gvo.setAchternaam((String) o[index++]);
        gvo.setVoornaam((String) o[index++]);
        gvo.setVoorletters((String) o[index++]);
        gvo.setTussenvoegsels((String) o[index++]);
        gvo.setReferentie((String) o[index++]);
        gvo.setEmail((String) o[index++]);
        OrderDefaultsGroepVO odgvo = new OrderDefaultsGroepVO();
        odgvo.setId((String) o[index++]);
        gvo.setOrderDefaultsGroep(odgvo);
        GebruikerAfdelingVO gavo = new GebruikerAfdelingVO();
        gavo.setId((String) o[index++]);
        gavo.setNaam((String) o[index++]);
        gavo.setVestiging(new VestigingVO((String) o[index++]));
        gvo.setGebruikerAfdeling(gavo);
        gvo.setVestiging(new VestigingVO((String) o[index++]));
        gvo.setTaal(new TaalVO((String) o[index++]));

        return gvo;
    }

    /**
     * @see GebruikerDAO#findGebruikers
     */
    public List findGebruikers() throws DAOException {
        List params = new ArrayList();
        StringBuffer query = new StringBuffer();
        query.append("select new nl.mcb.sfa.vo.gebruiker.GebruikerVO(gebruiker.gebruikerId, ");
        query.append("gebruiker.version, ");
        query.append("gebruiker.voornaam, ");
        query.append("gebruiker.achternaam, ");
        query.append("gebruiker.referentie, ");
        query.append("gebruiker.email, ");
        query.append("gebruiker.tussenvoegsels) ");
        query.append("from nl.mcb.sfa.vo.gebruiker.GebruikerVO gebruiker ");
        query.append("where gebruiker.actief = 1 ");
        query.append("order by gebruiker.achternaam");

        Logger.debug(this, "Query : " + query.toString());
        return this.find(query.toString(), params);
    }

    /**
     * @see GebruikerDAO#findGebruikers
     */
    public List findGebruikers(String naam, String referentie, String gebruikerAfdeling, String orderDefaultsGroepId) throws DAOException {
        List params = new ArrayList();
        StringBuffer query = new StringBuffer();
        query.append("select new nl.mcb.sfa.vo.gebruiker.GebruikerVO(gebruiker.gebruikerId, ");
        query.append("gebruiker.version, ");
        query.append("gebruiker.voornaam, ");
        query.append("gebruiker.achternaam, ");
        query.append("gebruiker.referentie, ");
        query.append("gebruiker.email, ");
        query.append("gebruiker.tussenvoegsels) ");
        query.append("from nl.mcb.sfa.vo.gebruiker.GebruikerVO gebruiker ");
        query.append("where gebruiker.actief = 1 ");

        if ((naam == null || naam.trim().length() == 0) && (referentie == null || referentie.trim().length() == 0)
                && (gebruikerAfdeling == null || gebruikerAfdeling.trim().length() == 0)
                && (orderDefaultsGroepId == null || orderDefaultsGroepId.trim().length() == 0)) {
            return new ArrayList();
        }

        if (naam != null && naam.trim().length() != 0) {
            query.append(" and upper(gebruiker.achternaam) like :naam");
            params.add(new Parameter("%" + naam.toUpperCase() + "%", Parameter.TYPE_STRING, "naam"));
        }

        if (referentie != null && referentie.trim().length() != 0) {
            query.append(" and gebruiker.referentie like :ref");
            params.add(new Parameter("%" + referentie.toUpperCase() + "%", Parameter.TYPE_STRING, "ref"));
        }

        if (gebruikerAfdeling != null && gebruikerAfdeling.trim().length() != 0) {
            query.append(" and gebruiker.gebruikerAfdeling.id = :gebruikerAfdelingId");
            params.add(new Parameter(gebruikerAfdeling, Parameter.TYPE_STRING, "gebruikerAfdelingId"));
        }

        if (orderDefaultsGroepId != null && orderDefaultsGroepId.trim().length() != 0) {
            query.append(" and gebruiker.orderDefaultsGroep.id = :orderDefaultsGroepId");
            params.add(new Parameter(orderDefaultsGroepId, Parameter.TYPE_STRING, "orderDefaultsGroepId"));
        }

        Logger.debug(this, "Query : " + query.toString());
        for (int i = 0; i < params.size(); i++) {
            Parameter parameter = (Parameter) params.get(i);
            Logger.debug(this, "Param " + i + " : " + parameter.getValue());
        }
        return this.find(query.toString(), params, 100);
    }

    /**
     * @see GebruikerDAO#findGebruikersVanGebruikersGroep
     */
    public List findGebruikersVanGebruikersGroep(String gebruikersGroepId) throws DAOException {
        List params = new ArrayList();
        params.add(new Parameter(gebruikersGroepId, Parameter.TYPE_STRING, "gebruikersgroepid"));
        return this.find("select new nl.mcb.sfa.vo.gebruiker.GebruikerVO(gebruiker.gebruikerId, gebruiker.version) from nl.mcb.sfa.vo.gebruiker.GebruikerVO gebruiker where gebruiker.actief = 1 and :gebruikersgroepid = some elements(gebruiker.gebruikersGroepen)", params);
    }

    /**
     * @see GebruikerDAO#findGebruikersVanOrderDefaultsGroep
     */
    public List findGebruikersVanOrderDefaultsGroep(String orderDefaultsGroepId) throws DAOException {
        List params = new ArrayList();
        Logger.debug(this, "orderDefaultsGroepId = " + orderDefaultsGroepId);
        params.add(new Parameter(orderDefaultsGroepId, Parameter.TYPE_STRING, "orderDefaultsGroepId"));
        return this.find("select new nl.mcb.sfa.vo.gebruiker.GebruikerVO(gebruiker.gebruikerId, gebruiker.version, gebruiker.voornaam, gebruiker.achternaam, gebruiker.referentie) from nl.mcb.sfa.vo.gebruiker.GebruikerVO gebruiker where gebruiker.actief = 1 and gebruiker.orderDefaultsGroep.id = :orderDefaultsGroepId", params);
    }

    /**
     * @see GebruikerDAO#updateGebruiker
     */
    public void updateGebruiker(GebruikerVO gvo) throws DAOException {
        /* Not Supported */
    }

    public void updateGebruikerOrderDefaultsGroep(GebruikerVO gvo) throws DAOException {
        /* Not Supported */
        throw new RuntimeException("Operation not supported");
    }

    /**
     * @see GebruikerDAO#deleteGebruikersGroepenVanGebruiker
     */
    public void deleteGebruikersGroepenVanGebruiker(String gebruikerId) throws DAOException {
        /* Not Supported */
    }

    /**
     * @see GebruikerDAO#deleteRollenVanGebruiker
     */
    public void deleteRollenVanGebruiker(String gebruikerId, List rollen) throws DAOException {
    }

    /**
     * @see GebruikerDAO#insertGebruikersGroepenVanGebruiker
     */
    public void insertGebruikersGroepenVanGebruiker(String gebruikerId, List gebruikersGroepen) throws DAOException {
        /* Not Supported */
    }

    /**
     * @see GebruikerDAO#deleteRollenVanGebruiker
     */
    public void deleteRollenVanGebruiker(String gebruikerId) throws DAOException {
        /* Not Supported */
    }

    /**
     * @see GebruikerDAO#insertRollenVanGebruiker
     */
    public void insertRollenVanGebruiker(String gebruikerId, List rollen) throws DAOException {
        /* Not Supported */
    }

    /**
     * @see GebruikerDAO#getVersion
     */
    public Integer getVersion(String gebruikerId) throws DAOException {
        List params = new ArrayList();
        params.add(new Parameter(gebruikerId, Parameter.TYPE_STRING, "gebruikerid"));
        List results = this.find("select gebruiker.version from nl.mcb.sfa.vo.gebruiker.GebruikerVO gebruiker where gebruiker.gebruikerId = :gebruikerid", params);

        if (results == null || results.size() < 1) {
            throw new DAOException("Geen gebruiker met gebruikerId=" + gebruikerId + " gevonden in de database");
        } else if (results.size() > 1) {
            throw new DAOException("Meer dan 1 gebruiker met gebruikerId=" + gebruikerId + " gevonden in de database");
        }
        return (Integer) results.get(0);
    }

    /**
     * @see GebruikerDAO#incrementVersion
     */
    public void incrementVersion(String gebruikerId) throws DAOException {
        /* Not Supported */
    }
}


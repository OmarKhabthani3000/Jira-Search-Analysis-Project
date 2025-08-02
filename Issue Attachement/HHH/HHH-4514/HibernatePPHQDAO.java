/*
 * Bupa
 * 15 - 19 Bloomsbury Way, London, WC1A 2BA
 * tel: 020 7656 2000
 * web: www.bupa.co.uk
 *
 * This Computer Program was first published in 2005 by Bupa Limited
 * in the united kingdom - (c) Bupa Limited 2004. All rights reserved.
 *
 */
package com.bupa.app.wellness.appservice.dao.pphq;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import com.bupa.app.wellness.appservice.dataobject.company.CompanyDO;
import com.bupa.app.wellness.appservice.dataobject.pphq.AbsenceDetailsDO;
import com.bupa.app.wellness.appservice.dataobject.pphq.ClinicalDetailsDO;
import com.bupa.app.wellness.appservice.dataobject.pphq.EmploymentHistoryDO;
import com.bupa.app.wellness.appservice.dataobject.pphq.EmploymentProblemsDO;
import com.bupa.app.wellness.appservice.dataobject.pphq.HealthDetailsDO;
import com.bupa.app.wellness.appservice.dataobject.pphq.HealthProblemsDO;
import com.bupa.app.wellness.appservice.dataobject.pphq.JobDetailsDO;
import com.bupa.app.wellness.appservice.dataobject.pphq.PPHQDO;
import com.bupa.app.wellness.appservice.dataobject.pphq.PPHQStatusAuditDO;
import com.bupa.app.wellness.appservice.dataobject.pphq.PPHQStatusDO;
import com.bupa.app.wellness.appservice.dataobject.pphq.ShortPPHQDO;
import com.bupa.app.wellness.appservice.dataobject.report.PPHQReportDO;
import com.bupa.app.wellness.appservice.dataobject.user.CandidateDO;
import com.bupa.app.wellness.appservice.util.Constants;
import com.bupa.framework.common.exception.BupaApplicationException;
import com.bupa.framework.common.exception.BupaDataAccessException;
import com.bupa.framework.dataaccess.dao.AbstractHibernateDAO;

/**
 * <p>
 * An implementation of PPHQDAO using Hibernate. The data access object uses a
 * hibernate configuration to persist and recover PPHQDO and it references
 * objects
 * </p>
 * <p>
 * The HibernatePPHQDAO uses a active configured Hibnernate Session passed into
 * the constructor. The DAO methods should use the Session object but not
 * attempt to open, flush, close the session or manage the transaction in
 * anyway. The underlying transaction should be managed by the client code
 * allowing multiple DAO methods to be used in a single transaction
 * </p>
 * 
 * @author $Author: aashimas $
 * @version $Revision: 1.75.2.1 $ $Date: 2008/12/22 16:09:27 $
 */
public class HibernatePPHQDAO extends AbstractHibernateDAO implements PPHQDAO {

	/**
	 * Logger This commented... throws the exception (IllegalAccessor) for LOG4J
	 * very randomly, need to resolve this issue
	 */
	private Log log = LogFactory.getLog(HibernatePPHQDAO.class);
	private final String CLASS_NAME = this.getClass().getName();

	private final static String HQL_FIND_PPHQ_BY_CANDIDATE_SURNAME_FORENAME2 = new StringBuffer(
			"from PPHQDO pphq ").append(
			"where lower(pphq.candidate.foreName) like :forename ").append(
			"and lower(pphq.candidate.surName) like :surname ").append(
			"order by pphq.createdDate").toString();

	private final static String HQL_FIND_PPHQ_BY_COMPANY_PPHQSTATUS_AND_DATE = new StringBuffer(
			"from PPHQStatusAuditDO psa ")
			.append("where psa.pphqStatus.type=:status ")
			.append(
					"and psa.pphqStatusDate between :statusDateFrom and :statusDateTo ")
			.append("and psa.pphq.pphqClient.pphqClientId=:companyId ").append(
					"order by psa.pphqStatusDate").toString();

	private final static String HQL_FIND_PPHQ_BY_COMPANY_PPHQSTATUS_AND_DATE_NEW = new StringBuffer(
			"from PPHQStatusAuditDO psa ")
			.append("where psa.pphqStatus.type=:status ")
			.append(
					"and psa.pphqStatusDate between :statusDateFrom and :statusDateTo ")
			.append("and psa.pphq.pphqClient.name=:companyName ").append(
					"order by psa.pphqStatusDate").toString();

	private final static String HQL_FIND_PPHQ_BY_COMPANY_REPORTSTATUS_AND_DATE = new StringBuffer(
			"from ReportStatusAuditDO rsa ")
			.append("where rsa.pphqStatus.type=:status ")
			.append(
					"and rsa.reportStatusDate between :statusDateFrom and :statusDateTo ")
			.append(
					"and rsa.pphqReport.pphq.pphq.pphqClient.pphqClientId=:companyId ")
			.append("order by rsa.reportStatusDate").toString();

	private final static String HQL_FIND_PPHQ_BY_COMPANY_REPORTSTATUS_AND_DATE_NEW = new StringBuffer(
			"from ReportStatusAuditDO rsa ")
			.append("where rsa.pphqStatus.type=:status ")
			.append(
					"and rsa.reportStatusDate between :statusDateFrom and :statusDateTo ")
			.append(
					"and rsa.pphqReport.pphq.pphq.pphqClient.name=:companyName ")
			.append("order by rsa.reportStatusDate").toString();

	private final static String HQL_FIND_PPHQ_BY_COMPANY_AND_CANDIDATE_OLD = new StringBuffer(
			"from PPHQDO pphq ").append(
			"where pphq.candidate.foreName like :forename ").append(
			"and pphq.candidate.surName like :surname ").append(
			"and pphq.pphq.pphqClient.pphqClientId = :companyId ").append(
			"order by pphq.createdDate").toString();

	private final static String HQL_FIND_PPHQ_BY_COMPANY_AND_CANDIDATE = new StringBuffer(
			"from PPHQDO pphq ").append(
			"where lower(pphq.candidate.foreName) like lower(:forename) ")
			.append("and lower(pphq.candidate.surName) like lower(:surname) ")
			.append("and pphq.pphq.pphqClient.name = :companyName ").append(
					"order by pphq.createdDate").toString();

	private final static String HQL_FIND_PPHQ_BY_CANDIDATE_REFFERING_COMPANY = "from PPHQDO as pphq "
			+ " pphq.pphqClient.pphqClientId = ? and pphq.referringManager.userName = ? and pphq.status = ? and  pphq.createdDate BETWEEN ? and ?";

	private final static String HQL_FIND_ALL_EMP_PROBLEMS = new StringBuffer(
			"from EmploymentProblemsDO as EP ").append(
			"where EP.questionDeleted = 0").toString();
	private final static String HQL_FIND_ALL_HEALTH_PROBLEMS = new StringBuffer(
			"from HealthProblemsDO as HP ").append(
			"where HP.questionDeleted = 0").toString();
	private final static String HQL_FIND_ALL_JOB_HAZZARDS = "from JobHazzardsDO as JH order by JH.id";
	private final static String HQL_FIND_ALL_JOB_INVOLVEMENTS = "from JobInvolvmentDO as JI order by JI.id";

	private final static String HQL_FIND_ALL_NIGHT_WORKING = "from NightWorkingDO as NW order by NW.id";
	private final static String HQL_FIND_ALL_FOOD_HANDLING = "from FoodHandlingDO as FH order by FH.id";
	private final static String HQL_FIND_ALL_IMMUNISATIONS = "from ImmunisationDetailsDO as IM order by IM.id";

	// PPHQ Rel.4 Code added on<11/03/2008> : Start For
	// <4.4><Customise PPHQ: Short PPHQ>
	private final static String HQL_FIND_ALL_SHORT_PPHQ = "from ShortPPHQDO as SP order by SP.id";
	// PPHQ Rel.4 Code added on<11/03/2008> : End
	private final static String HQL_FIND_PENDING_PPHQ = new StringBuffer(
			"select distinct(psa.pphq.id), psa.pphq.candidate.foreName, ")
			.append("psa.pphq.candidate.surName, psa.pphq.company.name, ")
			.append(
					"psa.pphq.referringManager.foreName, psa.pphq.referringManager.surName, ")
			.append("psa.pphqStatusDate, psa.pphq.locked ").append(
					"from PPHQStatusAuditDO psa ").append(
					"where psa.pphqStatus.type = :statusPPHQCompleted ")
			.append("and psa.pphq.closed = :closed ").append(
					"order by psa.pphqStatusDate").toString();

	private final static String HQL_FIND_HR_SUPER_USER = new StringBuffer(
			"select hr.id ").append("from HRUserDO hr ").append(
			"where hr.pphqClient.pphqClientId = :companyId ").append(
			"and hr.superUser = :issuperuser ").append("order by hr.foreName")
			.toString();

	private final static String HQL_GET_TOTAL_PPHQ = "SELECT do.id from PPHQDO do where do.pphqClient = :companyId";
	/**
	 * Getting the List of candidates of a perticular manager.
	 */
	private final static String HQL_FIND_ALL_CANDIDATES = "from PPHQDO as pphq where pphq.referringManager.id = ?";
	/**
	 * Query to get the candidate.
	 */
	private final static String HQL_FIND_CANDIDATE = "from PPHQDO as pphq where pphq.candidate = ?";

	private final static String HQL_FIND_STATUS_BY_TEXT = "from PPHQStatusDO as status where status.type = ?";

	/**
	 * 
	 */
	private final static String HQL_FIND_CANDIDATE_DETAILS = "from PPHQDO pphq where pphq.id = :pphqID";

	private final static String HQL_FIND_CANDIDATE_AND_STATUS = "from PPHQDO as pphq where "
			+ "pphq.candidate = ? and pphq.pphqStatusAudit.pphqStatus.type = ?";

	// select * from pphq where REFERRING_MANAGER_ID = 6 AND
	// pphq.PPHQ_ID IN (
	// select distinct PPHQ_ID from PPHQ_STATUS_AUDIT where
	// PPHQ_STATUS_AUDIT.PPHQ_STATUS_ID = 10
	// UNION
	// SELECT distinct PPHQ_ID FROM PPHQREPORT WHERE PPHQREPORT.PPHQREPORT_ID
	// IN(
	// select PPHQREPORT_ID from REPORT_STATUS_AUDIT where
	// REPORT_STATUS_AUDIT.REPORT_STATUS_AUDIT_ID = 6
	// )
	//
	// );
	//

	/**
	 * @param instantiates
	 *            the session from AbstractHibernateDAO
	 */
	public HibernatePPHQDAO(Session session) {
		super(session);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#saveJobDetails(JobDetailsDO
	 *      jobDetails)
	 */
	public JobDetailsDO saveJobDetails(JobDetailsDO jobDetails)
			throws BupaDataAccessException {
		final String METHOD_NAME = "saveJobDetails";

		try {
			Session hSession = this.getHibernateSession();
			hSession.saveOrUpdate(jobDetails);

		} catch (HibernateException he) {
			log.error("Error while saving Job details :: More : ", he);
			throw new BupaDataAccessException(
					"Save Update failed for JOB DETAILS", he);
		}

		return jobDetails;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#saveEmploymentProblems(EmploymentProblemsDO
	 *      employmentProblems)
	 */
	public EmploymentProblemsDO saveEmploymentProblems(
			EmploymentProblemsDO employmentProblems)
			throws BupaDataAccessException {
		final String METHOD_NAME = "saveEmploymentProblems";

		try {
			Session hSession = this.getHibernateSession();
			hSession.saveOrUpdate(employmentProblems);

		} catch (HibernateException he) {
			log.error("Error while saving Employment Problems :: More : ", he);
			throw new BupaDataAccessException(
					"Save Update failed for EmploymentProblem" + he);
		}

		return employmentProblems;
	}

	// PPHQ Rel.4 Code added on<31/03/2008> : Start For <14.5><Complete a PPHQ>
	/**
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#saveShortPPHQ(ShortPPHQDO
	 *      shortPPHQ)
	 */
	public ShortPPHQDO saveShortPPHQ(ShortPPHQDO shortPPHQ)
			throws BupaDataAccessException {
		final String METHOD_NAME = "saveShortPPHQ";

		try {
			Session hSession = this.getHibernateSession();
			if (shortPPHQ.getId() == null) {
				hSession.save(shortPPHQ);
			} else {
				hSession.update(shortPPHQ);
			}
		} catch (HibernateException he) {
			log.error("Error while saving Short PPHQ Questions :: More : ", he);
			throw new BupaDataAccessException(
					"Save Update failed for Short PPHQ Questions" + he);
		}

		return shortPPHQ;
	}

	// PPHQ Rel.4 Code added on<31/03/2008> : End

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#saveCandidateDetails(CandidateDO
	 *      candidate)
	 */
	public CandidateDO saveCandidateDetails(CandidateDO candidate)
			throws BupaDataAccessException {

		final String METHOD_NAME = "saveCandidateDetails";

		try {
			Session hSession = this.getHibernateSession();
			hSession.saveOrUpdate(candidate);

		} catch (HibernateException he) {
			log.error("Error while saving Candidate details :: More : ", he);
			throw new BupaDataAccessException(
					"Save Update failed for Candidate Details from PPHQ", he);
		}

		return candidate;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#saveEmploymentHistory
	 *      (EmploymentHistoryDO employmentHistory)
	 */
	public EmploymentHistoryDO saveEmploymentHistory(
			EmploymentHistoryDO employmentHistory)
			throws BupaDataAccessException {
		final String METHOD_NAME = "saveEmploymentHistory";

		try {
			Session hSession = this.getHibernateSession();
			if (employmentHistory.getId() == null) {
				hSession.save(employmentHistory);
			} else {
				hSession.update(employmentHistory);
			}
		} catch (HibernateException he) {
			log.error("Error while saving Employment History :: More : ", he);
			throw new BupaDataAccessException(
					"Save Update failed for Employment History", he);
		}

		return employmentHistory;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#saveAbsenceDetails
	 *      (AbsenceDetailsDO absenceDetails)
	 */
	public AbsenceDetailsDO saveAbsenceDetails(AbsenceDetailsDO absenceDetails)
			throws BupaDataAccessException {
		final String METHOD_NAME = "saveAbsenceDetails";

		try {
			Session hSession = this.getHibernateSession();
			hSession.saveOrUpdate(absenceDetails);

		} catch (HibernateException he) {
			log.error("Error while saving absence details :: More : ", he);
			throw new BupaDataAccessException(
					"Save update failed for absence details", he);
		}

		return absenceDetails;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#saveHealthDetails
	 *      (HealthDetailsDO healthDetails)
	 */
	public HealthDetailsDO saveHealthDetails(HealthDetailsDO healthDetails)
			throws BupaDataAccessException {

		final String METHOD_NAME = "saveHealthDetails";

		try {
			Session hSession = this.getHibernateSession();
			if (healthDetails.getId() == null) {
				hSession.save(healthDetails);
			} else {
				hSession.update(healthDetails);
			}
		} catch (HibernateException he) {
			log.error("Error while saving Health Details :: More : ", he);
			throw new BupaDataAccessException(
					"Save Update failed for Health Details", he);
		}

		return healthDetails;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#saveClinicalDetails
	 *      (ClinicalDetailsDO clinicalDetails)
	 */
	public ClinicalDetailsDO saveClinicalDetails(
			ClinicalDetailsDO clinicalDetails) throws BupaDataAccessException {
		final String METHOD_NAME = "saveClinicalDetails";

		try {
			Session hSession = this.getHibernateSession();
			if (clinicalDetails.getId() == null) {
				hSession.save(clinicalDetails);
			} else {
				hSession.update(clinicalDetails);
			}

		} catch (HibernateException he) {
			log.error("Error while saving Clincal Details :: More : ", he);
			throw new BupaDataAccessException(
					"Save Update failed for Clinical Details", he);
		}

		return clinicalDetails;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#saveHealthProblems
	 *      (HealthProblemsDO healthProblems)
	 */
	public HealthProblemsDO saveHealthProblems(HealthProblemsDO healthProblems)
			throws BupaDataAccessException {
		final String METHOD_NAME = "saveHealthProblems";

		try {
			Session hSession = this.getHibernateSession();
			hSession.saveOrUpdate(healthProblems);

		} catch (HibernateException he) {
			log.error("Error while saving Health Problems :: More : ", he);
			throw new BupaDataAccessException(
					"Save Update failed for Health Problems", he);
		}

		return healthProblems;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#saveCompany(CompanyDO
	 *      company)
	 */
	public CompanyDO saveCompany(CompanyDO company)
			throws BupaApplicationException {
		final String METHOD_NAME = "saveCompany";

		try {
			Session hSession = this.getHibernateSession();
			hSession.saveOrUpdate(company);

		} catch (HibernateException he) {
			log.error("Error while saving company :: More : ", he);
			throw new BupaDataAccessException(
					"Save Update failed for Company from PPHQ", he);
		}

		return company;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#savePPHQ(PPHQDO
	 *      pphq)
	 */
	public PPHQDO savePPHQ(PPHQDO pphq) throws BupaDataAccessException {
		final String METHOD_NAME = "savePPHQ()";

		try {
			Session hSession = this.getHibernateSession();
			hSession.saveOrUpdate(pphq);
		} catch (HibernateException he) {
			log.error("Error while saving PPHQ :: More : ", he);
			throw new BupaDataAccessException("Save Update failed for PPHQ", he);
		}

		return pphq;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#savePPHQStatusAudit(PPHQStatusAuditDO
	 *      statusAuditDO)
	 */
	public PPHQStatusAuditDO savePPHQStatusAudit(PPHQStatusAuditDO statusAuditDO)
			throws BupaDataAccessException {
		final String METHOD_NAME = "savePPHQ()";

		try {
			Session hSession = this.getHibernateSession();
			if (statusAuditDO.getId() == null) {
				hSession.save(statusAuditDO);
			} else {
				hSession.update(statusAuditDO);
			}
		} catch (HibernateException he) {
			log.error("Error while saving PPHQStatusAudit :: More : ", he);
			throw new BupaDataAccessException(
					"Save Update failed for PPHQStatusAudit from PPHQ", he);
		}

		return statusAuditDO;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#findPPHQById(Long
	 *      pphqId)
	 */
	public PPHQDO findPPHQById(Long pphqId) throws BupaDataAccessException {
		PPHQDO pphq = null;
		try {
			if (log.isDebugEnabled()) {
				log
						.debug("inside method findPPHQById : searching for pphq id : "
								+ (pphq != null ? pphqId.longValue() : 0));
			}
			pphq = (PPHQDO) this.getHibernateSession().load(PPHQDO.class,
					pphqId);
		} catch (HibernateException he) {
			log.error("Error while finding PPHQ by :: More : ", he);
			throw new BupaDataAccessException(
					"Error while findPPHQById(Long pphqId) : More : ", he);
		}
		return pphq;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#findPPHQByCandidateSurnameAndForeName(String
	 *      surName, String foreName)
	 */
	public List findPPHQByCandidateSurnameAndForeName(String surName,
			String foreName, Long companyId) throws BupaDataAccessException {
		List list = null;
		try {
			StringBuffer HQL_FIND_PPHQ_BY_CANDIDATE_SURNAME_FORENAME = new StringBuffer(
					"from PPHQDO as pphq where ");

			boolean isCompanyRequired = false;
			boolean isSurNameRequired = false;
			boolean isForeNameRequired = false;

			if (surName != null && !surName.equals("")) {
				HQL_FIND_PPHQ_BY_CANDIDATE_SURNAME_FORENAME
						.append("pphq.pphqClient.pphqClientId = ? ");
				isCompanyRequired = true;
			}
			if (surName != null && !surName.equals("")) {
				HQL_FIND_PPHQ_BY_CANDIDATE_SURNAME_FORENAME
						.append(" and lower(pphq.candidate.surName) like ?");
				isSurNameRequired = true;
			}
			// ForeName NOT mandatory
			if (foreName != null && !foreName.equals("")) {
				HQL_FIND_PPHQ_BY_CANDIDATE_SURNAME_FORENAME
						.append(" and lower(pphq.candidate.foreName) like ?");
				isForeNameRequired = true;
			}
			HQL_FIND_PPHQ_BY_CANDIDATE_SURNAME_FORENAME
					.append(" order by pphq.createdDate DESC");

			Query query = this.getHibernateSession().createQuery(
					HQL_FIND_PPHQ_BY_CANDIDATE_SURNAME_FORENAME.toString());
			if (log.isDebugEnabled()) {
				log.debug("Processing query : "
						+ HQL_FIND_PPHQ_BY_CANDIDATE_SURNAME_FORENAME
								.toString());
				log.debug("Binding parameter 1 : " + companyId.longValue());
				log.debug("Binding parameter 2 : " + surName);
				log.debug("Binding parameter 3 : " + foreName);
			}

			if (isCompanyRequired) {
				query.setLong(0, companyId.longValue());
			}
			if (isSurNameRequired) {
				query.setString(1, "%" + surName.toLowerCase() + "%");
			}
			if (isForeNameRequired) {
				query.setString(2, "%" + foreName.toLowerCase() + "%");
			}

			list = query.list();
		} catch (HibernateException he) {
			log.error("Error while finding PPHQ by candidate surname and "
					+ "fore name :: More : ", he);
			throw new BupaDataAccessException("Error while searching for PPHQ "
					+ "by candidate surname and forename : More : ", he);
		}

		return list;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#findPPHQByReferringCompany(Long
	 *      companyId, Long hrUserId, Date fromDate, Date toDate, Long statusId)
	 */
	public List findPPHQByReferringCompany(Long companyId, Date fromDate,
			Date toDate, Long statusId) throws BupaDataAccessException {

		List list = new ArrayList();
		StringBuffer HQL_FIND_PPHQ_BY_REFFERING_COMPANY = new StringBuffer(
				"select {pphq.*} from pphq where CLIENT_ID = :companyId AND ")
				.append("UPDATED_DATE BETWEEN :fromDate AND :toDate ").append(
						" AND PPHQ_ID IN ( ").append(
						" select distinct PPHQ_ID from PPHQ_STATUS_AUDIT ");

		if (statusId != -1) {
			HQL_FIND_PPHQ_BY_REFFERING_COMPANY
					.append(" where PPHQ_STATUS_AUDIT.PPHQ_STATUS_ID  = :statusId ");
		}
		HQL_FIND_PPHQ_BY_REFFERING_COMPANY
				.append(" UNION ")
				.append(
						" SELECT distinct PPHQ_ID FROM PPHQREPORT WHERE PPHQREPORT.PPHQREPORT_ID  IN")
				.append(" (SELECT DISTINCT PPHQREPORT_ID FROM ").append(
						" REPORT_STATUS_AUDIT ");
		if (statusId != -1) {
			HQL_FIND_PPHQ_BY_REFFERING_COMPANY
					.append(" WHERE REPORT_STATUS_AUDIT.PPHQ_STATUS_ID = :statusId )) order by NVL(UPDATED_DATE , CREATED_DATE) DESC");
		} else {
			HQL_FIND_PPHQ_BY_REFFERING_COMPANY
					.append(")) order by NVL(UPDATED_DATE , CREATED_DATE )  DESC");
		}

		log.info(HQL_FIND_PPHQ_BY_REFFERING_COMPANY.toString());

		try {
			Session session = this.getHibernateSession();
			Query query = session.createSQLQuery(
					HQL_FIND_PPHQ_BY_REFFERING_COMPANY.toString()).addEntity(
					"pphq", PPHQDO.class);
			query.setLong("companyId", companyId.longValue());
			query.setDate("fromDate", fromDate);
			query.setDate("toDate", toDate);
			if (statusId != -1) {
				query.setLong("statusId", statusId.longValue());
			}
			log.info(query.getQueryString());
			list = query.list();

		} catch (HibernateException he) {
			log.error("Error while finding PPHQ by Referring Company :: "
					+ "More : ", he);
			throw new BupaDataAccessException("Error while findPP"
					+ "HQByReferringCompany : More : ", he);
		}

		return list;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#findEmploymentProblems()
	 */
	public List findEmploymentProblems() throws BupaDataAccessException {
		List results = null;
		try {
			Query query = this.getHibernateSession().createQuery(
					HQL_FIND_ALL_EMP_PROBLEMS);
			results = query.list();

		} catch (HibernateException he) {
			log.error("Error while finding all employment problems :: More : ",
					he);
			throw new BupaDataAccessException(
					"Error while loading Employment Problem : More : ", he);
		}
		return results;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#findHealthProblems()
	 */
	public List findHealthProblems() throws BupaDataAccessException {
		List results = null;
		try {
			Query query = this.getHibernateSession().createQuery(
					HQL_FIND_ALL_HEALTH_PROBLEMS);
			results = query.list();

		} catch (HibernateException he) {
			log.error("Error while finding all health problems :: More : ", he);
			throw new BupaDataAccessException(
					"Error while loading Health Problems : More : ", he);
		}
		return results;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#findJobHazzards()
	 */
	public List findJobHazzards() throws BupaDataAccessException {
		List results = null;
		try {
			Query query = this.getHibernateSession().createQuery(
					HQL_FIND_ALL_JOB_HAZZARDS);
			results = query.list();

		} catch (HibernateException he) {
			log.error("Error while all Job Hazzards :: More : ", he);
			throw new BupaDataAccessException(
					"Error while loading Job hazzards : More :" + he);
		}
		return results;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#findJobInvolvements()
	 */
	public List findJobInvolvements() throws BupaDataAccessException {
		List results = null;
		try {
			Query query = this.getHibernateSession().createQuery(
					HQL_FIND_ALL_JOB_INVOLVEMENTS);
			results = query.list();

		} catch (HibernateException he) {
			log
					.error(
							"Error while finding all Job Involvements :: More : ",
							he);
			throw new BupaDataAccessException(
					"Error while loading Job Involvements : More : ", he);
		}
		return results;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#findPendingPPHQs()
	 */
	public List findPendingPPHQs() throws BupaDataAccessException {
		List results = null;

		try {
			results = this.getHibernateSession().createQuery(
					HQL_FIND_PENDING_PPHQ).setString("statusPPHQCompleted",
					Constants.PPHQ_COMPLETED).setBoolean("closed", false)
					.list();
		} catch (HibernateException he) {
			throw new BupaDataAccessException(he);
		}

		return results;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#getPPHQCandidatesOfManager(Long
	 *      managerId)
	 */
	public List getPPHQCandidatesOfManager(Long managerId)
			throws BupaDataAccessException {
		List candidatesOfHr = new ArrayList();
		try {
			// UserProfileDO objUserProfileDO = new UserProfileDO();
			// objUserProfileDO.setId(managerId);
			Query query = this.getHibernateSession().createQuery(
					HQL_FIND_ALL_CANDIDATES);
			query.setParameter(0, managerId);
			candidatesOfHr = query.list();
		} catch (HibernateException he) {
			// log.error(he);
			throw new BupaDataAccessException(he);
		}
		return candidatesOfHr;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#getPPHQCandidate(Long
	 *      candidateId)
	 */

	public PPHQDO getPPHQCandidate(Long candidateId)
			throws BupaDataAccessException {
		PPHQDO objPPHQDO = new PPHQDO();
		try {
			CandidateDO objCandidate = new CandidateDO();
			objCandidate.setId(candidateId);

			Query query = this.getHibernateSession().createQuery(
					HQL_FIND_CANDIDATE);
			query.setParameter(0, objCandidate);
			List list = query.list();
			if (!list.isEmpty()) {
				objPPHQDO = (PPHQDO) list.get(0);
			}
		} catch (HibernateException he) {
			// log.error(he);
			throw new BupaDataAccessException(
					"Exception while getting the Candidate", he);
		}
		return objPPHQDO;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#deleteEmploymentHistory(Long
	 *      id)
	 */
	public void deleteEmploymentHistory(Long id) throws BupaDataAccessException {
		try {
			Session hSession = this.getHibernateSession();
			if (id != null) {
				EmploymentHistoryDO dObj = (EmploymentHistoryDO) hSession.load(
						EmploymentHistoryDO.class, id);
				hSession.delete(dObj);
			}
		} catch (HibernateException he) {
			log
					.error(
							"Error while deleting Employment Histtory :: More : ",
							he);
			throw new BupaDataAccessException(
					"Error while delete Employment History : More : ", he);
		}

	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#deleteHealthDetails(Long
	 *      id)
	 */
	public void deleteHealthDetails(Long id) throws BupaDataAccessException {
		try {
			Session hSession = this.getHibernateSession();
			if (id != null) {
				HealthDetailsDO dObj = (HealthDetailsDO) hSession.load(
						HealthDetailsDO.class, id);
				hSession.delete(dObj);
			}
		} catch (HibernateException he) {
			log.error("Error while deleting Health Details :: More : ", he);
			throw new BupaDataAccessException(
					"Error while delete Health Details : More : ", he);
		}

	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#findPPHQStatusByText(String
	 *      statusText)
	 */
	public PPHQStatusDO findPPHQStatusByText(String statusText)
			throws BupaDataAccessException {
		PPHQStatusDO statusDO = null;
		try {
			Session hSession = this.getHibernateSession();
			Query query = hSession.createQuery(HQL_FIND_STATUS_BY_TEXT);
			query.setString(0, statusText);
			statusDO = (PPHQStatusDO) query.uniqueResult();
		} catch (HibernateException he) {
			log.error("Error while finding PPHQ status by type :: More : ", he);
			throw new BupaDataAccessException(
					"Error while Find PPHQStatus by type : More : ", he);
		}
		return statusDO;
	}

	/**
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#lockPPHQ(java.lang.Long)
	 */
	public PPHQDO lockPPHQ(Long pphqID) throws BupaDataAccessException {
		PPHQDO pphqObj = null;

		try {
			pphqObj = (PPHQDO) this.getHibernateSession().load(PPHQDO.class,
					pphqID);
			pphqObj.setLocked(Boolean.TRUE);
		} catch (HibernateException he) {
			throw new BupaDataAccessException(he);
		}

		return (pphqObj);
	}

	/**
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#unlockPPHQ(java.lang.Long)
	 */
	public PPHQDO unlockPPHQ(Long pphqID) throws BupaDataAccessException {
		PPHQDO pphqObj = null;

		try {
			pphqObj = (PPHQDO) this.getHibernateSession().load(PPHQDO.class,
					pphqID);
			pphqObj.setLocked(Boolean.FALSE);
		} catch (HibernateException he) {
			throw new BupaDataAccessException(he);
		}

		return (pphqObj);
	}

	/**
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#setLockPPHQ(java.lang.Long,
	 *      java.lang.Boolean)
	 */
	public PPHQDO setLockPPHQ(Long pphqID, Boolean locked)
			throws BupaDataAccessException {
		PPHQDO pphqObj = null;

		try {
			pphqObj = (PPHQDO) this.getHibernateSession().load(PPHQDO.class,
					pphqID);
			pphqObj.setLocked(locked);
		} catch (HibernateException he) {
			throw new BupaDataAccessException(he);
		}

		return (pphqObj);
	}

	/**
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#setLockPPHQs(java.lang.Long[],
	 *      java.lang.Boolean)
	 */
	public void setLockPPHQs(Long[] pphqIDs, Boolean locked)
			throws BupaDataAccessException {
		try {
			for (int i = 0; i < pphqIDs.length; i++) {
				setLockPPHQ(pphqIDs[i], locked);
			}
		} catch (HibernateException he) {
			throw new BupaDataAccessException(he);
		}
	}

	/**
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#isPPHQLocked(java.lang.Long)
	 */
	public Boolean isPPHQLocked(Long pphqID) throws BupaDataAccessException {
		PPHQDO pphqObj = null;
		boolean locked = false;

		try {
			pphqObj = (PPHQDO) this.getHibernateSession().load(PPHQDO.class,
					pphqID);
			locked = pphqObj.getLocked().booleanValue()
					|| pphqObj.getClosed().booleanValue();

			return (new Boolean(locked));
		} catch (HibernateException he) {
			throw new BupaDataAccessException(he);
		}
	}

	/**
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#findHealthProblemById(Long
	 *      id)
	 */
	public HealthProblemsDO findHealthProblemById(Long id)
			throws BupaDataAccessException {
		HealthProblemsDO hProblem = null;

		try {
			hProblem = (HealthProblemsDO) this.getHibernateSession().load(
					HealthProblemsDO.class, id);
		} catch (HibernateException he) {
			throw new BupaDataAccessException(he);
		}

		return (hProblem);
	}

	/**
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#findPPHQByCandidateNameAndSurname(java.lang.String,
	 *      java.lang.String)
	 */
	public List findPPHQByCandidateNameAndSurname(String forename,
			String surname) throws BupaDataAccessException {
		List results = null;

		try {
			results = this.getHibernateSession().createQuery(
					HQL_FIND_PPHQ_BY_CANDIDATE_SURNAME_FORENAME2)

			.setString("forename", forename.toLowerCase() + "%").setString(
					"surname", surname.toLowerCase() + "%").list();
		} catch (HibernateException he) {
			throw new BupaDataAccessException(he);
		}

		return results;
	}

	/**
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#findPPHQByCompanyAndDate(java.lang.Long,
	 *      java.lang.String, java.lang.Boolean java.util.Date, java.util.Date)
	 */
	public List findPPHQByCompanyStatusAndDate(Long companyId, String status,
			Boolean pphqStatus, Date dateFrom, Date dateTo)
			throws BupaDataAccessException {

		List results = null;

		try {
			String queryStr = HQL_FIND_PPHQ_BY_COMPANY_REPORTSTATUS_AND_DATE;
			if (pphqStatus.booleanValue()) {
				queryStr = HQL_FIND_PPHQ_BY_COMPANY_PPHQSTATUS_AND_DATE;
			}

			results = this.getHibernateSession().createQuery(queryStr).setLong(
					"companyId", companyId.longValue()).setString("status",
					status).setDate("statusDateFrom", dateFrom).setDate(
					"statusDateTo", dateTo).list();
		} catch (HibernateException he) {
			throw new BupaDataAccessException(he);
		}

		return results;
	}

	/**
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#findPPHQByCompanyAndCandidate(java.lang.Long,
	 *      java.lang.String, java.lang.String)
	 */
	public List findPPHQByCompanyAndCandidate(Long companyId, String forename,
			String surname) throws BupaDataAccessException {
		List results = null;

		try {
			results = this.getHibernateSession().createQuery(
					HQL_FIND_PPHQ_BY_COMPANY_AND_CANDIDATE_OLD).setLong(
					"companyId", companyId.longValue()).setString("forename",
					forename + "%").setString("surname", surname + "%").list();
		} catch (HibernateException he) {
			throw new BupaDataAccessException(he);
		}

		return results;
	}

	/**
	 * Returns the Total No of PPHQ's present for a perticular company.
	 * 
	 * @param companyId
	 * @return
	 * @throws BupaDataAccessException
	 */
	public int getTotalPPHQPresent(Long companyId)
			throws BupaDataAccessException {
		int totalPPHQ = 0;
		try {
			Query query = this.getHibernateSession().createQuery(
					HQL_GET_TOTAL_PPHQ);
			query.setParameter("companyId", companyId);
			totalPPHQ = query.list().size();
		} catch (HibernateException e) {
			throw new BupaDataAccessException(e.getMessage(), e);
		}
		return totalPPHQ;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#findPPHQByCompanyAndCandidate(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public List findPPHQByCompanyAndCandidate(String companyName,
			String forename, String surname) throws BupaDataAccessException {
		List results = null;

		try {
			results = this.getHibernateSession().createQuery(
					HQL_FIND_PPHQ_BY_COMPANY_AND_CANDIDATE).setString(
					"companyName", companyName).setString("forename",
					forename + "%").setString("surname", surname + "%").list();
		} catch (HibernateException he) {
			throw new BupaDataAccessException(he);
		}

		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#findPPHQByCompanyStatusAndDate(java.lang.String,
	 *      java.lang.String, java.lang.Boolean, java.util.Date, java.util.Date)
	 */
	public List findPPHQByCompanyStatusAndDate(String companyName,
			String status, Boolean pphqStatus, Date dateFrom, Date dateTo)
			throws BupaDataAccessException {

		List results = null;

		try {
			String queryStr = HQL_FIND_PPHQ_BY_COMPANY_REPORTSTATUS_AND_DATE_NEW;
			if (pphqStatus.booleanValue()) {
				queryStr = HQL_FIND_PPHQ_BY_COMPANY_PPHQSTATUS_AND_DATE_NEW;
			}

			results = this.getHibernateSession().createQuery(queryStr)
					.setString("companyName", companyName).setString("status",
							status).setDate("statusDateFrom", dateFrom)
					.setDate("statusDateTo", dateTo).list();
		} catch (HibernateException he) {
			throw new BupaDataAccessException(he);
		}
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#findImmunizationQuestions()
	 */
	public List findImmunizationQuestions() throws BupaDataAccessException {
		List results = null;
		try {
			Query query = this.getHibernateSession().createQuery(
					HQL_FIND_ALL_IMMUNISATIONS);
			results = query.list();

		} catch (HibernateException he) {
			log.error("Error while finding all Immunization Questions:: ", he);
			throw new BupaDataAccessException(
					"Error while loading Immunization Questions - ", he);
		}
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#findFoodHandlingQuestions()
	 */
	public List findFoodHandlingQuestions() throws BupaDataAccessException {
		List results = null;
		try {
			Query query = this.getHibernateSession().createQuery(
					HQL_FIND_ALL_FOOD_HANDLING);
			results = query.list();

		} catch (HibernateException he) {
			log.error("Error while finding all Food Handling Questions : ", he);
			throw new BupaDataAccessException(
					"Error while loading Food Handling Questions : ", he);
		}
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#findNightworkingQuestions()
	 */
	public List findNightworkingQuestions() throws BupaDataAccessException {
		List results = null;
		try {
			Query query = this.getHibernateSession().createQuery(
					HQL_FIND_ALL_NIGHT_WORKING);
			results = query.list();

		} catch (HibernateException he) {
			log.error("Error while finding all  Nightworking Questions : ", he);
			throw new BupaDataAccessException(
					"Error while loading Nightworking Questions : ", he);
		}
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#createCandidatePPHQ(com.bupa.app.wellness.appservice.dataobject.pphq.PPHQDO)
	 */
	public PPHQDO createCandidatePPHQ(PPHQDO pphqDO)
			throws BupaDataAccessException {
		return null;
	}

	/**
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#deleteAllAbsence(java.util.Set)
	 */
	public void deleteAllAbsence(Set dbObjs) throws BupaDataAccessException {
		Iterator i = dbObjs.iterator();
		while (i.hasNext()) {
			AbsenceDetailsDO absenceDO = (AbsenceDetailsDO) i.next();
			if (absenceDO.getId() != null) {
				this.getHibernateSession().delete(absenceDO);
			}
		}

	}

	/**
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#deleteAllEmploymentHistory(java.util.Set)
	 */
	public void deleteAllEmploymentHistory(Set dbObjs)
			throws BupaDataAccessException {
		Iterator i = dbObjs.iterator();
		while (i.hasNext()) {
			EmploymentHistoryDO historyDO = (EmploymentHistoryDO) i.next();
			if (historyDO.getId() != null) {
				this.getHibernateSession().delete(historyDO);
			}
		}
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#findHRSuperUser(java.lang.Long)
	 */
	public Long findHRSuperUser(Long companyId) throws BupaDataAccessException {
		List results = null;
		Long hrSuperUserId = null;
		try {
			results = this.getHibernateSession().createQuery(
					HQL_FIND_HR_SUPER_USER).setLong("companyId", companyId)
					.setBoolean("issuperuser", Boolean.TRUE).list();
			if (results != null && !results.isEmpty()) {
				hrSuperUserId = (Long) results.get(0);
			}

		} catch (HibernateException he) {
			log
					.error(
							"Exception while finding HR Super User for given companyId :: More :",
							he);
			throw new BupaDataAccessException(
					"Exception while findHRSuperUser : More :", he);
		}
		return hrSuperUserId;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#findPPHQByReferringHrSuperUser(java.lang.Long,java.util.Date,java.util.Date,java.lang.Long,java.lang.Long)
	 */
	public List findPPHQByReferringHrSuperUser(Long hrUserId, Date fromDate,
			Date toDate, Long statusId, Long companyId)
			throws BupaDataAccessException {
		List list = new ArrayList();

		StringBuffer HQL_FIND_PPHQ_BY_REFFERING_HR_SUPER_USER = new StringBuffer(
				"select {pphq.*} from pphq where OFF_LINE = :offline AND CLIENT_ID = :clientId ")
				.append("AND REFERRING_MANAGER_ID != :hrUserId AND ").append(
						"UPDATED_DATE BETWEEN :fromDate AND :toDate ").append(
						" AND PPHQ_ID IN ( ").append(
						" select distinct PPHQ_ID from PPHQ_STATUS_AUDIT ");

		if (statusId != -1) {
			HQL_FIND_PPHQ_BY_REFFERING_HR_SUPER_USER
					.append(" where PPHQ_STATUS_AUDIT.PPHQ_STATUS_ID  = :statusId ");
		}
		HQL_FIND_PPHQ_BY_REFFERING_HR_SUPER_USER
				.append(" UNION ")
				.append(
						" SELECT distinct PPHQ_ID FROM PPHQREPORT WHERE PPHQREPORT.PPHQREPORT_ID  IN")
				.append(" (SELECT DISTINCT PPHQREPORT_ID FROM ").append(
						" REPORT_STATUS_AUDIT ");
		if (statusId != -1) {
			HQL_FIND_PPHQ_BY_REFFERING_HR_SUPER_USER
					.append(" WHERE REPORT_STATUS_AUDIT.PPHQ_STATUS_ID = :statusId )) order by NVL(UPDATED_DATE , CREATED_DATE )  DESC");
		} else {
			HQL_FIND_PPHQ_BY_REFFERING_HR_SUPER_USER
					.append(")) order by NVL(UPDATED_DATE , CREATED_DATE )  DESC");
		}

		log.info(HQL_FIND_PPHQ_BY_REFFERING_HR_SUPER_USER.toString());

		try {
			Session session = this.getHibernateSession();
			Query query = session.createSQLQuery(
					HQL_FIND_PPHQ_BY_REFFERING_HR_SUPER_USER.toString())
					.addEntity("pphq", PPHQDO.class);
			query.setBoolean("offline", Boolean.TRUE);
			query.setLong("clientId", companyId);
			query.setLong("hrUserId", hrUserId.longValue());
			query.setDate("fromDate", fromDate);
			query.setDate("toDate", toDate);
			if (statusId != -1) {
				query.setLong("statusId", statusId.longValue());
			}

			log.info(query.getQueryString());
			list = query.list();
		} catch (HibernateException he) {
			log.error("Error while finding PPHQ by Referring HR Super User :: "
					+ "More : ", he);
			throw new BupaDataAccessException("Error while "
					+ "findPPHQByReferringHrSuperUser : More : ", he);
		}

		return list;
	}

	public List findPPHQByReferringUser(Long hrUserId, Date fromDate,
			Date toDate, Long statusId) throws BupaDataAccessException {
		List list = new ArrayList();

		StringBuffer HQL_FIND_PPHQ_BY_REFFERING_USER = new StringBuffer(
				"select {pphq.*} from pphq where REFERRING_MANAGER_ID = :hrUserId AND ")
				.append("UPDATED_DATE BETWEEN :fromDate AND :toDate ").append(
						" AND PPHQ_ID IN ( ").append(
						" select distinct PPHQ_ID from PPHQ_STATUS_AUDIT ");

		if (statusId != -1) {
			HQL_FIND_PPHQ_BY_REFFERING_USER
					.append(" where PPHQ_STATUS_AUDIT.PPHQ_STATUS_ID  = :statusId ");
		}
		HQL_FIND_PPHQ_BY_REFFERING_USER
				.append(" UNION ")
				.append(
						" SELECT distinct PPHQ_ID FROM PPHQREPORT WHERE PPHQREPORT.PPHQREPORT_ID  IN")
				.append(" (SELECT DISTINCT PPHQREPORT_ID FROM ").append(
						" REPORT_STATUS_AUDIT ");
		if (statusId != -1) {
			HQL_FIND_PPHQ_BY_REFFERING_USER
					.append(" WHERE REPORT_STATUS_AUDIT.PPHQ_STATUS_ID = :statusId )) order by NVL(UPDATED_DATE , CREATED_DATE )  DESC");
		} else {
			HQL_FIND_PPHQ_BY_REFFERING_USER

			// <--PPHQ V4-1 Release -Start HPOV 250579

					.append(")) order by NVL(UPDATED_DATE , CREATED_DATE )  DESC");

			//		.append(")) AND rownum < 36 order by NVL(UPDATED_DATE , CREATED_DATE )  DESC");
			// <--PPHQ V4-1 Release -END HPOV 250579
		}

		log.info(HQL_FIND_PPHQ_BY_REFFERING_USER.toString());

		try {
			Session session = this.getHibernateSession();
			Query query = session.createSQLQuery(
					HQL_FIND_PPHQ_BY_REFFERING_USER.toString()).addEntity(
					"pphq", PPHQDO.class);
			query.setLong("hrUserId", hrUserId.longValue());
			query.setDate("fromDate", fromDate);
			query.setDate("toDate", toDate);
			if (statusId != -1) {
				query.setLong("statusId", statusId.longValue());
			}

			log.info(query.getQueryString());
			list = query.list();
		} catch (HibernateException he) {
			log.error("Error while finding PPHQ by Referring Company :: "
					+ "More : ", he);
			throw new BupaDataAccessException("Error while findPP"
					+ "HQByReferringCompany : More : ", he);
		}

		return list;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#findPendingPPHQs()
	 */
	public List findPendingPPHQList(Set companies)
			throws BupaDataAccessException {
		List results = null;

		try {

			Criteria criteria = getHibernateSession().createCriteria(
					PPHQDO.class);
			criteria.createAlias("pphqStatusAudit", "audit");
			criteria.createAlias("audit.pphqStatus", "status");
			criteria.add(Expression.or(Expression.eq("status.type",
					Constants.PPHQ_COMPLETED), Expression.and(Expression.eq(
					"status.type", Constants.Bupa_RECEIVED), Expression.eq(
					"offLine", Boolean.TRUE))));
			criteria.add(Expression.eq("closed", false));
			criteria.addOrder(Order.desc("id"));
			criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			if (companies != null && !companies.isEmpty()) {
				criteria.add(Expression.in("pphqClient", companies));
			}
			results = criteria.list();
		} catch (HibernateException he) {
			throw new BupaDataAccessException(he);
		}

		return results;
	}

	// PPHQ Rel.4 Code added on 26-March-2008 : Start for
	// 2.Intranet Logon
	/**
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#findHubPendingPPHQList()
	 */
	public List findHubPendingPPHQList() throws BupaDataAccessException {
		List results = null;

		try {

			Criteria criteria = getHibernateSession().createCriteria(
					PPHQDO.class);
			criteria.createAlias("pphqClient", "client");
			criteria.createAlias("pphqStatusAudit", "audit");
			criteria.createAlias("audit.pphqStatus", "status");
			criteria.add(Expression.or(Expression.eq("status.type",
					Constants.PPHQ_COMPLETED), Expression.and(Expression.eq(
					"status.type", Constants.Bupa_RECEIVED), Expression.eq(
					"offLine", Boolean.TRUE))));
			criteria.add(Expression.eq("closed", false));
			criteria.add(Expression
					.eq("client.referEmail", Constants.HUB_EMAIL));
			criteria.addOrder(Order.desc("id"));
			criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			results = criteria.list();
		} catch (HibernateException he) {
			log.error("Error while finding pphqs :: More : ", he);
			throw new BupaDataAccessException(
					"Error while loading pphqs : More : ", he);
		}

		return results;
	}

	// PPHQ Rel.4 Code added on 26-March-2008 : End

	/**
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#findPPHQBySubmittedUser(java.lang.String)
	 */
	public List findPPHQBySubmittedUser(String userName)
			throws BupaDataAccessException {
		List results = null;

		try {

			Criteria criteria = getHibernateSession().createCriteria(
					PPHQDO.class);
			criteria.createAlias("referringManager", "manager");
			criteria.add(Expression.eq("manager.userName", userName));
			criteria.addOrder(Order.desc("createdDate"));

			results = criteria.list();
		} catch (HibernateException he) {
			throw new BupaDataAccessException(he);
		}

		return results;
	}

	// PPHQ Rel.4 Code added on<11/03/2008> : Start For
	// <4.4><Customise PPHQ: Short PPHQ>
	/**
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#findShortPPHQ()
	 */
	public List findShortPPHQ() throws BupaDataAccessException {
		List results = null;
		try {
			Query query = this.getHibernateSession().createQuery(
					HQL_FIND_ALL_SHORT_PPHQ);
			results = query.list();

		} catch (HibernateException he) {
			log.error("Error while finding all short pphq :: More : ", he);
			throw new BupaDataAccessException(
					"Error while loading short pphq : More : ", he);
		}
		return results;
	}

	// PPHQ Rel.4 Code added on<11/03/2008> : End

	// PPHQ Rel.4 Code added on<15/04/2008> : Start For <18><Edit-Candidate
	// PPHQ>
	/**
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#getCandidatePPHQSummary()
	 */
	public PPHQDO getCandidatePPHQSummary(Long pphqId, Long candidateId)
			throws BupaDataAccessException {

		PPHQDO objPPHQDO = new PPHQDO();
		try {
			Query query = this.getHibernateSession().createQuery(
					HQL_FIND_CANDIDATE_DETAILS);
			query.setParameter("pphqID", pphqId);
			objPPHQDO = (PPHQDO) query.uniqueResult();
		} catch (HibernateException he) {
			log.error("Error while finding candidate :: More : ", he);
			throw new BupaDataAccessException(
					"Error while loading candidate : More : ", he);
		}
		return objPPHQDO;

	}

	// PPHQ Rel.4 Code added on<15/04/2008> : End

	// PPHQ Rel.4 Code added on<21/04/2008> : Start For <5><Review Candidate
	// Answer - Send Standard PPHQ>
	/**
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#getPPHQCandidate(Long
	 *      candidateId)
	 */

	public PPHQDO getPPHQCandidate(Long candidateId, String status)
			throws BupaDataAccessException {
		PPHQDO objPPHQDO = new PPHQDO();
		try {
			CandidateDO objCandidate = new CandidateDO();
			objCandidate.setId(candidateId);

			Query query = this.getHibernateSession().createQuery(
					HQL_FIND_CANDIDATE_AND_STATUS);
			query.setParameter(0, objCandidate);
			query.setParameter(1, status);
			List list = query.list();
			if (!list.isEmpty()) {
				objPPHQDO = (PPHQDO) list.get(0);
			}

			query = this.getHibernateSession().createQuery(HQL_FIND_CANDIDATE);
			query.setParameter(0, objCandidate);
			List newList = query.list();
			if (newList.size() > 1) {
				newList.remove(objPPHQDO);
			}
			objPPHQDO = (PPHQDO) newList.get(0);
		} catch (HibernateException he) {
			log
					.error(
							"Error while finding candidate based on status of candidate:: More : ",
							he);
			throw new BupaDataAccessException(
					"Exception while getting the Candidate", he);
		}
		return objPPHQDO;
	}

	// PPHQ Rel.4 Code added on<21/04/2008> : End

	// PPHQ Rel.4 Code added on<25/04/2008> : Start For <9><Search For a PPHQ>

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#findInterimReportByPPHQId(Long
	 *      pphqId,Long interimPPHQReportID,boolean interim)
	 */
	public List findInterimReportByPPHQId(Long pphqID, Long interimPPHQReportID)
			throws BupaDataAccessException {
		List list = null;
		try {
			Criteria cr = this.getHibernateSession().createCriteria(
					PPHQReportDO.class);
			cr.createAlias("pphq", "pphqId");
			cr.add(Expression.eq("pphqId.id", pphqID));
			cr.add(Expression.eq("interim", true));
			if (interimPPHQReportID != null) {
				cr.add(Expression.eq("id", interimPPHQReportID));
			}
			list = cr.list();

		} catch (HibernateException he) {
			log.error("Error while finding all Interim Report:: More : ", he);
			throw new BupaDataAccessException(
					"Error while loading Interim Report : More : ", he);
		}
		return list;
	}

	// PPHQ Rel.4 Code added on<25/04/2008> : End

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.bupa.app.wellness.appservice.dao.pphq.PPHQDAO#findPPHQReportById(Long
	 *      pphqId)
	 */
	public PPHQReportDO findPPHQReportById(Long pphqID)
			throws BupaDataAccessException {
		PPHQReportDO reportDO = null;
		try {
			Criteria cr = this.getHibernateSession().createCriteria(
					PPHQReportDO.class);
			cr.createAlias("pphq", "pphqId");
			cr.add(Expression.eq("pphqId.id", pphqID));
			cr.add(Expression.eq("interim", false));
			if (cr.list() != null && cr.list().size() > 0) {
				reportDO = (PPHQReportDO) cr.list().get(0);
			}

		} catch (HibernateException he) {
			log.error("Error while finding all PPHQ Report:: More : ", he);
			throw new BupaDataAccessException(
					"Error while loading findPPHQReportById : More : ", he);
		}
		return reportDO;
	}
}
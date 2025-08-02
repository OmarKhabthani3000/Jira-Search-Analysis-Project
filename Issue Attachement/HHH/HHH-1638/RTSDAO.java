/**
 *
 * CLASS NAME: RTSDAO
 *
 * COPYRIGHT NOTICE:
 *     @author Copyright Logistics.com 2003
 *     Subject to use, access and disclosure restrictions.
 */

package com.logistics.so.rts.dao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import javax.ejb.CreateException;
import javax.ejb.EJBException;

import org.hibernate.StaleObjectStateException;

import com.logistics.basedata.BaseDataDef;
import com.logistics.basedata.admin.ucl.UCL;
import com.logistics.basedata.ejb.BaseDataHomeHolder;
import com.logistics.basedata.ejb.basedatalookupmgr.BaseDataLookupMgr;
import com.logistics.basedata.ejb.parametermanager.ParameterManager;
import com.logistics.basedata.errorlog.ErrorLogsDAO;
import com.logistics.basedata.exception.BaseDataEngineException;
import com.logistics.basedata.filter.BasedataFilterUtility;
import com.logistics.basedata.util.Source;
import com.logistics.basedata.util.SourceDesc;
import com.logistics.basedata.util.finitevalue.CreationType;
import com.logistics.basedata.util.finitevalue.PartnerShipper;
import com.logistics.basedata.util.finitevalue.SourceType;
import com.logistics.javalib.admin.ucl.UCLAbstractUtil;
import com.logistics.javalib.admin.ucl.UCLUtilException;
import com.logistics.javalib.domain.id.ShipperId;
import com.logistics.javalib.domain.pk.ShipperPK;
import com.logistics.javalib.filter.FilterData;
import com.logistics.javalib.filter.FilterSQLBuilder;
import com.logistics.javalib.jsputil.PageToPageParms;
import com.logistics.javalib.jsputil.SortingCriterion;
import com.logistics.javalib.persistence.PersistenceException;
import com.logistics.javalib.persistence.PersistenceReader;
import com.logistics.javalib.persistence.jdbc.JDBCFunc;
import com.logistics.javalib.persistence.jdbc.JDBCPersistenceWriter;
import com.logistics.javalib.persistence.jdbc.SQLQuery;
import com.logistics.javalib.persistence.jdbc.SequenceGenerator;
import com.logistics.javalib.persistence.layer.PersistLayerException;
import com.logistics.javalib.persistence.layer.Query;
import com.logistics.javalib.persistence.layer.Session;
import com.logistics.javalib.persistence.layer.Type;
import com.logistics.javalib.util.*;
import com.logistics.javalib.validation.ErrorMessageMap;
import com.logistics.javalib.validation.ErrorType;
import com.logistics.javalib.validation.HardAndSoftCheckValidationBean;
import com.logistics.so.SOSDef;
import com.logistics.so.admin.parameter.SOSParameterType;
import com.logistics.so.asn.RoutingRequest;
import com.logistics.so.asn.RoutingRequestDetail;
import com.logistics.so.core.domain.SOSValidationErrorAccess;
import com.logistics.so.core.domain.finitevalue.OrderStatus;
import com.logistics.so.core.domain.finitevalue.RTSLineStatus;
import com.logistics.so.core.domain.finitevalue.RTSStatus;
import com.logistics.so.core.domain.id.OrderId;
import com.logistics.so.ejb.SOSHomeHolder;
import com.logistics.so.ejb.commmgr.SOSCommManager;
import com.logistics.so.ejb.ordermanager.SOSOrderManagerSB;
import com.logistics.so.order.AbstractOrder;
import com.logistics.so.order.OrderPK;
import com.logistics.so.order.SOSOrderJDBCAccessor;
import com.logistics.so.order.ordersubobj.AbstractOrderLineItem;
import com.logistics.so.order.ordersubobj.SOSOrderLineItem;
import com.logistics.so.order.ordersubobj.SOSOrderLineItemJDBCAccessor;
import com.logistics.so.permission.SOSPermissionsCodeList;
import com.logistics.so.rts.bo.RTS;
import com.logistics.so.rts.bo.RTSInterceptor;
import com.logistics.so.rts.bo.RTSLineItem;
import com.logistics.so.rts.bo.RTSLineItemPK;
import com.logistics.so.rts.bo.RTSPK;
import com.logistics.so.rts.bo.RTSSize;
import com.logistics.so.rts.bo.RTSSizePK;
import com.logistics.so.rts.bo.RTSStatusHelper;
import com.logistics.so.rts.helper.RTSCCLHelper;
import com.logistics.so.rts.helper.RTSCodeList;
import com.logistics.so.util.SOSDebugLog;
import com.logistics.so.util.SOSException;

/**
 * Provides data access support (CRUD) for Rts.
 */

public class RTSDAO {
	private static int NULL = 0;

	public ArrayList findAll(int tcCompanyId, SortingCriterion sc,
			PageToPageParms p2p) throws PersistLayerException {
		return findAll(tcCompanyId, sc, p2p, null);
	}

	public ArrayList findAll(int tcCompanyId, SortingCriterion sc,
			PageToPageParms p2p, FilterData rtsListFilterData)
			throws PersistLayerException {
		SOSDebugLog.DEBUG_LOG
				.logLow("executing the findAll method() in RtsDAO");
		Session session = SOSDef.openSession(new RTSInterceptor());
		ArrayList all = null;
		String filter = "";
		String where = "where rts.tcCompanyId =" + tcCompanyId;
		try {
			String orderBy = "";
			if (sc != null) {
				orderBy += " order by " + sc.createOrderByClause();
			}
			String filterWhereOverriddenClause = BasedataFilterUtility
					.getOverriddenWhereClauseForSOFilter(rtsListFilterData);
			String filterWhere = FilterSQLBuilder.SQL_BUILDER.buildWhereClause(
					rtsListFilterData, tcCompanyId, "",
					FilterSQLBuilder.USE_HIBERNATE_BUILDER).getWhereClause();
			if (Misc.isNullString(filterWhere) == false) {
				where += " and " + filterWhere;
			}
			if (Misc.isNullString(filterWhereOverriddenClause) == false) {
				where += " and " + filterWhereOverriddenClause;
			}

			int startIndex = p2p.getCurrentRow();
			int rowsToRead = p2p.getRowsPerPage();
			p2p.setTotalNumberOfRows(((Integer) session.iterate("select count(*) from RTS rts " + where).next()).intValue());
			Query rtsQuery = session.createQuery(" from RTS rts " + where + orderBy);

			rtsQuery.setFirstResult(startIndex);
			rtsQuery.setMaxResults(rowsToRead);
			List allRTSOnPage = rtsQuery.list();

			List list = allRTSOnPage;
			if (list == null) {
				all = new ArrayList(0);
			} else {
				all = new ArrayList(list);
			}
			// To Find Alerts and Warning for List of RTS object.
			// Commented.. No Need to Check for SoftCheck Error. (May used for future.)
//			try{
//				//all = ErrorLogsDAO.getErrorLog(all, tcCompanyId, "RTS", session);
//				RTS rts;
//				if(all != null && all.size() > 0) {
//					Iterator rtsItr = all.iterator();
//					while(rtsItr.hasNext()){
//					 rts = (RTS)rtsItr.next();
//					 ErrorLogsDAO.getErrorLog(rts , tcCompanyId, rts.getId().getRtsId(),"RTS", session);
//					}
//				}
//			}catch(Exception errorExp){
//			}
		} catch (PersistLayerException ex) {

			throw ex;
		} finally {
			if (session != null)
				session.close();
		}
		return all;
	}



	public ArrayList findAllByRtsIdsList(List rtsIDList, int tcCompanyId)
				throws PersistLayerException {

		SOSDebugLog.DEBUG_LOG
				.logLow("executing the findAllByRtsIdsList method() in RtsDAO");
		Session session = SOSDef.openSession(new RTSInterceptor());

		ArrayList all = new ArrayList();
		StringBuffer inClause = new StringBuffer(" r.id.rtsId in (");
		if (rtsIDList != null && rtsIDList.size() > 0)
		{
			Iterator iter = rtsIDList.iterator();
			while(iter.hasNext())
			{
				String str = (String)iter.next();
				if(iter.hasNext())
				{
					inClause.append(str + ",");
				}
				else
				{
					inClause.append(str);
				}
			}
			inClause.append(" )");
		}
		try
		{
			if (rtsIDList != null && rtsIDList.size() > 0)
			{
				Object[] vals = new Object[] { new Integer(tcCompanyId) };

				List list = session.find("from RTS r where " + inClause.toString()
					+  " and " + " r.tcCompanyId = ? ", vals,
					new Type[] { Type.INTEGER });

				if (list == null)
				{
					all = new ArrayList(0);
				}
				else
				{
					all = new ArrayList(list);
				}
			}
		}
		catch (PersistLayerException ex)
		{
			throw ex;
		}
		finally
		{
			if (session != null)
				session.close();
		}
		return all;
	}

	public ArrayList findAll(Vector tcCompanyIdList, List businessPtrList,
			SortingCriterion sc, PageToPageParms p2p,
			FilterData rtsListFilterData) throws PersistLayerException {
		SOSDebugLog.DEBUG_LOG
				.logLow("executing the findAll method() in RtsDAO");
		Session session = SOSDef.openSession(new RTSInterceptor());
		ArrayList all = null;
		String filter = "";
		String where = "";
		boolean check = true;

		if (tcCompanyIdList != null && tcCompanyIdList.size() > 0) {
			where = "where rts.tcCompanyId in(";
			Iterator itr = tcCompanyIdList.iterator();
			while (itr.hasNext()) {
				PartnerShipper tcCompanyId = (PartnerShipper) itr.next();
				if (check) {
					where = where + tcCompanyId.getCompanyId();
				} else {
					where = where + "," + tcCompanyId.getCompanyId();
				}
				check = false;
			}
			where = where + ")";
		}

		if (businessPtrList != null && businessPtrList.size() > 0) {
			check = true;
			where = where + " and rts.businessPartnerId in(";
			Iterator itr = businessPtrList.iterator();
			while (itr.hasNext()) {
				String bp = (String) itr.next();
				if (check) {
					where = where + " '" + bp + "'";
				} else {
					where = where + ", '" + bp + "'";
				}
				check = false;
			}
			where = where + ")";
		}

		try {
			String orderBy = "";
			if (sc != null) {
				orderBy += " order by " + sc.createOrderByClause();
			}

			if (rtsListFilterData != null) {
				filter = FilterSQLBuilder.SQL_BUILDER.buildWhereClause(
						rtsListFilterData, 0, "",
						FilterSQLBuilder.USE_HIBERNATE_BUILDER)
						.getWhereClause();

				if (!Misc.isNullTrimmedString(filter)) {
					where = where + " and " + filter;

				}
			}

			int startIndex = p2p.getCurrentRow();
			int rowsToRead = p2p.getRowsPerPage();
			p2p
					.setTotalNumberOfRows(((Integer) session.iterate(
							"select count(*) from RTS rts " + where).next())
							.intValue());
			Query rtsQuery = session.createQuery(" from RTS rts " + where
					+ orderBy);
			rtsQuery.setFirstResult(startIndex);
			rtsQuery.setMaxResults(rowsToRead);
			List allRTSOnPage = rtsQuery.list();

			List list = allRTSOnPage;
			if (list == null) {
				all = new ArrayList(0);
			} else {
				all = new ArrayList(list);
			}

		} catch (PersistLayerException ex) {

			throw ex;
		} finally {
			if (session != null)
				session.close();
		}
		return all;
	}

    private static String SELECT_CLAUSE = "select rts.id.rtsId, rts.version, rts.tcCompanyId, rts.rtsOFacilityIdInteger, rts.rtsDFacilityIdInteger, rts.rtsOFacilityAliasId, rts.rtsDFacilityAliasId, rts.isRtsOriginChecked, rts.isRtsDestinationChecked, rts.pickupStartDttm, rts.pickupEndDttm, rts.pickupTimeZone, rts.deliveryTimeZone, rts.deliveryDttmStart, rts.deliveryDttmEnd, rts.businessPartnerId, rts.totalSizeOnOrders, rts.additionalSize1Double, rts.additionalSize1UomIdInteger, rts.additionalSize2Double, rts.additionalSize2UomIdInteger, rts.quantityDouble, rts.quantityUomIdInteger, rts.rtsStatus, rts.isLocked, rts.rrcNumber, rts.isHazmat, rts.autoCreateMethod, rts.createdDttm, rts.createdSource, rts.modifiedDttm, rts.modifiedSource, rts.createdSourceType, rts.modifiedSourceType, rts.tsCrtlNumber";

    public ArrayList findAll(Vector tcCompanyIdList, List businessPtrList,
			SortingCriterion sc, PageToPageParms p2p,
			FilterData rtsListFilterData, String regionString) throws PersistLayerException {
		SOSDebugLog.DEBUG_LOG
				.logLow("executing the findAll method() in RtsDAO");
		Session session = SOSDef.openSession(new RTSInterceptor());
		ArrayList all = null;
		String filter = "";
		String where = "";
		boolean check = true;

		if (tcCompanyIdList != null && tcCompanyIdList.size() > 0) {
			where = "where rts.tcCompanyId in(";
			Iterator itr = tcCompanyIdList.iterator();
			while (itr.hasNext()) {
				PartnerShipper tcCompanyId = (PartnerShipper) itr.next();
				if (check) {
					where = where + tcCompanyId.getCompanyId();
				} else {
					where = where + "," + tcCompanyId.getCompanyId();
				}
				check = false;
			}
			where = where + ")";
		}

		if (businessPtrList != null && businessPtrList.size() > 0) {
			check = true;
			where = where + " and rts.businessPartnerId in(";
			Iterator itr = businessPtrList.iterator();
			while (itr.hasNext()) {
				String bp = (String) itr.next();
				if (check) {
					where = where + " '" + bp + "'";
				} else {
					where = where + ", '" + bp + "'";
				}
				check = false;
			}
			where = where + ")";
		}

		try {
			String orderBy = "";
			if (sc != null) {
				orderBy += " order by " + sc.createOrderByClause();
			}

			if (rtsListFilterData != null) {
				filter = FilterSQLBuilder.SQL_BUILDER.buildWhereClause(
						rtsListFilterData, 0, "",
						FilterSQLBuilder.USE_HIBERNATE_BUILDER)
						.getWhereClause();

				if (!Misc.isNullTrimmedString(filter)) {
					where = where + " and " + filter;

				}
			}
            if (regionString != null) where += (" and rli.regionIdInteger in "+regionString);
//            where += (" and rts.id = rli.id.rtsId ");
            int startIndex = p2p.getCurrentRow();
			int rowsToRead = p2p.getRowsPerPage();
			/*p2p
					.setTotalNumberOfRows(((Integer) session.iterate(
							"select count(*) from RTS rts, RTSLineItem rli " + where).next())
							.intValue());
			Query rtsQuery = session.createQuery("select rts.* from RTS rts, RTSLineItem rli " + where
					+ orderBy);*/
            p2p
					.setTotalNumberOfRows(((Integer) session.iterate(
							"select count(*) from RTS rts LEFT OUTER JOIN fetch rts.rtsLineItems as rli " + where).next())
							.intValue());
			Query rtsQuery = session.createQuery("select rts from RTS rts LEFT OUTER JOIN fetch rts.rtsLineItems as rli " + where
					+ orderBy);
            rtsQuery.setFirstResult(startIndex);
			rtsQuery.setMaxResults(rowsToRead);
			List allRTSOnPage = rtsQuery.list();

			List list = allRTSOnPage;
			if (list == null) {
				all = new ArrayList(0);
			} else {
				all = new ArrayList(list);
			}

		} catch (PersistLayerException ex) {

			throw ex;
		} finally {
			if (session != null)
				session.close();
		}
		return all;
	}

    public ArrayList findAll(String rtsName) throws PersistLayerException {

		SOSDebugLog.DEBUG_LOG
				.logLow("executing the findAll method() in RtsDAO");
		Session session = SOSDef.openSession(new RTSInterceptor());

		ArrayList all = null;

		try {

			Object[] vals = new Object[] { rtsName, };

			List list = session.find("from RTS r where" + " r.rtsName like ? ",
					vals, new Type[] { Type.STRING });

			if (list == null) {
				all = new ArrayList(0);
			} else {
				all = new ArrayList(list);
			}

		} catch (PersistLayerException ex) {
			/*
			 * if (transaction != null) { transaction.rollback(); }
			 */
			throw ex;
		} finally {
			if (session != null)
				session.close();
		}
		return all;
	}

	public ArrayList findAll(int tcCompanyId) throws PersistLayerException {

		SOSDebugLog.DEBUG_LOG
				.logLow("executing the findAll method() in RtsDAO");
		Session session = SOSDef.openSession(new RTSInterceptor());

		ArrayList all = null;

		try {

			Object[] vals = new Object[] { new Integer(tcCompanyId), };

			List list = session.find(
					"from RTS r where" + " r.tcCompanyId = ? ", vals,
					new Type[] { Type.INTEGER });

			if (list == null) {
				all = new ArrayList(0);
			} else {
				all = new ArrayList(list);
			}
		} catch (PersistLayerException ex) {
			throw ex;
		} finally {
			if (session != null)
				session.close();
		}
		return all;
	}

	public ArrayList findRTSLineItems(List orderIdList)
		throws PersistLayerException
		{
			return findRTSLineItems(orderIdList, null);
		}

	public ArrayList findRTSLineItems(List orderIdList, Connection conn)
			throws PersistLayerException {
		SOSDebugLog.DEBUG_LOG
				.logLow("executing the findAll(int orderId) in RtsDAO"
						+ this.getClass().getName());
		Session session = null;
		if(conn == null)
		{
			session = SOSDef.openSession(new RTSInterceptor());
		}
		else
		{
			session = SOSDef.openSession(conn, new RTSInterceptor());
		}
		ArrayList rtsLineItems = null;
		String orderIds = "";
		int orderIdCount = 0;
		if (orderIdList != null) {
			orderIdCount = orderIdList.size();
		}
		for (int i = 0; i < orderIdCount; i++) {
			orderIds += orderIdList.get(i)
					+ (((i) == (orderIdCount - 1)) ? "" : ",");
		}
		try {
			Query query = session
					.createQuery("from RTSLineItem rli where rli.orderId in ("
							+ orderIds
							+ ") order by rli.orderId,rli.lineItemId,rli.id.rtsLineItemId,rli.id.rtsId");
			List list = query.list();
			if (list == null)
			{
				rtsLineItems = new ArrayList(0);
			}
			else
			{
				rtsLineItems = new ArrayList(list);
			}
			Iterator rliIter = rtsLineItems.iterator();
			RTSSizeDAO rtsSizeDAO = new RTSSizeDAO();
			while(rliIter.hasNext())
			{
				RTSLineItem rtsLineItem = (RTSLineItem)rliIter.next();
				List lineItemSizelist = rtsSizeDAO.findAll(rtsLineItem.getId().getRtsId(),
															rtsLineItem.getId().getRtsLineItemId(), session);
				if (lineItemSizelist != null && lineItemSizelist.size() > 0)
				{
					Set set = new HashSet(lineItemSizelist);
					rtsLineItem.setRtsSizes(set);
				}
			}
		} catch (PersistLayerException ple) {
			throw ple;
		} finally {
			if (session != null)
				session.close();
		}
		return rtsLineItems;
	}

	public ArrayList findRTSLineItems(int orderId) throws PersistLayerException {
		SOSDebugLog.DEBUG_LOG
				.logLow("executing the findAll(int orderId) in RtsDAO"
						+ this.getClass().getName());
		Session session = SOSDef.openSession(new RTSInterceptor());
		ArrayList rtsLineItems = null;
		try {
			Query query = session
					.createQuery("from RTSLineItem rli where rli.orderId = "
							+ orderId
							+ " order by rli.orderId,rli.lineItemId,rli.id.rtsLineItemId");
			List list = query.list();
			if (list == null)
			{
				rtsLineItems = new ArrayList(0);
			}
			else
			{
				rtsLineItems = new ArrayList(list);
			}
			Iterator rliIter = rtsLineItems.iterator();
			RTSSizeDAO rtsSizeDAO = new RTSSizeDAO();
			while(rliIter.hasNext())
			{
				RTSLineItem rtsLineItem = (RTSLineItem)rliIter.next();
				List lineItemSizelist = rtsSizeDAO.findAll(rtsLineItem.getId().getRtsId(),
															rtsLineItem.getId().getRtsLineItemId(), session);
				if (lineItemSizelist != null && lineItemSizelist.size() > 0)
				{
					Set set = new HashSet(lineItemSizelist);
					rtsLineItem.setRtsSizes(set);
				}
			}
		} catch (PersistLayerException ple) {
			throw ple;
		} finally {
			if (session != null)
				session.close();
		}
		return rtsLineItems;
	}

	public ArrayList getRTSLineItemsForRTS(int rtsId) throws PersistLayerException
	{
		SOSDebugLog.DEBUG_LOG.logLow("executing the getRTSLineItemsForRTS(int orderId) in "
						+ this.getClass().getName());
		Session session = SOSDef.openSession(new RTSInterceptor());
		ArrayList rtsLineItems = null;
		try
		{
			Query query = session.createQuery("from RTSLineItem rli where rli.id.rtsId = "
							+ rtsId + " order by rli.id.rtsLineItemId");
			List list = query.list();
			if (list == null)
			{
				rtsLineItems = new ArrayList(0);
			}
			else
			{
				rtsLineItems = new ArrayList(list);
			}
		}
		catch (PersistLayerException ple)
		{
			throw ple;
		}
		finally
		{
			if (session != null)
			{
				session.close();
			}
		}
		return rtsLineItems;
	}

	public ArrayList findAll(int orderId, String lineItemIdString)
			throws PersistLayerException {
		SOSDebugLog.DEBUG_LOG
				.logLow("executing the findAll(int orderId,String lineItemIdString) in RtsDAO"
						+ this.getClass().getName());
		Session session = SOSDef.openSession(new RTSInterceptor());
		ArrayList rtsLineItems = null;
		try {
			Query query = session
					.createQuery("from RTSLineItem rli where rli.orderId = "
							+ orderId
							+ " and rli.lineItemId = '"
							+ lineItemIdString
							+ "' order by rli.orderId,rli.lineItemId,rli.id.rtsLineItemId");
			List list = query.list();
			if (list == null) {
				rtsLineItems = new ArrayList(0);
			} else {
				rtsLineItems = new ArrayList(list);
			}
		} catch (PersistLayerException ple) {
			throw ple;
		} finally {
			if (session != null)
				session.close();
		}
		return rtsLineItems;
	}

	public ArrayList findAll(String rtsName, int tcCompanyId)
			throws PersistLayerException {

		SOSDebugLog.DEBUG_LOG
				.logLow("executing the findAll method() in RtsDAO");
		Session session = SOSDef.openSession(new RTSInterceptor());

		ArrayList all = null;

		try {

			Object[] vals = new Object[] { rtsName, new Integer(tcCompanyId), };

			List list = session.find("from RTS r where"
					+ " r.rtsName like ? and " + " r.tcCompanyId = ? ", vals,
					new Type[] { Type.STRING, Type.INTEGER });

			if (list == null) {
				all = new ArrayList(0);
			} else {
				all = new ArrayList(list);
			}

		} catch (PersistLayerException ex) {

			throw ex;
		} finally {
			if (session != null)
				session.close();
		}
		return all;
	}

	public ArrayList findAllByTcRtsId(String rtsName, int tcCompanyId)
			throws PersistLayerException {

		SOSDebugLog.DEBUG_LOG
				.logLow("executing the findAllByTcRtsId method() in RtsDAO");
		Session session = SOSDef.openSession(new RTSInterceptor());

		ArrayList all = null;

		try {
			Object[] vals = new Object[] { rtsName, new Integer(tcCompanyId), };

			List list = session.find("from RTS r where"
					+ " r.id.rtsId like ? and " + " r.tcCompanyId = ? ", vals,
					new Type[] { Type.STRING, Type.INTEGER });

			if (list == null) {
				all = new ArrayList(0);
			} else {
				all = new ArrayList(list);
			}
		} catch (PersistLayerException ex) {
			throw ex;
		} finally {
			if (session != null)
				session.close();
		}
		return all;
	}

	public RTS save(RTS rts) throws PersistLayerException {
		SOSDebugLog.DEBUG_LOG.logDebug(SOSDebugLog.ORDER_CATEGORY,
				"executing the save method()");
		Session session = SOSDef.openSession(new RTSInterceptor());
		RTSPK providedPK = rts.getId();
		RTS rtsToSave = rts;
		int rtsId = 0;
		RTSPK rtsPK = null;
		SOSDebugLog.DEBUG_LOG
				.logLow(SOSDebugLog.ORDER_CATEGORY, rts.toString());
		try {
			if (providedPK == null || providedPK.isNull()) {
				// Construct the new key.
				rtsId = SequenceGenerator.next(session.connection(),
						RTSPK.SEQUENCE);
				rtsPK = new RTSPK(rtsId);
				rtsToSave.setId(rtsPK);
				if (Misc.isNullTrimmedString(rtsToSave.getTcRtsId())) {
					rtsToSave.setTcRtsId(String.valueOf(rtsId));
				}
				populateLineItemsWithRTSId(rtsToSave);
				session.save(rtsToSave);
				saveRTSSize(rtsToSave, session);
				session.flush();
				updateDependentObjects(rtsToSave, session.connection());
				sendOutboundRTSTxml(rtsToSave);
			} else // Update the object's key.
			{
				populateLineItemsWithRTSId(rtsToSave);
				RTS newRTS = (RTS) find(rtsToSave.getId());
				if (newRTS == null) {
					rtsToSave.addHardCheckError(RTSCodeList.HEC_RTS_NOT_FOUND,
							ErrorMessageMap.getErrorMessage(
									RTSCodeList.HEC_RTS_NOT_FOUND).add(
									rtsToSave.getId().getRtsId()));
				} else {
					if (rtsToSave.getVersion() != newRTS.getVersion()) {
						rtsToSave.addHardCheckError(
								RTSCodeList.HEC_RTS_SAVE_CONFLICT,
								ErrorMessageMap.getErrorMessage(
										RTSCodeList.HEC_RTS_SAVE_CONFLICT).add(
										rtsToSave.getId().getRtsId()));
					} else {
						Set deletedLines = rtsToSave.getDeletedRtsLines();
						if (deletedLines != null && deletedLines.size() > 0)
						{
							//Need to Delete child records ie) TO's associated with any RTS Line Item.
							deleteTOLineItemAttachedToRTS(rtsToSave,newRTS, session.connection());

							StringBuffer deleteLineQuery = new StringBuffer(
									"from RTSLineItem r where r.id.rtsId="
											+ rtsToSave.getId().getRtsId()
											+ " and r.id.rtsLineItemId in (");
							Iterator deletedLinesItr = deletedLines.iterator();
							int count = 0;
							while (deletedLinesItr.hasNext()) {
								Integer rtsLineItemId = (Integer) deletedLinesItr
										.next();
								if (count > 0)
									deleteLineQuery.append(",");
								count++;
								deleteLineQuery
										.append(rtsLineItemId.intValue());
							}
							deleteLineQuery.append(")");
							session.delete(deleteLineQuery.toString());
							session.flush();
							//FIX CR-16449. RTS header status is not going back to created when all the RTS Lines are deleted.
							Set lineItem = rtsToSave.getRtsLineItems();
							if(lineItem == null || lineItem.size() == 0 ) {
							rtsToSave.setRtsStatus(Short.parseShort(RTSStatus.CREATED.getCode()));
							}
						}

						session.update(rtsToSave);
						//session.flush();
						saveRTSSize(rtsToSave, session);
						session.flush();
						//updateDependentObjects(rtsToSave, session.connection());
						updateDependentObjects(newRTS, session.connection());
						sendOutboundRTSTxml(rtsToSave);
					}
				}
			}
			// Insert HCE and SoftCheck Error in Error_Log Table.
			SourceDesc sd =	new SourceDesc((SourceType)(SourceType.MANAGER.findByCode(rts.getCreatedSourceType())), new Source(rts.getCreatedSource()));
			SOSValidationErrorAccess.insertRTSErrors(sd,rtsToSave);
			rtsToSave.clearSoftCheckErrors();
			rtsToSave.clearSoftCheckErrorOverrides();

		} catch (PersistenceException ex) {
			throw new PersistLayerException(ex.getOriginalException());
		} catch (Throwable t) {
			PersistLayerException p = new PersistLayerException(t);
			if (p.isObjectNotFoundException()) {
				rtsToSave.addHardCheckError(RTSCodeList.HEC_RTS_NOT_FOUND,
						ErrorMessageMap.getErrorMessage(
								RTSCodeList.HEC_RTS_NOT_FOUND).add(
								rtsToSave.getId().getRtsId()));
			} else if (p.isStaleObjectException()) {
				rtsToSave.addHardCheckError(RTSCodeList.HEC_RTS_SAVE_CONFLICT,
						ErrorMessageMap.getErrorMessage(
								RTSCodeList.HEC_RTS_SAVE_CONFLICT).add(
								rtsToSave.getId().getRtsId()));
			} else {
				throw p;
			}
		} finally {
			if (session != null)
				session.close();
		}
		return rtsToSave;
	}

	/*
	 * Method 'deleteTOLineItemAttachedToRTS' Deletes TO's Attatched RTS Line Item.
	 */
	private void deleteTOLineItemAttachedToRTS(RTS rtsToSave,RTS rtsFromDB, Connection connection )
	{

		Set deletedLines = rtsToSave.getDeletedRtsLines();
        Set rtsLines = rtsFromDB.getRtsLineItems();
        Vector toLines = getTOLineItems(rtsToSave.getId().getRtsId(),new ArrayList(deletedLines));
        try
        {
            UserId userId =new UserId(rtsToSave.getModifiedSource());
            SourceDesc sourceDesc = new SourceDesc(userId);
            ArrayList deletedList = new ArrayList();
            Iterator deletedLinesItr = deletedLines.iterator();
            while(deletedLinesItr.hasNext())
            {
                int rtsLineItemId = ((Integer)deletedLinesItr.next()).intValue();
                Iterator rtsLinesItr=rtsLines.iterator();
                while(rtsLinesItr.hasNext())
                {
                    RTSLineItem rtsLineItem = (RTSLineItem)rtsLinesItr.next();
                    rtsLineItem.setLocale(rtsToSave.getLocale());//Set Locale in RTS Line so as to fetch when updating TO in RTSCCLHelper.removeAssociatedTOLinesWithoutRTSCCLTriggered()
                    if(rtsLineItem.getId().getRtsLineItemId()==rtsLineItemId)
                    {
                        deletedList.add(rtsLineItem);
                    }

                }

            }
            if(deletedList.size()>0)
            {
                RTSCCLHelper.removeAssociatedTOLinesWithoutRTSCCLTriggered(deletedList,connection,userId,new ShipperPK(new ShipperId(rtsToSave.getTcCompanyId())),sourceDesc,null);
            }
        }
        catch(Exception ex)
        {
            SOSDebugLog.DEBUG_LOG.logLow("IN RTS DAO :: Exception While Deleting TO Line Item -- > " + ex);
        }

		/*f(toLines != null && toLines.size() > 0 ){
			String deleteTOSizeQuery = 	"Delete from ORDER_LINE_ITEM_SIZE " +
										"where tc_company_id = ? and order_id in ( ? ";

			String deleteTOQuery = 	"Delete from Order_Line_Item " +
									"where tc_company_id = ? " +
									"and rts_id = ? and order_id in ( ? ";
			for(int i = 1; i < toLines.size(); i++ ) {
				deleteTOSizeQuery = deleteTOSizeQuery + ", ?";
				deleteTOQuery = deleteTOQuery + ", ?";
			}
			deleteTOSizeQuery = deleteTOSizeQuery + ")";
			deleteTOQuery = deleteTOQuery + ")";

			try {
				JDBCPersistenceWriter deleteSize = null;
				JDBCPersistenceWriter pw = null;

				SQLQuery deleteSizeSQL = new SQLQuery(deleteTOSizeQuery);
				SQLQuery updateSQL = new SQLQuery(deleteTOQuery);

				deleteSize = new JDBCPersistenceWriter(deleteSizeSQL, connection);
				pw = new JDBCPersistenceWriter(updateSQL, connection);

				pw.setNextInt(rtsToSave.getTcCompanyId());
				deleteSize.setNextInt(rtsToSave.getTcCompanyId());

				pw.setNextInt(rtsToSave.getId().getRtsId());

				Iterator it = toLines.iterator();
				while(it.hasNext()) {
					AbstractOrderLineItem  toLineItem = (SOSOrderLineItem)it.next();
					pw.setNextInt(Integer.parseInt(toLineItem.getOrderIdString()));
					deleteSize.setNextInt(Integer.parseInt(toLineItem.getOrderIdString()));
				}
				deleteSize.executeUpdate();
				pw.executeUpdate();

			}catch(Exception e)	{
				SOSDebugLog.DEBUG_LOG.logLow("IN RTS DAO :: Exception While Deleting TO Line Item -- > " + e);
			}
		}*/
	}

	private void saveRTSSize(RTS rtsToSave, Session session)
			throws PersistLayerException {
		if (session != null && rtsToSave != null) {
			if (rtsToSave.getRtsSizes() != null
					&& rtsToSave.getRtsSizes().size() > 0) { // >0
				RTSSizeDAO rtsSizeDAO = new RTSSizeDAO();
				Iterator rtsSizeItr = rtsToSave.getRtsSizes().iterator();
				while (rtsSizeItr.hasNext()) {
					RTSSize rtsSize = (RTSSize) rtsSizeItr.next();
					if (rtsSize.getId().getRtsSizeId() == NullDef.NULL_INT) {
						RTSSizePK rtsSizePK = new RTSSizePK(rtsToSave.getId()
								.getRtsId(), 0, NullDef.NULL_INT);
						rtsSizePK.setRtsId(rtsToSave.getId().getRtsId());
						rtsSizePK.setRtsLineItemId(0);
						rtsSizePK.setRtsSizeId(NullDef.NULL_INT);
						rtsSize.setId(rtsSizePK);
					} else {
						RTSSizePK rtsSizePK = new RTSSizePK(rtsToSave.getId()
								.getRtsId(), 0, rtsSize.getId().getRtsSizeId());
						// Commented to Fix CR - 18391.
//						rtsSizePK.setRtsId(rtsToSave.getId().getRtsId());
//						rtsSizePK.setRtsLineItemId(NullDef.NULL_INT);
						rtsSize.setId(rtsSizePK);
					}

					rtsSizeDAO.save(rtsSize, session);

					// RTSSize rtsSize = (RTSSize) rtsSizeItr.next();
					// rtsSizeDAO.save(rtsSize, session);
				}
			}
			// This looping will be changed after exploring suitable solution
			if (rtsToSave.getRtsLineItems() != null
					&& rtsToSave.getRtsLineItems().size() > 0) {
				Iterator rtsLinesItr = rtsToSave.getRtsLineItems().iterator();
				while (rtsLinesItr.hasNext()) {
					RTSLineItem rtsLineItem = (RTSLineItem) rtsLinesItr.next();
					if (rtsLineItem.getRtsSizes() != null
							&& rtsLineItem.getRtsSizes().size() > 0) {
						RTSSizeDAO rtsSizeDAO = new RTSSizeDAO();
						Iterator rtsSizeItr = rtsLineItem.getRtsSizes()
								.iterator();
						while (rtsSizeItr.hasNext()) {
							RTSSize rtsSize = (RTSSize) rtsSizeItr.next();
							if (rtsSize.getId().getRtsSizeId() != NullDef.NULL_INT) {
								RTSSizePK rtsSizePK = new RTSSizePK(rtsToSave
										.getId().getRtsId(), rtsLineItem
										.getId().getRtsLineItemId(), rtsSize
										.getId().getRtsSizeId());
								rtsSizePK
										.setRtsId(rtsToSave.getId().getRtsId());
								rtsSizePK.setRtsLineItemId(rtsLineItem.getId()
										.getRtsLineItemId());
								rtsSize.setId(rtsSizePK);
							} else {
								RTSSizePK rtsSizePK = new RTSSizePK(rtsToSave
										.getId().getRtsId(), rtsLineItem
										.getId().getRtsLineItemId(),
										NullDef.NULL_INT);
								rtsSizePK
										.setRtsId(rtsToSave.getId().getRtsId());
								rtsSizePK.setRtsLineItemId(rtsLineItem.getId()
										.getRtsLineItemId());
								rtsSizePK.setRtsSizeId(NullDef.NULL_INT);
								rtsSize.setId(rtsSizePK);
							}
							rtsSizeDAO.save(rtsSize, session);
						}
					}
				}
			}
		}
	}

	/**
	 * Deletes a RTS based on RTSPK
	 */
	public void delete(RTSPK pk) throws PersistLayerException {
		SOSDebugLog.DEBUG_LOG.logLow("executing the delete method() in RtsDAO");
		Session session = SOSDef.openSession(new RTSInterceptor());

		RTS rts = null;
		try {
			rts = find(session, pk);
			if (rts != null) {
				session.delete(rts);
			}

			// Delete the mulitple sizes for Header
			if (rts != null && rts.getRtsSizes() != null
					&& rts.getRtsSizes().size() > 1) {
				RTSSizeDAO rtsSizeDAO = new RTSSizeDAO();
				Iterator rtsSizeItr = rts.getRtsSizes().iterator();
				while (rtsSizeItr.hasNext()) {
					RTSSize rtsSize = (RTSSize) rtsSizeItr.next();
					rtsSizeDAO.delete(rtsSize.getId(), session);
				}
			}
			// This looping will be changed after exploring suitable solution
			// Delete the mulitple sizes for RTS Lines
			if (rts != null && rts.getRtsLineItems() != null
					&& rts.getRtsLineItems().size() > 0) {
				Iterator rtsLinesItr = rts.getRtsLineItems().iterator();
				while (rtsLinesItr.hasNext()) {
					RTSLineItem rtsLineItem = (RTSLineItem) rtsLinesItr.next();
					if (rtsLineItem.getRtsSizes() != null
							&& rtsLineItem.getRtsSizes().size() > 1) {
						RTSSizeDAO rtsSizeDAO = new RTSSizeDAO();
						Iterator rtsSizeItr = rtsLineItem.getRtsSizes()
								.iterator();
						while (rtsSizeItr.hasNext()) {
							RTSSize rtsSize = (RTSSize) rtsSizeItr.next();
							rtsSizeDAO.delete(rtsSize.getId(), session);
						}
					}

				}
			}
			session.flush();
		} catch (PersistLayerException ex) {
			throw ex;
		} finally {
			session.close();
		}
	}

	public void delete(RTS rts, RTSPK pk) throws PersistLayerException {
		SOSDebugLog.DEBUG_LOG.logLow("executing the delete method() in RtsDAO");
		Session session = SOSDef.openSession(new RTSInterceptor());
		//RTS rtsObj = null;
		try {
			// Delete the mulitple sizes for Header
			if (rts != null && rts.getRtsSizes() != null && rts.getRtsSizes().size() > 0) {

				RTSSizeDAO rtsSizeDAO = new RTSSizeDAO();
				Iterator rtsSizeItr = rts.getRtsSizes().iterator();
				while (rtsSizeItr.hasNext()) {
					RTSSize rtsSize = (RTSSize) rtsSizeItr.next();
					rtsSizeDAO.delete(rtsSize.getId(), session);
				}
			}
			if (rts != null && rts.getRtsLineItems() != null && rts.getRtsLineItems().size() > 0) {
				Iterator rtsLinesItr = rts.getRtsLineItems().iterator();
				while (rtsLinesItr.hasNext()) {
					RTSLineItem rtsLineItem = (RTSLineItem) rtsLinesItr.next();
					Set rtsAddSize = rtsLineItem.getRtsSizes();
					if (rtsAddSize != null && rtsAddSize.size() > 0) {
						RTSSizeDAO rtsSizeDAO = new RTSSizeDAO();
						Iterator rtsSizeItr = rtsAddSize.iterator();
						while (rtsSizeItr.hasNext()) {
							RTSSize rtsSize = (RTSSize) rtsSizeItr.next();
							rtsSizeDAO.delete(rtsSize.getId(), session);
						}
					}
				}
			}
			session.flush();
			//rtsObj = find(session, pk);
			if (rts != null ) {
				session.delete(rts);
			}
			session.flush();
			//Deleting Entire RTS, Increase PO quantity of RTS Line Item to be deleted.
			updateDependentObjects(rts, session.connection());
		} catch (PersistLayerException ex) {
			SOSDebugLog.DEBUG_LOG.logLow("IN RTS DAO :: Exception While Deleting RTS from RTS List page -- > " + ex);
			throw ex;
		} catch(PersistenceException ex){
			SOSDebugLog.DEBUG_LOG.logLow("IN RTS DAO :: Exception While Deleting RTS and Updating dependent RTS object -- > " + ex);
		}finally {
			session.close();
		}
	}

	/**
	 * finds a RTS based on input RTS Primary Key
	 */
	public RTS find(RTSPK rtsPK) throws PersistLayerException {
		SOSDebugLog.DEBUG_LOG.logLow("executing the find method() in RtsDAO");
		Session session = SOSDef.openSession(new RTSInterceptor());
		RTS rts = null;
		try {
			rts = find(session, rtsPK);
			RTSSizeDAO rtsSizeDAO = new RTSSizeDAO();
			List list = rtsSizeDAO.findAll(rtsPK.getRtsId(), NULL, session);
			if (list != null && list.size() > 0)
			{
				Set set = new HashSet(list);
				rts.setRtsSizes(set);
			}
			// Find the mulitple sizes for RTS Lines
			if (rts != null && rts.getRtsLineItems() != null && rts.getRtsLineItems().size() > 0)
			{
				Iterator rtsLinesItr = rts.getRtsLineItems().iterator();
				while (rtsLinesItr.hasNext())
				{
					RTSLineItem rtsLineItem = (RTSLineItem) rtsLinesItr.next();
					List lineItemSizelist = rtsSizeDAO.findAll(rtsLineItem.getId().getRtsId(),
															   rtsLineItem.getId().getRtsLineItemId(), session);
					if (lineItemSizelist != null && lineItemSizelist.size() > 0)
					{
						Set set = new HashSet(lineItemSizelist);
						rtsLineItem.setRtsSizes(set);
					}
				}
			}
			try{
			    // To find RTS HardCheck Errors and SoftCheck Errors
//				ArrayList errorlist = ErrorLogsDAO.getErrorLog(rts, rts.getTcCompanyId(), rts.getId().getRtsId(), "RTS", session);
//				if(errorlist != null && errorlist.size() > 0){
//					rts.setHardCheckErrors(errorlist);
//				}
//				errorlist = new ErrorLogsDAO().getErrorLog(rts, rts.getTcCompanyId(), rts.getId().getRtsId(), "RTS", session);
//				if(errorlist != null && errorlist.size() > 0){
//						rts.setHardCheckErrors(errorlist);
//				}
			} catch(Exception e){
				SOSDebugLog.DEBUG_LOG.logLow(" Exception While getting Warings and Error");
			}


		} finally {
			if (session != null) {
				session.close();
			}
		}
		return rts;
	}

	/**
	 * finds a RTS based on input RTS Primary Key
	 */
	protected RTS find(Session session, RTSPK rtsPK)
			throws PersistLayerException {
		return (RTS) session.load(RTS.class, rtsPK);
	}

	private void populateLineItemsWithRTSId(RTS rtsToSave) {
        try {
            Set lineItems = rtsToSave.getRtsLineItems();
            Iterator itr = lineItems.iterator();
            SOSOrderManagerSB orderMgr = (SOSOrderManagerSB) SOSHomeHolder.ORDER_MANAGER_SB_HOME.create();
            AbstractOrder masterOrder = null;
            while (itr.hasNext()) {
                try {
                    RTSLineItem rtsLineItem = (RTSLineItem) itr.next();
                    rtsLineItem.getId().setRtsId(rtsToSave.getId().getRtsId());
                    rtsLineItem.setTcCompanyId(rtsToSave.getTcCompanyId());
                    masterOrder = (AbstractOrder) orderMgr.getOrderDetails(new UserId(rtsToSave.getCreatedSource()), new ShipperPK(new ShipperId(rtsToSave.getTcCompanyId())), new Long(rtsLineItem.getOrderId()).intValue()).getBean();
                    rtsLineItem.setRegionId(masterOrder.getRegionId().getRegionIdInt());
                } catch (SOSException e) {
                    SOSDebugLog.DEBUG_LOG.logException(SOSDebugLog.ORDER_CATEGORY,e);  //To change body of catch statement use File | Settings | File Templates.
                } catch (RemoteException e) {
                    SOSDebugLog.DEBUG_LOG.logException(SOSDebugLog.ORDER_CATEGORY,e);  //To change body of catch statement use File | Settings | File Templates.
                } catch (MaxLengthString.MaxLengthExceededException e) {
                    SOSDebugLog.DEBUG_LOG.logException(SOSDebugLog.ORDER_CATEGORY,e);  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        } catch (CreateException e) {
            SOSDebugLog.DEBUG_LOG.logException(SOSDebugLog.ORDER_CATEGORY,e);  //To change body of catch statement use File | Settings | File Templates.
        } catch (RemoteException e) {
            SOSDebugLog.DEBUG_LOG.logException(SOSDebugLog.ORDER_CATEGORY,e);  //To change body of catch statement use File | Settings | File Templates.
        }
    }

	private String getClassName() {
		return "com.logistics.so.rts.dao.RTSDAO";
	}

	// This will give you the quantity already done RTS
	public double findRTSQuantityForPOLineItem(long orderId, String poLineItemId)
			throws PersistLayerException {
		double rtsQuantity = 0;
		SOSDebugLog.DEBUG_LOG
				.logLow("executing the findRTSQuantityForPOLineItem method() in RtsDAO");
		Session session = SOSDef.openSession(new RTSInterceptor());

		try {

			Object[] vals = new Object[] { new Long(orderId), poLineItemId };
			List list = session.find(
					"select sum(r.sizeValueDouble) from RTSLineItem r where"
							+ " r.orderId=? and r.lineItemId=? ", vals,
					new Type[] { Type.LONG, Type.STRING });
			if (list != null && list.size() > 0 && list.get(0) != null) {
				rtsQuantity = ((Double) list.get(0)).doubleValue();
			}
		} catch (PersistLayerException ex) {
			throw ex;
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return rtsQuantity;
	}

	// This will give you the quantity already done RTS
	// Added This method to fix CR - 19138
	public double findRTSQuantityForPOLineItem(long orderId, String poLineItemId, int rtsId)
			throws PersistLayerException {
		double rtsQuantity = 0;
		SOSDebugLog.DEBUG_LOG
				.logLow("executing the findRTSQuantityForPOLineItem method() in RtsDAO");
		Session session = SOSDef.openSession(new RTSInterceptor());

		try {

			Object[] vals = new Object[] { new Long(orderId), poLineItemId, new Integer(rtsId) };
			List list = session.find(
					"select sum(r.sizeValueDouble) from RTSLineItem r where"
							+ " r.orderId=? and r.lineItemId=? and r.id.rtsId <> ? ", vals, 
					new Type[] { Type.LONG, Type.STRING, Type.INTEGER });
			if (list != null && list.size() > 0 && list.get(0) != null) {
				rtsQuantity = ((Double) list.get(0)).doubleValue();
			}
		} catch (PersistLayerException ex) {
			throw ex;
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return rtsQuantity;
	}
    //  This will give you the quantity already done RTS
    public double getRTSQuantityForTOLineItem(int rtsId, int rtsLineItemId)
             throws PersistLayerException
    {

         double remainingQty=0;
         SOSDebugLog.DEBUG_LOG
                 .logLow("executing the findRTSQuantityForPOLineItem method() in RtsDAO");
         Session session = SOSDef.openSession(new RTSInterceptor());
         List list=null;
         try {
             if(rtsId == NullDef.NULL_INT && rtsLineItemId == NullDef.NULL_INT )
             {
                return 0;
             }
             else if(rtsId != NullDef.NULL_INT && rtsLineItemId == NullDef.NULL_INT )
             {
                 Object[] vals = new Object[] { new Integer(rtsId)};
                 list = session.find(
                         "select r.quantityDouble,r.totalSizeOnOrders from RTS r where "
                                 + " r.id.rtsId=? ", vals,
                         new Type[] { Type.INTEGER});
             }
             else
             {
                 Object[] vals = new Object[] { new Integer(rtsId),new Integer(rtsLineItemId)};
                 list = session.find(
                          "select r.sizeValueDouble,r.totalSizeOnOrders from RTSLineItem r where"
                                  + " r.id.rtsId=? and r.id.rtsLineItemId=? ", vals,
                          new Type[] {  Type.INTEGER, Type.INTEGER });

             }

             if (list != null && list.size() > 0 && list.get(0) != null) {
                 Object obj[]=(Object[])list.get(0);
                 Double a1=(Double)obj[0];
                 Double a2=(Double)obj[1];
                 if(a1!=null && a2!=null)
                 {
                    double rtsQuantity = a1.doubleValue();
                    double totalSizeOnOrder = a2.doubleValue();
                    remainingQty=rtsQuantity-totalSizeOnOrder;
                 }

             }
         } catch (PersistLayerException ex) {
             throw ex;
         } finally {
             if (session != null) {
                 session.close();
             }
         }
         return remainingQty;
     }
	private void updateDependentObjects(RTS rts, Connection connection)
			throws PersistenceException {
		updateReadyToShipStatus(rts, connection);
	}

	/**
	 * updates ORDER,ORDER_LINE_ITEMS tables IS_READY_TO_SHIP flag
	 */
	private void updateReadyToShipStatus(RTS rts, Connection connection)
			throws PersistenceException {
		Set rtsSet = rts.getRtsLineItems();
		Iterator rtsIter = rtsSet.iterator();
		while (rtsIter.hasNext()) {
			RTSLineItem rli = (RTSLineItem) rtsIter.next();
			updateReadyToShip((int) rli.getOrderId(), rli.getLineItemId(),
					connection);
			updateReadyToShip((int) rli.getOrderId(), connection);
		}
	}

	/**
	 * Updates ORDER_LINE_ITEM table's IS_Ready_TO_SHIP When all the RTS line
	 * items are ready to ship,IS_Ready_TO_SHIP is true else IS_Ready_TO_SHIP is
	 * false
	 */
	public void updateReadyToShip(int OrderID, String lineItemId,
			Connection connection) throws PersistenceException {
		SOSDebugLog.DEBUG_LOG
				.logDebug(
						SOSDebugLog.ORDER_CATEGORY,
						"Executing "
								+ getClass().getName()
								+ ".updateReadyToShip(int OrderID, String lineItemId, Connection connection)");
		JDBCPersistenceWriter pw = null;
		PersistenceReader pr = null;
		int remainingQuantity = -1;

		SQLQuery selectSQL = new SQLQuery("SELECT "
				+ "(SELECT MO_SIZE_VALUE FROM ORDER_LINE_ITEM "
				+ "WHERE  ORDER_LINE_ITEM.ORDER_ID=? "
				+ "AND ORDER_LINE_ITEM.LINE_ITEM_ID=?) " + "- "
				+ "(SELECT nvl(SUM(SIZE_VALUE),0) FROM RTS_LINE_ITEM "
				+ "WHERE  RTS_LINE_ITEM.ORDER_ID=? "
				+ "AND RTS_LINE_ITEM.LINE_ITEM_ID=? ) " + "from dual");

		SQLQuery updateSQL = new SQLQuery(
				"UPDATE ORDER_LINE_ITEM  SET ORDER_LINE_ITEM.IS_READY_TO_SHIP=? "
						+ "WHERE ORDER_LINE_ITEM.ORDER_ID=? "
						+ "AND ORDER_LINE_ITEM.LINE_ITEM_ID=?");

		pw = new JDBCPersistenceWriter(selectSQL, connection);
		pw.setNextInt(OrderID);
		pw.setNextString(lineItemId);
		pw.setNextInt(OrderID);
		pw.setNextString(lineItemId);
		pr = pw.executeQuery();
		if (pr.next()) {
			remainingQuantity = pr.getNextInt();
		}
		pw = new JDBCPersistenceWriter(updateSQL, connection);
		if (remainingQuantity > 0) {
			pw.setNextInt(0);
		} else {
			pw.setNextInt(1);
		}
		pw.setNextInt(OrderID);
		pw.setNextString(lineItemId);
		pw.executeUpdate();
	}

	/**
	 * Updates Orders table's IS_Ready_TO_SHIP When all the line items are ready
	 * to ship,IS_Ready_TO_SHIP is true else IS_Ready_TO_SHIP is false
	 */
	public void updateReadyToShip(int orderId, Connection conn)
			throws PersistenceException {
		SOSDebugLog.DEBUG_LOG.logDebug(SOSDebugLog.ORDER_CATEGORY, "Executing "
				+ getClass().getName()
				+ ".updateReadyToShip(int OrderID, Connection connection)");
		JDBCPersistenceWriter pw = null;
		PersistenceReader pr = null;
		String sql = "SELECT COUNT(*) FROM ORDER_LINE_ITEM "
				+ "WHERE ORDER_ID = " + orderId + " AND "
				+ "IS_READY_TO_SHIP = 0 AND " + "IS_MO_LINE_ITEM = 1";

		int count = -1;
		pw = new JDBCPersistenceWriter(new SQLQuery(sql), conn);
		pr = pw.executeQuery();
		while (pr.next()) {
			count = pr.getNextInt();
		}

		String updateSql = "UPDATE ORDERS SET is_ready_to_ship = ? WHERE order_id = ?";
		pw = new JDBCPersistenceWriter(new SQLQuery(updateSql), conn);

		if (count > 0) {
			pw.setNextInt(0); // set 0 means not ready to ship
			pw.setNextInt(orderId);
			pw.executeUpdate();

		} else {
			pw.setNextInt(1); // set 1 means ready to ship
			pw.setNextInt(orderId);
			pw.executeUpdate();
		}
	}

	/*
	 * public List getAllRoutingRequestForPO(int poID)throws
	 * PersistLayerException { BaseDataDebugLog.DEBUG_LOG.logLow( "executing the
	 * getAllRoutingRequestForPO method() in RtsDAO"); Session session =
	 * SOSDef.openSession(new RTSInterceptor()); try{ Object[] vals = new
	 * Object[] {new Long(poID)}; List list = session.find("select
	 * r.lineItemId,r.rrcNumber,r.createdDttm from RTSLineItem r where" + "
	 * r.orderId=? ", vals, new Type[] { Type.LONG });
	 *
	 * if (list == null && list.size()<=0) { return new ArrayList(0); }
	 *
	 * return list; } catch (PersistLayerException ex) { return new
	 * ArrayList(0); } finally { session.close(); } }
	 */

	public String getRoutingRequestReceivedForOrderLineItem(
			String orderIDString, String lineItemID)
			throws PersistLayerException {
		Session session = SOSDef.openSession(new RTSInterceptor());
		String rrcNumber = null;
		try {
			if (!Misc.isNullString(orderIDString)
					&& !Misc.isNullString(lineItemID)) {
				Object[] vals = new Object[] { new Long(orderIDString),
						lineItemID };
				List list = session.find(
						"select r.rrcNumber from RTSLineItem r where"
								+ " r.orderId=? and r.lineItemId=?", vals,
						new Type[] { Type.LONG, Type.STRING });

				if ((list != null) && (list.isEmpty() == false)) {
					rrcNumber = (String) list.get(0);
				}
			}
		} catch (PersistLayerException ex) {

		} finally {
			if (session != null) {
				session.close();
			}
		}
		return rrcNumber;
	}

	public ArrayList findRTSObjects(int orderId, int tcCompanyId)
			throws PersistLayerException {
		Session session = SOSDef.openSession(new RTSInterceptor());
		ArrayList rtsObjectsList = null;

		try {
			Query queryRTSList = session
					.createQuery(" from RTS rts where rts.id.rtsId in ( select  rli.id.rtsId from RTSLineItem rli where rli.orderId = "
							+ orderId
							+ "  and  rli.tcCompanyIdInteger = "
							+ tcCompanyId + ")");

			List list = queryRTSList.list();
			if (list == null) {
				rtsObjectsList = new ArrayList(0);
			} else {
				rtsObjectsList = new ArrayList(list);
			}

			//
		} catch (PersistLayerException ple) {
			throw ple;
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return rtsObjectsList;
	}

	public void deleteRTSLinesCreatedFromWouldBeDeletedPOLines(
			int purchaseOrderId, List deletablePOLinesIDsList, int tcCompanyId)
			throws PersistLayerException {
		String rtsLineItemIds = "";
		if (deletablePOLinesIDsList == null
				|| deletablePOLinesIDsList.isEmpty()) {
			return; // Nothing to delete, return
		} else {
			Iterator rtsLinesIter = deletablePOLinesIDsList.iterator();
			while (rtsLinesIter.hasNext()) {
				rtsLineItemIds = rtsLineItemIds + ", \'"
						+ (String) rtsLinesIter.next() + "\'";
			}
			rtsLineItemIds = rtsLineItemIds.substring(1, rtsLineItemIds
					.length());
		}
		Session session = null;
		try {
			session = SOSDef.openSession();
			String whereClause = "where rli.lineItemId in ( " + rtsLineItemIds
					+ " ) and rli.orderId = " + purchaseOrderId;
			String deleteDetailsQuery = "from RTSLineItem rli " + whereClause;
			session.delete(deleteDetailsQuery);
			session.flush();
		} finally {
			if (session != null)
				session.close();
		}
	}


	public List loadRTSObjects(int purchaseOrderId,
			List deletablePOLinesIDsList, int tcCompanyId)
			throws PersistLayerException {
		List rtsObjectList = null;
		Session session = SOSDef.openSession(new RTSInterceptor());
		StringBuffer inClause = new StringBuffer(" and rli.lineItemId in ( ");
		Iterator poLinesIDListIter = deletablePOLinesIDsList.iterator();
		while (poLinesIDListIter.hasNext()) {
			String poLineIDStr = (String) poLinesIDListIter.next();
			if (poLinesIDListIter.hasNext()) {
				inClause.append(poLineIDStr + ", ");
			} else {
				inClause.append(poLineIDStr + " )");
			}
		}
		try {
			Query queryRTSList = session
					.createQuery(" from RTS rts where rts.id.rtsId in ( select  rli.id.rtsId from RTSLineItem rli where rli.orderId = "
							+ purchaseOrderId
							+ " "
							+ inClause.toString()
							+ "  and  rli.tcCompanyIdInteger = "
							+ tcCompanyId
							+ ")");

			List list = queryRTSList.list();
			if (list == null) {
				rtsObjectList = new ArrayList(0);
			} else {
				rtsObjectList = new ArrayList(list);
			}
		} catch (PersistLayerException ple) {
			throw ple;
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return rtsObjectList;
	}

	// This method will return the TOLineItem List;
	public Vector getTOLineItems(int rtsId, List rtsLineItemIdList) {
		Vector toList = null;
		//Fix CR - '16686' TO Dispaly TO's craeted for RTS HEADER. in this case Line Item List will be null.
		String orderBy = " ORDER BY RTS_LINE_ITEM_ID ";
		if (rtsLineItemIdList == null || rtsLineItemIdList.size() < 1)
		{
			orderBy = "";
			//return null;
		}
		StringBuffer whereClause = new StringBuffer(" where IS_MO_LINE_ITEM=0 and RTS_ID=" + rtsId + " ");
		/*
		 * This condition of ORDER_LINE_ITEM.RTS_LINE_ITEM_ID in (list of given
		 * RTS LI IDs) is really not needed Hence, removing this condition from
		 * the SQL - Gautam CR 11969 + " and RTS_LINE_ITEM_ID in ("); for (int i =
		 * 0; i < rtsLineItemIdList.size(); i++) { Integer rtsLineItemIdInt =
		 * (Integer) rtsLineItemIdList.get(i); int rtsLineItemId = 0; if
		 * (rtsLineItemIdInt != null) { rtsLineItemId =
		 * rtsLineItemIdInt.intValue(); } if (i > 0) whereClause.append(",");
		 * whereClause.append(rtsLineItemId); } whereClause.append(")");
		 */
		try {
			toList = SOSOrderLineItemJDBCAccessor.ACCESSOR.findAll(whereClause.toString(), orderBy);
		} catch (PersistenceException ex) {
			SOSDebugLog.DEBUG_LOG.logLow("getTOLineItems" + this.getClass().getName());
		}
		return toList;
	}

	// Add to sendoutbound RTS tXML
	// Preparing RoutingRequest Object per vendor
	private Map prepareRoutingRequestMapForEachBP(RoutingRequest rr,
			ShipperPK shipperPK, SourceDesc sourceDesc) throws SOSException {
		Map routingRequestMap = new HashMap();
		try {

			for (Iterator iter = rr.getRRDetailList().iterator(); iter
					.hasNext();) {
				RoutingRequestDetail rrDetail = (RoutingRequestDetail) iter
						.next();
				AbstractOrder oData = getAbstractOrderFromRRDetail(rrDetail,
						shipperPK, sourceDesc);
				String bpString = rr.getBusinessPartnerId();
				if (Misc.isNullString(bpString) && oData !=null)
					bpString = oData.getBusinessPartnerIdString();
				if (!Misc.isNullTrimmedString(bpString)) {
					if (routingRequestMap.containsKey(bpString)) {
						RoutingRequest existingRR = (RoutingRequest) routingRequestMap
								.get(bpString);
						existingRR.getRRDetailList().add(rrDetail);
					} else {
						RoutingRequest clonedRR = (RoutingRequest) getClone(rr);
						if (clonedRR.getRRDetailList() != null) {
							clonedRR.getRRDetailList().clear();
						}
						List rrDetailList = clonedRR.getRRDetailList();
						if (rrDetailList.isEmpty())
							rrDetailList.add(rrDetail);

						routingRequestMap.put(bpString, clonedRR);
					}
				}
			}

		} catch (Exception e) {
			SOSDebugLog.DEBUG_LOG.logException(SOSDebugLog.RTS_EDI753_CATEGORY,
					e);
			throw new SOSException("SQLException during RTSMgrBean."
					+ "performReadyToShipForRoutingRequest : " + e.toString());
		}
		return routingRequestMap;
	}

	public static Object getClone(Object orig) {
		Object obj = null;
		try {
			// Write the object out to a byte array
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeObject(orig);
			out.flush();
			out.close();

			// Make an input stream from the byte array and read
			// a copy of the object back in.
			ObjectInputStream in = new ObjectInputStream(
					new ByteArrayInputStream(bos.toByteArray()));
			obj = in.readObject();
		} catch (IOException e) {

		} catch (ClassNotFoundException cnfe) {

		}
		return obj;
	}

	private AbstractOrder getAbstractOrderFromRRDetail(
			RoutingRequestDetail rrDetail, ShipperPK shipperPK,
			SourceDesc sourceDesc) throws SOSException {
		AbstractOrder oData = null;

		try {
			SOSOrderManagerSB ordMgr = SOSHomeHolder.ORDER_MANAGER_SB_HOME
					.create();
			String tcOrderID = rrDetail.getTCOrderIdString();
			int tcCompanyId = shipperPK.getShipperId().getCompanyId();
			//int ordID = SOSOrderJDBCAccessor.ACCESSOR.getOrderId(tcCompanyId, tcOrderID);
			int ordID = SOSOrderJDBCAccessor.ACCESSOR.getOrderId(getRelatedBU(sourceDesc.getUserId(), tcCompanyId) ,tcOrderID);
			if(ordID != -1)
			{
				oData = (AbstractOrder)ordMgr.getEmptyOrder(sourceDesc.getUserId(),shipperPK,CreationType.IMPORT).getBean();
				Map orderDataMap=SOSOrderJDBCAccessor.ACCESSOR.getRequiredOrderDataFromOrderID(ordID);
		 	    if(orderDataMap!=null)
		 	    {
		 	    	Set entrySet = orderDataMap.entrySet();
					Iterator entrySetIter = entrySet.iterator();
					while(entrySetIter.hasNext())
					{
						Map.Entry mapEntry = (Map.Entry)entrySetIter.next();
						String key = (String)mapEntry.getKey();
						String value = (String)mapEntry.getValue();

						if("ORDER_ID".equalsIgnoreCase(key) && Misc.isNullTrimmedString(value)==false)
		 	    	       oData.setOrderId(new OrderId(Integer.parseInt(value)));
		 	    	    else if("TC_ORDER_ID".equalsIgnoreCase(key))
		 	    	       oData.setTCOrderIdString(value);
		 	    	    else if("TC_COMPANY_ID".equalsIgnoreCase(key) && Misc.isNullTrimmedString(value)==false)
		 	    	       oData.setShipperPK(new ShipperPK(new ShipperId(Integer.parseInt(value))));
		 	    	    else if("BUSINESS_PARTNER_ID".equalsIgnoreCase(key))
		 	    	       oData.setBusinessPartnerIdString(value);
		 	    	    else if("ACCEPTANCE_STATUS".equalsIgnoreCase(key))
	 	    	           oData.setAcceptanceStatusCodeString(value);
	 	    	    }
				 }
		  }
		} catch (RemoteException e) {
			SOSDebugLog.DEBUG_LOG.logException(SOSDebugLog.RTS_EDI753_CATEGORY,
					e);
			throw new SOSException("SQLException during RTSMgrBean."
					+ "performReadyToShipForRoutingRequest : " + e.toString());
		} catch (CreateException e) {
			SOSDebugLog.DEBUG_LOG.logException(SOSDebugLog.RTS_EDI753_CATEGORY,
					e);
			throw new SOSException("SQLException during RTSMgrBean."
					+ "performReadyToShipForRoutingRequest : " + e.toString());
		} catch (SOSException e) {
			SOSDebugLog.DEBUG_LOG.logException(SOSDebugLog.RTS_EDI753_CATEGORY,
					e);
			throw new SOSException("SQLException during RTSMgrBean."
					+ "performReadyToShipForRoutingRequest : " + e.toString());
		} catch (Exception e) {
			SOSDebugLog.DEBUG_LOG.logException(SOSDebugLog.RTS_EDI753_CATEGORY,
					e);
			throw new SOSException("SQLException during RTSMgrBean."
					+ "performReadyToShipForRoutingRequest : " + e.toString());
		}

		return oData;
	}

	private void sendOutboundRTSXmlAsAcknowledgementForEDI753(
			ShipperPK shipperPK, Map routingRequestMapSpecificToBP)
			throws SOSException {
		try {

			Set entrySet = routingRequestMapSpecificToBP.entrySet();
			Iterator entrySetIter = entrySet.iterator();
			RoutingRequest rr = null;
			while (entrySetIter.hasNext()) {
				Map.Entry mapEntry = (Map.Entry) entrySetIter.next();
				String bpString = (String) mapEntry.getKey();
				rr = (RoutingRequest) mapEntry.getValue();

				// If atleast one is succssful send out bound RTS
				// check for company param

				if (rr.getRRDetailList().size() > 0) {

					SOSCommManager sosCommMgr = SOSHomeHolder.COMM_MANAGER_HOME
							.create();
					sosCommMgr.sendRTSMessage(rr);

				}
			}
		} catch (RemoteException e) {
			SOSDebugLog.DEBUG_LOG.logException(SOSDebugLog.RTS_EDI753_CATEGORY,
					e);
			throw new SOSException("SQLException during RTSMgrBean."
					+ "performReadyToShipForRoutingRequest : " + e.toString());
		} catch (CreateException e) {
			SOSDebugLog.DEBUG_LOG.logException(SOSDebugLog.RTS_EDI753_CATEGORY,
					e);
			throw new SOSException("SQLException during RTSMgrBean."
					+ "performReadyToShipForRoutingRequest : " + e.toString());
		}
		/*
		 * catch (SOSEngineException e) { SOSDebugLog.DEBUG_LOG.logException
		 * (SOSDebugLog.RTS_EDI753_CATEGORY, e); throw new SOSException(
		 * "SQLException during RTSMgrBean."+
		 * "performReadyToShipForRoutingRequest : "+ e.toString()); }
		 */
	}

	// For Lookup
	public ArrayList getRtsIds(String searchString, Vector vect)
			throws PersistLayerException {
		SOSDebugLog.DEBUG_LOG
				.logLow("executing the getRtsIds(String searchString, int tcCompanyId) in RtsDAO"
						+ this.getClass().getName());
		ArrayList ids = null;
		Session session = SOSDef.openSession();
		searchString = searchString.replace('*', '%');
		StringBuffer bf = new StringBuffer(StringEncoder
				.oracleConvert(StringEncoder.replace(searchString, "_", "\\_")));
		if (bf != null && bf.toString().indexOf("_") != -1) {
			bf.append("' ESCAPE '\\'");
		}
		searchString = bf.toString();
		try {
			// fix CR # 18236 cast for DB2, size 20 for DB2 bigint
			Query query = session
					.createQuery("select rts.id.rtsId from RTS rts where cast(rts.id.rtsId as char(20)) like ('"
							+ searchString
							+ "') "
							+ " and rts.tcCompanyId in ( "
							+ JSPUtility.createListString(vect, ",")
							+ ")order by rts.id.rtsId");

			query.setFirstResult(0);
			query.setMaxResults(BaseDataLookupMgr.MAX_LOOKUP_ROWS + 1);

			List list = query.list();
			if (list == null) {
				ids = new ArrayList(0);
			} else {
				ids = new ArrayList(list);
			}

		} catch (PersistLayerException ple) {
			throw ple;
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return ids;

	}

	// For Lookup
	public ArrayList getRtsLineIds(String searchString, Vector vect)
			throws PersistLayerException {
		SOSDebugLog.DEBUG_LOG
				.logLow("executing the getRtsLineIds(String searchString, Vector vect) in RtsDAO"
						+ this.getClass().getName());
		ArrayList ids = null;
		Session session = SOSDef.openSession();
		searchString = searchString.replace('*', '%');
		StringBuffer bf = new StringBuffer(StringEncoder
				.oracleConvert(StringEncoder.replace(searchString, "_", "\\_")));
		if (bf != null && bf.toString().indexOf("_") != -1) {
			bf.append("' ESCAPE '\\'");
		}
		searchString = bf.toString();
		try {
			Query query = session
					.createQuery("select distinct rli.id.rtsLineItemId from RTSLineItem rli where rli.id.rtsLineItemId  like ('"
							+ searchString
							+ "') "
							+ " and rli.tcCompanyIdInteger in ( "
							+ JSPUtility.createListString(vect, ",")
							+ ")order by rli.id.rtsLineItemId");

			query.setFirstResult(0);
			query.setMaxResults(BaseDataLookupMgr.MAX_LOOKUP_ROWS + 1);

			List list = query.list();
			if (list == null) {
				ids = new ArrayList(0);
			} else {
				ids = new ArrayList(list);
			}

		} catch (PersistLayerException ple) {
			throw ple;
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return ids;

	}

	// For Lookup
	public ArrayList getEDIRefIdList(String searchString, Vector vect)
			throws PersistLayerException {
		SOSDebugLog.DEBUG_LOG
				.logLow("executing the getEDIRefIdList(String searchString, Vector vect) in RtsDAO"
						+ this.getClass().getName());
		ArrayList ids = null;
		Session session = SOSDef.openSession();
		searchString = searchString.replace('*', '%');
		StringBuffer bf = new StringBuffer(StringEncoder
				.oracleConvert(StringEncoder.replace(searchString, "_", "\\_")));
		if (bf != null && bf.toString().indexOf("_") != -1) {
			bf.append("' ESCAPE '\\'");
		}
		searchString = bf.toString();
		try {
			Query query = session
					.createQuery("select distinct rli.rrcNumber from RTSLineItem rli where rli.rrcNumber  like ('"
							+ searchString
							+ "') "
							+ " and rli.tcCompanyIdInteger in ( "
							+ JSPUtility.createListString(vect, ",")
							+ ")order by rli.rrcNumber");

			query.setFirstResult(0);
			query.setMaxResults(BaseDataLookupMgr.MAX_LOOKUP_ROWS + 1);

			List list = query.list();
			if (list == null) {
				ids = new ArrayList(0);
			} else {
				ids = new ArrayList(list);
			}

		} catch (PersistLayerException ple) {
			throw ple;
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return ids;

	}

	// For Lookup
	public ArrayList getEDIRoutingNoList(String searchString, Vector vect)
			throws PersistLayerException {
		SOSDebugLog.DEBUG_LOG
				.logLow("executing the getEDIRoutingNoList(String searchString, Vector vect) in RtsDAO"
						+ this.getClass().getName());
		ArrayList ids = null;
		Session session = SOSDef.openSession();
		searchString = searchString.replace('*', '%');
		StringBuffer bf = new StringBuffer(StringEncoder
				.oracleConvert(StringEncoder.replace(searchString, "_", "\\_")));
		if (bf != null && bf.toString().indexOf("_") != -1) {
			bf.append("' ESCAPE '\\'");
		}
		searchString = bf.toString();
		try {
			Query query = session
					.createQuery("select distinct rli.attributeNumber from RTSLineItem rli where rli.attributeNumber  like ('"
							+ searchString
							+ "') "
							+ " and rli.tcCompanyIdInteger in ( "
							+ JSPUtility.createListString(vect, ",")
							+ ")order by rli.attributeNumber");

			query.setFirstResult(0);
			query.setMaxResults(BaseDataLookupMgr.MAX_LOOKUP_ROWS + 1);

			List list = query.list();
			if (list == null) {
				ids = new ArrayList(0);
			} else {
				ids = new ArrayList(list);
			}

		} catch (PersistLayerException ple) {
			throw ple;
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return ids;

	}

	public void sendOutboundRTSTxml(RTS rtsObject) throws EJBException {
		try {
			if (rtsObject.isInteractive()) {
				ParameterManager mgr = BaseDataHomeHolder.PARAMETER_MANAGER_HOME
						.create();
				String selectedCPType = mgr.getParameterValue(rtsObject
						.getTcCompanyId(), SOSParameterType.SEND_RTS_TXML
						.getCode());
				if ("true".equals(selectedCPType)) {
					RoutingRequest rr = new RoutingRequest();
					rr.populateRoutingRequestFromRTS(rtsObject);
					ShipperPK shipperPK = new ShipperPK(new ShipperId(rtsObject
							.getTcCompanyId()));
					SourceType st = null;
					if ("EDI".equals(rtsObject.getModifiedSourceType()))
						st = SourceType.EDI;
					else
						st = SourceType.USER;
					SourceDesc sourceDesc = new SourceDesc(st, new Source(
							rtsObject.getModifiedSource()));
					Map routingRequestMapSpecificToBP = prepareRoutingRequestMapForEachBP(
							rr, shipperPK, sourceDesc);
					sendOutboundRTSXmlAsAcknowledgementForEDI753(shipperPK,
							routingRequestMapSpecificToBP);
				}
			}
		} catch (Exception ex) {
			SOSDebugLog.DEBUG_LOG.logHigh(SOSDebugLog.ORDER_CATEGORY, ex
					.getMessage());
		}
	}

	public Collection findRTSLineItemsAsPerDetailFilter(int rtsId,String whereClause) throws PersistLayerException {
		SOSDebugLog.DEBUG_LOG
				.logLow("executing the findAll(int orderId) in RtsDAO"
						+ this.getClass().getName());
		Session session = SOSDef.openSession(new RTSInterceptor());
		Collection rtsLineItems = null;
		try {
			Query query = session
					.createQuery("from RTSLineItem rli where rli.id.rtsId ="+rtsId +" " + whereClause);
			List list = query.list();
			if (list == null) {
				rtsLineItems = new ArrayList(0);
			} else {
				rtsLineItems = new ArrayList(list);
			}
		} catch (PersistLayerException ple) {
			throw ple;
		} finally {
			if (session != null)
				session.close();
		}
		return rtsLineItems;
	}
	public String findRTSEDIIdString(int rtsId)
				throws PersistLayerException {
			String ediIdString="";
			SOSDebugLog.DEBUG_LOG
					.logLow("executing the findRTSEDIIdString method() in RtsDAO");
			Session session = SOSDef.openSession(new RTSInterceptor());
			try {
				Object[] vals = new Object[] { new Integer(rtsId)};
				List list = session.find(
						"select r.rrcNumber from RTS r where"
								+ " r.id.rtsId=?", vals,
								new Type[] { Type.INTEGER});
				if (list != null && list.size() > 0 && list.get(0) != null) {
					ediIdString = (String) list.get(0);
				}
			} catch (PersistLayerException ex) {
				throw ex;
			} finally {
				if (session != null) {
						session.close();
				}
			}
			return ediIdString;
	}

    //This method will update the RTSStatus and RTS_LineStatus according to TO status
    public void updateRTSNLINEStatusWithShipment(
        int tcCompanyId,
        int shipmentId)
        throws SOSException {
        Connection conn = null;
        try {
            conn = SOSDef.CONNECTION_CREATOR.createConnection();
            updateRTSNLINEStatusWithShipment(tcCompanyId, shipmentId, conn);
        } catch (PersistenceException ex) {
            SOSDebugLog.DEBUG_LOG.logLow(
                "Exception occured while executing the updateRTSNLINEStatusWithShipment method() in RtsDAO");
            throw new SOSException(ex);
        } finally {
            JDBCFunc.closeJDBCResources(conn);
        }
    }
    public void updateRTSNLINEStatusWithShipment(
        int tcCompanyId,
        int shipmentId,
        Connection conn)
        throws SOSException {
        JDBCPersistenceWriter pw = null;
        PersistenceReader pr = null;
        try {
            SOSDebugLog.DEBUG_LOG.logLow(
                "inside updateRTSNLINEStatusWithShipment method in RtsDAO");
            String orderQuery =
                "select order_id from ORDERS where SHIPMENT_ID="
                    + shipmentId
                    + " AND TC_COMPANY_ID="
                    + tcCompanyId;
            pw = new JDBCPersistenceWriter(new SQLQuery(orderQuery), conn);
            pr = pw.executeQuery();
            List orderIdList = new ArrayList();
            while (pr.next()) {
                orderIdList.add(new Integer(pr.getNextInt()));
            }
            for (int i = 0; i < orderIdList.size(); i++) {
                updateRTSStatusWithOrder(
                    ((Integer) orderIdList.get(i)).intValue(),
                    conn);
            }
        } catch (PersistenceException ex) {
            SOSDebugLog.DEBUG_LOG.logLow(
                "Exception occured while executing the updateRTSNLINEStatusWithShipment method() in RtsDAO");
            throw new SOSException(ex);
        } finally {
            JDBCFunc.closeJDBCResources(
                pr,
                pw,
                "updateRTSNLINEStatusWithShipment");
        }
    }
    public void updateRTSStatusWithOrder(
        Collection orderidList,
        Connection conn)
        throws SOSException {
        if (orderidList != null && orderidList.size() > 0) {
            Iterator itr = orderidList.iterator();
            while (itr.hasNext()) {
                Object iObj = itr.next();
                if (iObj instanceof Integer) {
                    int orderId = ((Integer) iObj).intValue();
                    updateRTSStatusWithOrder(orderId);

                } else if (iObj instanceof OrderPK) {
                    int orderId = ((OrderPK)iObj).getOrderId();
                    updateRTSStatusWithOrder(orderId);
                }

            }
        }
    }
    public void updateRTSStatusWithOrder(int order_id) throws SOSException {
        Connection conn = null;
        try {
            SOSDebugLog.DEBUG_LOG.logLow(
                "inside updateRTSStatusWithOrder method in RtsDAO");
            conn = SOSDef.CONNECTION_CREATOR.createConnection();
            updateRTSStatusWithOrder(order_id, conn);
        } catch (PersistenceException ex) {
            SOSDebugLog.DEBUG_LOG.logLow(
                "Exception occured while executing the updateRTSStatusWithOrder method() in RtsDAO");
            throw new SOSException(ex);
        } finally {
            JDBCFunc.closeJDBCResources(conn);
        }
    }
    public void updateRTSStatusWithOrder(int order_id, Connection conn)
        throws SOSException {
        JDBCPersistenceWriter pw = null;
        PersistenceReader pr = null;
        try {
            SOSDebugLog.DEBUG_LOG.logLow(
                "inside updateRTSStatusWithOrder method in RtsDAO");
            String statusQuery =
                "select order_line_item.order_id,order_status,rts_id,rts_line_item_id from order_line_item,orders where rts_id in(select rts_id from order_line_item where order_id=?) and order_line_item.order_id=Orders.order_id and rts_id is not null";
            pw = new JDBCPersistenceWriter(new SQLQuery(statusQuery), conn);
            pw.setNextInt(order_id);
            pr = pw.executeQuery();
            List rtsHelperlist = new ArrayList();
            //Populate all the OrderId for that RTS
            while (pr.next()) {
                RTSStatusHelper rtsStatusHelper = new RTSStatusHelper();
                rtsStatusHelper.setOrderId(pr.getNextInt());
                rtsStatusHelper.setOrderStatus(pr.getNextInt());
                rtsStatusHelper.setRtsId(pr.getNextInt());
                rtsStatusHelper.setRtsLineId(pr.getNextInt());
                rtsHelperlist.add(rtsStatusHelper);
            }
            JDBCFunc.closeJDBCResources(pr, pw, "updateRTSStatusWithOrder");
            Map statusMap = new HashMap();
            Map rtsLineStatusMap = new HashMap();
            for (int i = 0; i < rtsHelperlist.size(); i++) {

                RTSStatusHelper rtsStatusHelper =
                    (RTSStatusHelper) rtsHelperlist.get(i);
                RTSPK rtsPK = new RTSPK(rtsStatusHelper.getRtsId());
                if (statusMap.get(rtsPK) == null) {
                    statusMap.put(
                        rtsPK,
                        new Integer(rtsStatusHelper.getOrderStatus()));
                } else if (
                    (statusMap.containsKey(rtsPK))
                        && (((Integer) statusMap.get(rtsPK)).intValue()
                            > rtsStatusHelper.getOrderStatus())) {
                    statusMap.put(
                        rtsPK,
                        new Integer(rtsStatusHelper.getOrderStatus()));
                }
                //create Map for LineItem Status
                if (rtsStatusHelper.getRtsLineId() > 0) {
                    RTSLineItemPK rtsLineItemPK =
                        new RTSLineItemPK(
                            rtsStatusHelper.getRtsId(),
                            rtsStatusHelper.getRtsLineId());
                    if (rtsLineStatusMap.get(rtsLineItemPK) == null) {
                        rtsLineStatusMap.put(
                            rtsLineItemPK,
                            new Integer(rtsStatusHelper.getOrderStatus()));
                    } else if (
                        (rtsLineStatusMap.containsKey(rtsLineItemPK)
                            && ((Integer) rtsLineStatusMap.get(rtsLineItemPK))
                                .intValue()
                                > rtsStatusHelper.getOrderStatus())) {
                        rtsLineStatusMap.put(
                            rtsLineItemPK,
                            new Integer(rtsStatusHelper.getOrderStatus()));
                    }

                }
            }
            //Update the RTSHeader Status for each rtsId
            Map oldRTSStatusMap = new HashMap();
            Set rtsIdset = statusMap.keySet();
            Iterator rtsIdIterator = rtsIdset.iterator();
            StringBuffer selectOldRTSStatusQuery =
                new StringBuffer(" select rts_id,rts_status from rts where rts_id in (-1");
            while (rtsIdIterator.hasNext()) {
                int rtsId = ((RTSPK) rtsIdIterator.next()).getRtsId();
                selectOldRTSStatusQuery.append("," + rtsId);
            }
            selectOldRTSStatusQuery.append(")");
            //Fetch the old Status of RTS
            pw =
                new JDBCPersistenceWriter(
                    new SQLQuery(selectOldRTSStatusQuery),
                    conn);
            pr = pw.executeQuery();
            while (pr.next()) {
                int rtsId = pr.getNextInt();
                int oldRTSStatus = pr.getNextInt();
                //Donot Update when RTS status if it is less then planned status
                //Remove RTS ID
                if (oldRTSStatus
                    < Integer.parseInt(RTSStatus.PLANNED.getCode())) {
                    statusMap.remove(new RTSPK(rtsId));
                }
            }
            JDBCFunc.closeJDBCResources(pr, pw, "updateRTSStatusWithOrder");
            //Update the status with that of TO
            if (statusMap.size() > 0) {
                StringBuffer newRTSStatusQuery =
                    new StringBuffer(" update rts set rts_status=? ,MODIFIED_DTTM=GETDATE(),VERSION=VERSION+1 where rts_id = ? ");
                pw =
                    new JDBCPersistenceWriter(
                        new SQLQuery(newRTSStatusQuery),
                        conn);
                Set statusSet = statusMap.keySet();
                Iterator itr = statusSet.iterator();
                while (itr.hasNext()) {
                    RTSPK rtsPK = (RTSPK) itr.next();
                    Integer orderStatusInger = (Integer) statusMap.get(rtsPK);
                    int rtsStatus = -1;
                    if (orderStatusInger.intValue()
                        == Integer.parseInt(OrderStatus.COMPLETE.getCode())) {
                        rtsStatus =
                            Integer.parseInt(RTSStatus.COMPLETE.getCode());
                        pw.setNextInt(rtsStatus);
                        pw.setNextInt(rtsPK.getRtsId());
                        SOSDebugLog.DEBUG_LOG.logLow(
                            "inside updateRTSStatusWithOrder method in RtsDAO--RTS_STATUS:"
                                + rtsStatus
                                + ",RTS_ID:"
                                + rtsPK.getRtsId());
                        pw.addBatch();
                    }

                }
                pw.executeBatch();
            }
            JDBCFunc.closeJDBCResources(pr, pw, "updateRTSStatusWithOrder");
            //Update the Status for RTS Line
            Map oldRTSLineStatusMap = new HashMap();
            Set rtsLinePKset = rtsLineStatusMap.keySet();
            Iterator rtsLinePKIterator = rtsLinePKset.iterator();
            StringBuffer selectOldRTSLineStatusQuery =new StringBuffer();
            if((BaseDataDef.DB2_DRIVER_NAME.equals(System.getProperty("db2.dbDriverClassName"))))
            {

                    selectOldRTSLineStatusQuery.append(" select RTS_ID,RTS_LINE_ITEM_ID,RTS_LINE_STATUS from RTS_LINE_ITEM where (RTS_ID,RTS_LINE_ITEM_ID) in (values (-1,-1)");

            }
            else
            {

                selectOldRTSLineStatusQuery.append(" select RTS_ID,RTS_LINE_ITEM_ID,RTS_LINE_STATUS from RTS_LINE_ITEM where (RTS_ID,RTS_LINE_ITEM_ID) in ((-1,-1)");

            }
            while (rtsLinePKIterator.hasNext()) {
                RTSLineItemPK rtsLineItemPK =
                    (RTSLineItemPK) rtsLinePKIterator.next();
                int rtsId = rtsLineItemPK.getRtsId();
                int rtsLineId = rtsLineItemPK.getRtsLineItemId();
                selectOldRTSLineStatusQuery.append(
                    ",(" + rtsId + "," + rtsLineId + ")");
            }
            selectOldRTSLineStatusQuery.append(")");
            //Fetch the old Status of RTSLineItem
            pw =
                new JDBCPersistenceWriter(
                    new SQLQuery(selectOldRTSLineStatusQuery),
                    conn);
            pr = pw.executeQuery();
            while (pr.next()) {
                int rtsId = pr.getNextInt();
                int rtsLineId = pr.getNextInt();
                int oldRTSLineStatus = pr.getNextInt();
                //Donot Update when RTS status if it is less then planned status
                //Remove RTSLine form Map if the status is less than planned
                if (oldRTSLineStatus
                    < Integer.parseInt(RTSLineStatus.UNPLANNED.getCode())) {
                    rtsLineStatusMap.remove(
                        new RTSLineItemPK(rtsId, rtsLineId));
                }
            }
            JDBCFunc.closeJDBCResources(pr, pw, "updateRTSStatusWithOrder");
            //Update the Line status with that of TO
            if (rtsLineStatusMap.size() > 0) {
                StringBuffer newRTSStatusQuery =
                    new StringBuffer(" update rts_line_item set RTS_LINE_STATUS = ?,MODIFIED_DTTM=GETDATE(),VERSION=VERSION+1 where rts_id = ? and RTS_LINE_ITEM_ID= ? ");
                pw =
                    new JDBCPersistenceWriter(
                        new SQLQuery(newRTSStatusQuery),
                        conn);
                Set lineStatusSet = rtsLineStatusMap.keySet();
                Iterator lineStatusItr = lineStatusSet.iterator();
                while (lineStatusItr.hasNext()) {
                    RTSLineItemPK rtsLineItemPK =
                        (RTSLineItemPK) lineStatusItr.next();
                    Integer orderStatusInger =
                        (Integer) rtsLineStatusMap.get(rtsLineItemPK);
                    int rtsLineStatus = -1;
                    if (orderStatusInger.intValue()
                        == Integer.parseInt(OrderStatus.PLANNED.getCode())) {
                        rtsLineStatus =
                            Integer.parseInt(RTSLineStatus.PLANNED.getCode());
                    }
                    if (orderStatusInger.intValue()
                        == Integer.parseInt(OrderStatus.AVAILABLE.getCode())) {
                        rtsLineStatus =
                            Integer.parseInt(RTSLineStatus.AVAILABLE.getCode());
                    }
                    if (orderStatusInger.intValue()
                        == Integer.parseInt(OrderStatus.ASSIGNED.getCode())) {
                        rtsLineStatus =
                            Integer.parseInt(RTSLineStatus.ASSIGNED.getCode());
                    }
                    if (orderStatusInger.intValue()
                        == Integer.parseInt(OrderStatus.TENDERED.getCode())) {
                        rtsLineStatus =
                            Integer.parseInt(RTSLineStatus.TENDERED.getCode());
                    }
                    if (orderStatusInger.intValue()
                        == Integer.parseInt(OrderStatus.ACCEPTED.getCode())) {
                        rtsLineStatus =
                            Integer.parseInt(RTSLineStatus.ACCEPTED.getCode());
                    }
                    if (orderStatusInger.intValue()
                        == Integer.parseInt(OrderStatus.IN_TRANSIT.getCode())) {
                        rtsLineStatus =
                            Integer.parseInt(
                                RTSLineStatus.IN_TRANSIT.getCode());
                    }
                    if (orderStatusInger.intValue()
                        == Integer.parseInt(OrderStatus.DELIVERED.getCode())) {
                        rtsLineStatus =
                            Integer.parseInt(RTSLineStatus.DELIVERED.getCode());
                    }
                    if (orderStatusInger.intValue()
                        == Integer.parseInt(OrderStatus.COMPLETE.getCode())) {
                        rtsLineStatus =
                            Integer.parseInt(RTSLineStatus.COMPLETE.getCode());
                    }
                    if (rtsLineStatus != -1) {
                        pw.setNextInt(rtsLineStatus);
                        pw.setNextInt(rtsLineItemPK.getRtsId());
                        pw.setNextInt(rtsLineItemPK.getRtsLineItemId());
                        SOSDebugLog.DEBUG_LOG.logLow(
                            "inside updateRTSStatusWithOrder method in RtsDAO--RTS_LINE_STATUS:"
                                + rtsLineStatus
                                + ",RTS_ID:"
                                + rtsLineItemPK.getRtsId()
                                + ",RTS_LINE_ID:"
                                + rtsLineItemPK.getRtsLineItemId());
                        pw.addBatch();
                    }

                }
                pw.executeBatch();
            }
            JDBCFunc.closeJDBCResources(pr, pw, "updateRTSStatusWithOrder");
            SOSDebugLog.DEBUG_LOG.logLow(
                "Completed Updting RTS status in updateRTSStatusWithOrder of RTSDAO ");
        } catch (PersistenceException ex) {
            SOSDebugLog.DEBUG_LOG.logLow(
                "Exception occured while executing the updateRTSStatusWithOrder method() in RtsDAO");
            throw new SOSException(ex);
        } finally {
            JDBCFunc.closeJDBCResources(pr, pw, "updateRTSStatusWithOrder");
        }
    }
    
//  Implrment 3PL changes.
	public ArrayList getRelatedBU(UserId userId, int companyId)
	{
		ArrayList buCompanyString = new ArrayList();
		try
		{
	        HashMap relatedBuMap = UCLAbstractUtil.findBUsByPermissionCodeAndUserId(SOSPermissionsCodeList.CREATE_RTS, UCL.getUserIdNumberFromUserId(userId).getVal());
	        List relatedCompanyIdList = new ArrayList(relatedBuMap.keySet());
	        for (int i=0;i<relatedCompanyIdList.size();i++){
	        	Integer compId = (Integer) relatedCompanyIdList.get(i);
	        	buCompanyString.add(compId);
	        }
	        if(buCompanyString.size() == 0)// If UCL does not return related company list.
	        	buCompanyString.add(new Integer(companyId));
		} catch (UCLUtilException e)
		{	
			buCompanyString = new ArrayList();
			buCompanyString.add(new Integer(companyId));
			return buCompanyString;
		}
	       return buCompanyString;
	}

}

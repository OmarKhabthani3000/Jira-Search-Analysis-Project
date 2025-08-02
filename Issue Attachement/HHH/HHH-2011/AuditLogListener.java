package com.contata.auditLog.listener;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.hibernate.HibernateException;
import org.hibernate.engine.EntityEntry;
import org.hibernate.event.FlushEvent;
import org.hibernate.event.def.DefaultFlushEventListener;
import org.hibernate.type.Type;
import org.hibernate.util.IdentityMap;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.contata.auditLog.AuditEntry;
import com.contata.auditLog.AuditEntryData;
import com.contata.auditLog.dao.AuditEntryDao;
import com.contata.auditLog.dao.hibernateImpl.AuditEntryDaoHibernateImp;
import com.contata.auditLog.interceptor.AuditEntryInterceptor;
import com.contata.dao.PersistentObject;
import com.contata.dao.managers.DAOUtils;
import com.contata.metaDataReaders.MetaDataFromClass;
import com.contata.metadataBeans.MetaData;
import com.contata.metadataBeans.PropertyMetaData;
import com.contata.model.contacts.Contact;
import com.contata.services.ServiceLocator;
import com.contata.services.ThreadLocalStore;
import com.contata.services.security.ContactSecurityManager;
import com.contata.taglib.utils.PropertyUtil;
import com.contata.taglib.utils.TagUtil;


@SuppressWarnings("serial")
public class AuditLogListener extends DefaultFlushEventListener{
	
	private DAOUtils daoUtils;
	boolean trap ;
	
	public boolean isTrap() {
		return trap;
	}

	public void setTrap(boolean trap) {
		this.trap = trap;
	}

	/** Handle the given flush event.
	 *
	 * @param event The flush event to be handled.
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public void onFlush(FlushEvent event) throws HibernateException{
		ThreadLocalStore localStore = ThreadLocalStore.getInstance();
	
		LinkedList<AuditEntry> list = (LinkedList)localStore.get(AuditEntryInterceptor.KEY);
		final Map.Entry[] listEntry = IdentityMap.concurrentEntries( event.getSession().getPersistenceContext().getEntityEntries() );
	
						final int listEntryLength = listEntry.length;
						for(int ii = 0 ; ii < listEntryLength ; ii++){
							Map.Entry me = listEntry[ii];
							String oldValueList = "";
							String propertyList = "";
							Object entity = me.getKey();
							
							//Introspection on Persistent object to compare it's initial and final state
							if(	!(entity instanceof AuditEntry) && 
									!(entity instanceof AuditEntryData)){

								EntityEntry entityEntry = (EntityEntry)me.getValue();
								String [] properties = entityEntry. getPersister().getPropertyNames();
								PropertyUtil util = PropertyUtil.getInstance();

								for(String property : properties){
									Type type = entityEntry.getPersister().getPropertyType(property);
									if(type.isEntityType() || type.isCollectionType()){
										continue;
									}
									Object newValue = util.getProperty(entity, property);
									if("".equals(newValue)){
										newValue = " ";
									}
									if(entityEntry!=null){
									Object oldValue = entityEntry.getLoadedValue(property);
									
									if("".equals(oldValue)){
										oldValue = " "; 
									}
									if((oldValue == null && newValue != null) || (oldValue != null && !oldValue.equals(newValue))){
										
										oldValueList += "".equals(oldValueList) ? oldValue : "::" + oldValue;
										propertyList += "".equals(propertyList) ? property : "::" + property;
										
									}
									}
								}
								// If there is a change in state of Persistent Object then save it
								if(!("".equals(oldValueList.trim()))){
									saveAuditData(list , entityEntry ,oldValueList , propertyList );
								}

							}
						}
				
		
		super.onFlush(event);

	}
	
	/**
	 * Save AuditEntryData object to database
	 * @param list List containing AuditEntry objects
	 * @param entity Persistent Object to be saved
	 * @param oldValueList Initial state of the object
	 * @param propertyList properties that have been updated
	 */
	private void saveAuditData(LinkedList<AuditEntry> list, EntityEntry entity, String oldValueList, String propertyList) {
		
	
		Serializable persistentId  = entity.getId();
		AuditEntryData data = new AuditEntryData();
		String className = entity.getEntityName();

		if(list == null || list.size() == 0){
			data.setRootEntry(null);
			data.setLeftValue(null);
		}else{
			data.setRootEntry(list.getFirst());
			data.setLeftValue(list.getLast().getLeft());
		}
		
		data.setClassName(className);
		data.setField(propertyList);
		data.setPersistentObjectId((Long)persistentId);
		try {
			data.setContact(ServiceLocator.getBean(ContactSecurityManager.class).getLoggedInUser());
		} catch (RuntimeException e) {
			data.setContact(null);
		}
		data.setOldValue(oldValueList);
		if(list == null || list.size() == 0 || !trap){
			getDAOUtils().save(data);
		}
		else{
			checkEntry(data,list.getLast());
		}

	}
	
	/**
	 * Load DAOUtil Object from spring
	 * @return DAOUtil object
	 */
	public DAOUtils getDAOUtils() {
	    if(this.daoUtils == null) {
	      this.daoUtils = ServiceLocator.getBean(DAOUtils.class);
	    }
	    return daoUtils;
	}
	
	/**
	 * Make sure that there is only one entry in database corresponding to a Persistent Object
	 * @param data AuditEntryData object to be saved
	 * @param last Parent AuditEntry Object 
	 */
	private void checkEntry(AuditEntryData data,  AuditEntry last) {
		String className = data.getClassName();
		AuditEntry first = data.getRootEntry();
		Serializable persistentId = data.getPersistentObjectId();
		String oldValueList = data.getOldValue();
		String propertyList = data.getField();
		
		AuditEntryDao hibernateImp = ServiceLocator.getBean(AuditEntryDao.class);
		List <AuditEntryData> list = hibernateImp.findAuditDataForPersistentObject(className,first, persistentId);
		
		String newPropertyList = "";
		String newOldValueList = "";

		if(list.size() == 0){
			getDAOUtils().save(data);
			return;
		}
		else{
			AuditEntryData entryReference = list.get(0);
			if((!entryReference.getOldValue().equals(oldValueList) || !entryReference.getField().equals(propertyList))){
				
				String[] arr1 = entryReference.getOldValue().split("::");
				String[] arr2 = entryReference.getField().split("::");
				String[] arr3 = oldValueList.split("::");
				String[] arr4 = propertyList.split("::");
				
				outer : for(int ii = 0 ; ii < arr2.length ; ii++){
					inner : for(int jj = 0 ; jj < arr4.length ; jj++){
								if(arr2[ii].equals(arr4[jj])){
									newPropertyList += "".equals(newPropertyList.trim()) ? arr2[ii] : "::" + arr2[ii];
									newOldValueList += "".equals(newOldValueList.trim()) ? arr1[ii] : "::" + arr1[ii];
									continue outer;
								}
							}
							newPropertyList += "".equals(newPropertyList.trim()) ? arr2[ii] : "::" + arr2[ii];
							newOldValueList += "".equals(newOldValueList.trim()) ? arr1[ii] : "::" + arr1[ii];
						}
				
					outer : for(int ii = 0 ; ii < arr4.length ; ii++){
						inner : for(int jj = 0 ; jj < arr2.length ; jj++){
									if(arr4[ii].equals(arr2[jj])){
										continue outer;
									}
								}
								newPropertyList += "".equals(newPropertyList.trim()) ? arr4[ii] : "::" + arr4[ii];
								newOldValueList += "".equals(newOldValueList.trim()) ? arr3[ii] : "::" + arr3[ii];
							}
					
				entryReference.setLeftValue(last.getLeft());
				entryReference.setOldValue(newOldValueList);
				entryReference.setField(newPropertyList);
				getDAOUtils().save(entryReference);
				return;
			}
		}
	}
}

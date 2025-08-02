package it.difesa.siac.ng.service.broker;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import com.almaviva.nderbroker.model.eportal.types.AttachmentName;
import org.hibernate.ScrollMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.jinq.jpa.JinqJPAStreamProvider;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Component;

import com.almaviva.nderbroker.model.MessageOutbound;
import com.almaviva.nderbroker.model.NsnAction;
import com.almaviva.nderbroker.model.container.MessageContainer;
import com.almaviva.nderbroker.model.enumerator.WorkingStatus;
import com.almaviva.nderbroker.model.eportal.action.ASSIGNNIINANDREGISTERUSER;
import com.almaviva.nderbroker.model.eportal.action.Action;
import com.almaviva.nderbroker.service.DatabaseService;

import it.difesa.siac.ng.domain.model.parameters.masterdata.NationsNcbNcageMaskVarTab;
import it.difesa.siac.ng.domain.model.trs.TrsLsaAction;
import it.difesa.siac.ng.exception.NotFoundException;
import it.difesa.siac.ng.util.ResponseErrorCode;

@Component
@NoRepositoryBean
@EntityScan("com.almaviva.nderbroker.model")
public class BrokerDatabaseService implements DatabaseService {

    private JinqJPAStreamProvider streams;
    private EntityManager entityManager;


    public BrokerDatabaseService(EntityManagerFactory entityManagerFactory) {
        streams = new JinqJPAStreamProvider(entityManagerFactory);
        entityManager = entityManagerFactory.createEntityManager();
    }

    @Override
    public Action save(Action action) {
        entityManager.getTransaction().begin();
        entityManager.persist(action);
        entityManager.getTransaction().commit();
        return action;
    }

    @Override
    public List<Action> getActionsByMessageId(String fileName) {
        entityManager.getTransaction().begin();
        String messageId = getMessageIdFromFileName(fileName);
        List<Action> actionList = streams.streamAll(entityManager, Action.class)
                .where(action -> action.getActionId().startsWith(messageId))
                .toList();
        entityManager.getTransaction().commit();
        return actionList;
    }

    @Override
    public List<Action> getActionsByMessageIdToPrint(String messageId) {
        entityManager.getTransaction().begin();
        String nsnType = NsnAction.class.getSimpleName();
        List<Action> actionList = streams.streamAll(entityManager, Action.class)
                .where(action -> action.getActionId().startsWith(messageId))
                .where(action -> !action.getActionType().equals(nsnType))
                .sortedBy(Action::getActionId)
                .toList();
        entityManager.getTransaction().commit();
        return actionList;
    }

    public List<AttachmentName> getAttachmentsByMessageId(String messageId){
        entityManager.getTransaction().begin();
        List<AttachmentName> attachmentNameList = streams.streamAll(entityManager, AttachmentName.class)
                .where(attachmentName -> attachmentName.getValue().startsWith(messageId))
                .sortedBy(AttachmentName::getValue)
                .toList();
        entityManager.getTransaction().commit();
        return attachmentNameList;
    }

    @Override
    public Action getAction(String actionId) {
        Action actionObj;
        List<Action> actionObjList;
        try {
            entityManager.getTransaction().begin();
            actionObjList = streams.streamAll(entityManager, Action.class)
                .where(action -> action.getActionId().equals(actionId)).collect(Collectors.toList());
//		        actionObj = streams.streamAll(entityManager, Action.class)
//		                .where(action -> action.getActionId().equals(actionId)).getOnlyValue();
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            throw new NotFoundException("BrokerDatabaseService: Errore nel caricamento della transazione : " + actionId, ResponseErrorCode.EC_SYSTEM_ENTITY_NOT_FOUND);
        } /*finally {
	    		if (entityManager.getTransaction().isActive()) {
	    			entityManager.getTransaction().commit();
	    		}
        }*/

        if (actionObjList.size() > 1) {
            throw new NotFoundException("BrokerDatabaseService: La transazione compare piu' volte nel database a causa di una rilavorazione non consentita del file XML", ResponseErrorCode.EC_SYSTEM_ENTITY_NOT_FOUND);
        } else if (actionObjList.size() == 0) {
            throw new NotFoundException("La transazione " + actionId + " non e' presente nel database. E' possibile che si stia importando una transazione di tipo INTERROGATE prima della transazione delle quale si vuole conoscere lo stato.", ResponseErrorCode.EC_SYSTEM_ENTITY_NOT_FOUND);
        } else {
            actionObj = actionObjList.get(0);
        }

        return actionObj;
    }

    @Override
    public Integer getNewMessageSerialNumber(String destination) {
        entityManager.getTransaction().begin();
        Integer[] serialNumber = new Integer[1];
        Session session = entityManager.unwrap(Session.class);
        session.doWork(connection -> {
            CallableStatement call = connection.prepareCall("{ ? = call BROKER_XML.getMessageSerialNumber(?) }");
            call.registerOutParameter(1, Types.INTEGER);
            call.setString(2, destination);
            call.execute();
            serialNumber[0] = call.getInt(1);
        });
        entityManager.getTransaction().commit();
        return serialNumber[0];
    }

    @Override
    public void save(MessageOutbound messageOutbound) {
        entityManager.getTransaction().begin();
        entityManager.persist(messageOutbound);
        entityManager.getTransaction().commit();
    }

    @Override
    public MessageOutbound getMessageOutbound(String messageId) {
        entityManager.getTransaction().begin();
        MessageOutbound messageOutboundObj = streams.streamAll(entityManager, MessageOutbound.class)
                .where(messageOutbound -> messageOutbound.getMessageId().equals(messageId)).getOnlyValue();
        entityManager.getTransaction().commit();
        return messageOutboundObj;
    }

    public List<Action> getAllActionToFinalize() {
        return streams.streamAll(entityManager, Action.class)
                .where(action -> action.getWorkingStatus().equals(WorkingStatus.TO_FINALIZE))
                .toList();
    }

    public List<Action> getAllActionToFinalizeByFileName(String fileName) {
        String messageId = getMessageIdFromFileName(fileName);
        return streams.streamAll(entityManager, Action.class)
                .where(action -> action.getWorkingStatus().equals(WorkingStatus.TO_FINALIZE))
                .where(action -> action.getActionId().startsWith(messageId))
                .toList();
    }

    public List<Action> getAllActionToPreFinalize() {
        return streams.streamAll(entityManager, Action.class)
                .where(action -> action.getWorkingStatus().equals(WorkingStatus.TO_PRE_FINALIZE))
                .toList();
    }

    public List<Action> getAllActionToPreFinalizeByFileName(String fileName) {
        String messageId = getMessageIdFromFileName(fileName);
        return streams.streamAll(entityManager, Action.class)
                .where(action -> action.getWorkingStatus().equals(WorkingStatus.TO_PRE_FINALIZE))
                .where(action -> action.getActionId().startsWith(messageId))
                .toList();
    }

    public void saveActionError(Action action, String errorMsg){
        action.setWorkingStatus(WorkingStatus.BLOCKED);
        action.setMessage_error(errorMsg);
        this.save(action);
    }

    public List<TrsLsaAction> getAllLsaTransactions(NationsNcbNcageMaskVarTab nationsNcbNcageMaskVarTab, String actionId) {

        List<TrsLsaAction> lsaTransactions = new ArrayList<>();

        String queryString = "select action.primary_action_id, action.action_id, action.message_date, lsa.priority_code_2867, item.creation_date"
                + " from broker_xml.action action, broker_xml.assign_niin_and_register_user lsa, orasiac.dit_items item"
                + " where action.primary_action_id = lsa.primary_action_id and action.working_status = 0"
                + " and action.action_type = 'ASSIGNNIINANDREGISTERUSER' and action.action_id = item.trs_action_id";

        if (actionId != null && !actionId.isEmpty()) {
            queryString += " and action.action_id = '" + actionId + "'";
        } else if (nationsNcbNcageMaskVarTab != null) {
            queryString += " and action.action_id like '" + nationsNcbNcageMaskVarTab.getIso() + "%'";
        }

        Query query = entityManager.createNativeQuery(queryString);
        List<Object[]> actionList = query.getResultList();
        if (actionList != null && !actionList.isEmpty()) {
            for (Object[] objects : actionList) {
                lsaTransactions.add(new TrsLsaAction((BigDecimal) objects[0], (String) objects[1], (Date) objects[2], (String) objects[3], (Date) objects[4]));
            }
        }

        return lsaTransactions;
    }

    public ASSIGNNIINANDREGISTERUSER getLsaDetail(Integer primaryActionId) {
        return streams.streamAll(entityManager, ASSIGNNIINANDREGISTERUSER.class)
                .where(action -> action.getId() == primaryActionId)
                .getOnlyValue();
    }

    @Override
    public void save(MessageContainer messageContainer) {
        entityManager.getTransaction().begin();
        entityManager.persist(messageContainer);
        entityManager.getTransaction().commit();
    }

    @Override
    public boolean alreadyElaborated(String messageId) {
		entityManager.getTransaction().begin();
		List<MessageContainer> messageContainerObj = streams.streamAll(entityManager, MessageContainer.class)
				.where(messageContainer -> messageContainer.getMessageName().equals(messageId)).toList();
		entityManager.getTransaction().commit();
        return messageContainerObj != null && messageContainerObj.size() > 0;
    }

    /**
     * Restituisce le transazioni inserite nel database del broker prodotte in uscita dopo la lavorazione delle transazioni in ingresso
     *
     * @param messageId
     * @return
     */
    public List<String> getGeneretedTrsOut(String messageId) {
        List<String> trsOutNameList = new ArrayList<>();

        try {
            // la classe Action del broker non mappa con hibernate il campo MESSAGE_OUTBOUND_ID per questo la nativa...
            trsOutNameList = (ArrayList<String>)
                    (entityManager.createNativeQuery("select a.ACTION_ID "
                            + "  from BROKER_XML.MESSAGE_OUTBOUND mo left join BROKER_XML.ACTION a"
                            + "        on a.MESSAGE_OUTBOUND_ID = mo.MESSAGE_OUTBOUND_ID"
                            + "  where     a.ACTION_ID LIKE '" + messageId + "%'")
                            .getResultList());
        } catch (Exception e) { // sopprime l'eccezione, non fermare l'esecuzione per i nomi delle trs prodottea
//			throw new NotFoundException("elenco transazioni prodotte in uscita non recuperabile" + e.getMessage());
        }

        return trsOutNameList;
    }

    private String getMessageIdFromFileName(String fileName){
        String messageId = null;
        char c = fileName.charAt(0);
        if (Character.isLetter(c)){
            messageId = fileName.substring(0, fileName.length() - 4);
        }else if(c >= '0' && c <= '9'){
            String name = fileName.substring(fileName.indexOf("-")+1);
            messageId = name.substring(0,name.length()-4);
        }
        return messageId;
    }
}

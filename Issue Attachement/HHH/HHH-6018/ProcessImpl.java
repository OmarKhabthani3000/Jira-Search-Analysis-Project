/*
 * ========================================================================
 *
 * Copyright (c) 2005-2011 Unpublished Work of Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS AN UNPUBLISHED WORK AND CONTAINS CONFIDENTIAL,
 * PROPRIETARY AND TRADE SECRET INFORMATION OF NOVELL, INC. ACCESS TO
 * THIS WORK IS RESTRICTED TO (I) NOVELL, INC. EMPLOYEES WHO HAVE A NEED
 * TO KNOW HOW TO PERFORM TASKS WITHIN THE SCOPE OF THEIR ASSIGNMENTS AND
 * (II) ENTITIES OTHER THAN NOVELL, INC. WHO HAVE ENTERED INTO
 * APPROPRIATE LICENSE AGREEMENTS. NO PART OF THIS WORK MAY BE USED,
 * PRACTICED, PERFORMED, COPIED, DISTRIBUTED, REVISED, MODIFIED,
 * TRANSLATED, ABRIDGED, CONDENSED, EXPANDED, COLLECTED, COMPILED,
 * LINKED, RECAST, TRANSFORMED OR ADAPTED WITHOUT THE PRIOR WRITTEN
 * CONSENT OF NOVELL, INC. ANY USE OR EXPLOITATION OF THIS WORK WITHOUT
 * AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO CRIMINAL AND CIVIL
 * LIABILITY.
 *
 * ========================================================================
 */
package com.novell.soa.af.impl.core;

import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Semaphore;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;

import org.hibernate.Session;
import org.w3c.dom.Document;

import com.novell.soa.persist.UUID;
import com.novell.idm.attestation.api.AttestationRequestStatus;
import com.novell.idm.attestation.exception.AttestationException;
import com.novell.idm.attestation.persist.AttestationRequest;
import com.novell.idm.attestation.persist.AttestationRequestDAO;
import com.novell.idm.attestation.resources.AttestationRsrc;
import com.novell.idm.security.authorization.api.IRuntimeAuthorizationManager;
import com.novell.idm.security.authorization.runtime.RuntimeAuthType;
import com.novell.idm.security.authorization.runtime.RuntimeOperationType;
import com.novell.soa.af.DataItemException;
import com.novell.soa.af.IComment;
import com.novell.soa.af.ICommentQuery;
import com.novell.soa.af.IDataItem;
import com.novell.soa.af.IEntitlement;
import com.novell.soa.af.IIDXUtil;
import com.novell.soa.af.IProcess;
import com.novell.soa.af.IProcessInfo;
import com.novell.soa.af.IProvisioningRequest;
import com.novell.soa.af.IProvisioningStatus;
import com.novell.soa.af.IProvisioningStatusQuery;
import com.novell.soa.af.IWorkEntry;
import com.novell.soa.af.ProcessConstants;
import com.novell.soa.af.ProcessException;
import com.novell.soa.af.impl.AFCommentId;
import com.novell.soa.af.impl.AFLogEventAction;
import com.novell.soa.af.impl.activity.ActivityException;
import com.novell.soa.af.impl.activity.ActivityNode;
import com.novell.soa.af.impl.activity.ActivityPersistenceException;
import com.novell.soa.af.impl.activity.BranchActivity;
import com.novell.soa.af.impl.activity.ConditionActivity;
import com.novell.soa.af.impl.activity.FinishActivity;
import com.novell.soa.af.impl.activity.IntegrationActivity;
import com.novell.soa.af.impl.activity.LogActivity;
import com.novell.soa.af.impl.activity.MappingActivity;
import com.novell.soa.af.impl.activity.MergeActivity;
import com.novell.soa.af.impl.activity.NotificationActivity;
import com.novell.soa.af.impl.activity.ProvisionActivity;
import com.novell.soa.af.impl.activity.RunnableActivity;
import com.novell.soa.af.impl.activity.StartActivity;
import com.novell.soa.af.impl.activity.StartCorrelatedFlowActivity;
import com.novell.soa.af.impl.activity.UserActivity;
import com.novell.soa.af.impl.model.ModelException;
import com.novell.soa.af.impl.model.ModelFactory;
import com.novell.soa.af.impl.model.ProcessFlowModel;
import com.novell.soa.af.impl.model.binding.ActivityBean;
import com.novell.soa.af.impl.model.binding.BranchActivityBean;
import com.novell.soa.af.impl.model.binding.ConditionActivityBean;
import com.novell.soa.af.impl.model.binding.FinishActivityBean;
import com.novell.soa.af.impl.model.binding.ImportScript;
import com.novell.soa.af.impl.model.binding.IntegrationActivityBean;
import com.novell.soa.af.impl.model.binding.LogActivityBean;
import com.novell.soa.af.impl.model.binding.MappingActivityBean;
import com.novell.soa.af.impl.model.binding.MergeActivityBean;
import com.novell.soa.af.impl.model.binding.NotificationActivityBean;
import com.novell.soa.af.impl.model.binding.ProvisionActivityBean;
import com.novell.soa.af.impl.model.binding.ResourceStatusBindingActivityBean;
import com.novell.soa.af.impl.model.binding.ResourceRequestActivityBean;
import com.novell.soa.af.impl.model.binding.RoleBindingActivityBean;
import com.novell.soa.af.impl.model.binding.RoleRequestActivityBean;
import com.novell.soa.af.impl.model.binding.StartActivityBean;
import com.novell.soa.af.impl.model.binding.StartCorrelatedFlowActivityBean;
import com.novell.soa.af.impl.model.binding.UserActivityBean;
import com.novell.soa.af.impl.persist.ActivityStatusInfo;
import com.novell.soa.af.impl.persist.AfActivityDAO;
import com.novell.soa.af.impl.persist.AfActivityId;
import com.novell.soa.af.impl.persist.AfActivityTimerTasksDAO;
import com.novell.soa.af.impl.persist.AfBranchDAO;
import com.novell.soa.af.impl.persist.CommentDAO;
import com.novell.soa.af.impl.persist.ProcessDAO;
import com.novell.soa.af.impl.persist.ProvisioningStatus;
import com.novell.soa.af.impl.persist.ProvisioningStatusDAO;
import com.novell.soa.af.impl.persist.ProvisioningStatusId;
import com.novell.soa.af.impl.persist.ProvisioningStatusQueryImpl;
import com.novell.soa.af.impl.persist.QuorumDAO;
import com.novell.soa.af.impl.persist.WorkFlowDataDAO;
import com.novell.soa.af.impl.persist.WorkTaskDAO;
import com.novell.soa.af.impl.resources.LicenseComplianceRsrc;
import com.novell.soa.af.impl.resources.WorkflowRsrc;
import com.novell.soa.af.impl.scripting.ProcessInfo;
import com.novell.soa.af.impl.scripting.TargetInfo;
import com.novell.soa.af.impl.scripting.UserActivityInfo;
import com.novell.soa.af.impl.timers.ActivityTimerException;
import com.novell.soa.af.impl.timers.ITimedActivity;
import com.novell.soa.common.i18n.LocaleInfo;
import com.novell.soa.common.i18n.LocalizationHelper;
import com.novell.soa.logging.LogFactory;
import com.novell.soa.logging.Logger;
import com.novell.soa.logging.naudit.NauditHelper;
import com.novell.soa.persist.HibernateUtil;
import com.novell.soa.persist.PersistenceException;
import com.novell.soa.util.EditionHelper;
import com.novell.srvprv.spi.security.IDMAuthorizationException;
import com.novell.srvprv.spi.security.ISecurityContext;
import com.sssw.fw.api.EbiQueryExpression;
import com.sssw.fw.core.EboConfig;

/** The approval flow process. */
public class ProcessImpl implements IProcess, IProcessInfo
{
    private static final Logger LOGGER = LogFactory.getLogger(ProcessImpl.class);

    /** Element type */
    public static final String EL_TYPE = "com.novell.soa.af.IProcess";
    /** Process activity id */
    public static final String NO_ACTIVITYID = "_Process";
    private static final int WORKITEM_MAX_RETRY = 10; // workitem update retries
    private static final int NO_ROW_LIMIT = -1; // no max row limit for result sets
    private ProcessFlowModel m_model; // model
    private final Map<String, ActivityNode> m_activeNodes =
            Collections.synchronizedMap(new HashMap<String, ActivityNode>()); // activity id  to activity map
    private EngineImpl m_engine; // reference to engine
    private RequestContext m_context; // context
    private final Semaphore m_mergeSemaphore = new Semaphore(1);
    /** */
    private String m_requestId;
    /** */
    private String m_processId;
    /** */
    private String m_version;
    /** */
    private Date m_creationTime;
    /** */
    private String m_engineId;
    /** */
    private String m_recipient;
    /** */
    private String m_initiator;
    /** */
    private String m_proxy;
    /** */
    private int m_restrictView;
    /** */
    private int m_processStatus;
    /** */
    private int m_approvalStatus;
    /** */
    private Date m_completionTime;
    /** */
    private String m_resourceType;
    /** */
    private String m_correlationId;

    /** Constructor for processes that have been previously started. */
    public ProcessImpl()
    {
    }

    /**
     * Constructor for processes that have not been previously started.
     *
     * @param model         the process flow model
     * @param recipient     the recipient
     * @param context       the security context of the user initiating the process
     * @param correlationId a GUID used to correlate this flow with other flows or null
     */
    public ProcessImpl(ProcessFlowModel model, String recipient, ISecurityContext context, String correlationId)
    {
        m_model = model;
        setCreationTime(new Timestamp(System.currentTimeMillis()));
        setId(model.getId());
        setRequestId(UUID.generate().toString());
        setVersion(model.getVersion());
        setEngineId(getEngine().getId());
        setRecipient(recipient);
        ISecurityContext proxy = context.getParent();
        //todo you can have non proxy context, todo verify in the next release or add another check
        setProxy(proxy == null || proxy.isServiceContext() ? null : proxy.getPrimaryPrincipal());
        setInitiator(context.getPrimaryPrincipal());
        setProcessStatus(ProcessConstants.STOPPED);
        setApprovalStatus(ProcessConstants.PROCESSING);
        setCompletionTime(null);
        setRestrictView(model.isRestrictViewEnabled() ? ProcessConstants.RESTRICTED : ProcessConstants.NONRESTRICTED);
        setCorrelationId(null == correlationId ? EngineImpl.generateCorrelationIdForUserRequest() : correlationId);
    }

    private void setModel()
    {
        try {
            ModelFactory modelFactory = ModelFactory.getInstance();
            m_model = modelFactory.loadProcessFlow(getId(), getVersion());
        } catch (ModelException e) {
            LOGGER.error(e);
        }
    }

    /**
     * Get this Process's execution context
     *
     * @return RequestContext as the context
     * @throws ProcessException unable to apply changes to work flow data
     */
    private synchronized RequestContext getRequestContext()
    {
        if (m_context == null) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(RequestContext.INITIATOR, getInitiator());
            map.put(RequestContext.RECIPIENT, getRecipient());
            map.put(RequestContext.PROCESS, new ProcessInfo(this));
            map.put(RequestContext.TARGET, new TargetInfo(this));
            map.put(RequestContext.LOCALE, LocaleInfo.getApplicationDefaultLocale());
            map.put(RequestContext.IMPORTEDSCRIPTS, getModel().getRootBean().getImportScript());
            List<UserActivityBean> userActivities = m_model.getUserActivities();
            int size = userActivities.size();
            for (int i = 0; i < size; i++) {
                UserActivityBean bean = userActivities.get(i);
                map.put(bean.getId(), new UserActivityInfo(bean, this));
            }
            m_context = new RequestContext(getRequestId(), map);
        }
        return m_context;
    }

    /**
     * Determine if comment(s) should be generated for process
     *
     * @return boolean true if comments should be generated
     */
    public final boolean isGenerateCommentsEnabled()
    {
        if (m_model == null) {
            return false;
        } else {
            return m_model.isGenerateCommentsEnabled();
        }
    }

    /**
     * Update the request's workitem
     *
     * @param activity the activity associated with the dataitems to persist
     * @return boolean true if successful
     * @throws ProcessException unable to apply changes to work flow data
     */
    public boolean persistWorkFlowData(ActivityNode activity)
            throws ProcessException
    {
        return activity.applyChanges();
    }

    /**
     * Get this Process's data item evaluator
     *
     * @return the data item evaluator
     */
    public IDataItemEvaluator getEvaluator()
    {
        return DataItemEvaluator.getInstance(getRequestContext());
    }

    /**
     * Returns whether global email notifications are enabled/disabled
     *
     * @return true if they are enabled. false if they are not.
     */
    public boolean isNotificationEnabled()
    {
        return m_model.isNotificationEnabled();
    }

    /**
     * Returns the localized name of the process.
     *
     * @param locale the desired locale
     * @return the localized name.
     */
    public String getName(Locale locale)
    {
        return getName(locale, false);
    }

    /**
     * Returns the localized name of the process.
     *
     * @param locale       the desired locale
     * @param scriptClient if caller is script engine, scriptClient is true
     * @return the localized name.
     */
    public String getName(Locale locale, boolean scriptClient)
    {
        String name = m_model.getName(locale);

        if (m_model.isNameExpr(locale)) {
            name = evalExpr(name, scriptClient);
        }
        return name;
    }

    /**
     * Returns the evaluated expression.
     *
     * @param expr         expression to evaluate
     * @param scriptClient if caller is script engine, scriptClient is true
     * @return the result.
     */
    private String evalExpr(String expr, boolean scriptClient)
    {
        DataItemEvaluator evaluator = DataItemEvaluator.getInstance();
        try {
            try {
                WorkFlowData workItemElement = (WorkFlowData) evaluator.getRegisteredObject(RequestContext.FLOWDATA);

                if (workItemElement == null) {
                    workItemElement = WorkFlowDataDAO.findById(getRequestId());
                    evaluator.registerObject(RequestContext.FLOWDATA, workItemElement);
                    evaluator.registerObject(RequestContext.WORKITEM, workItemElement.getDocument());

                }
            } catch (Exception xmle) {
                throw new DataItemException(xmle, WorkflowRsrc.SCRIPTING_ENGINE_PROBLEM);
            }

            expr = evaluator.evaluateToString(expr);

        } catch (DataItemException error) {
            LOGGER.error(error);
        } finally {
            if (!scriptClient) {
                evaluator.reset();
            }
        }

        return expr;
    }

    /**
     * Returns the localized description of the process.
     *
     * @param locale the desired locale
     * @return the localized description.
     */
    public String getDescription(Locale locale)
    {
        return getDescription(locale, false);
    }

    /**
     * Returns the localized description of the process.
     *
     * @param locale       the desired locale
     * @param scriptClient if caller is script engine, scriptClient is true
     * @return the localized description.
     */
    public String getDescription(Locale locale, boolean scriptClient)
    {
        String description = m_model.getDescription(locale);

        if (m_model.isDescriptionExpr(locale)) {
            description = evalExpr(description, scriptClient);
        }

        return description;
    }

    /**
     * Returns the instance/request id of the process.
     *
     * @return the instance/request id of the process.
     */
    public String getRequestId()
    {
        return m_requestId;
    }

    /**
     * Sets the instance/request id of the process.
     *
     * @param requestId the instance/request id of the process.
     */
    public void setRequestId(String requestId)
    {
        this.m_requestId = requestId;
    }

    /**
     * Returns the process definition id.
     *
     * @return the id of the process definition.
     */
    public String getId()
    {
        return m_processId;
    }

    /**
     * Sets the process definition id.
     *
     * @param processId the process definition id.
     */
    private void setId(String processId)
    {
        this.m_processId = processId;
        if (null == m_model && m_version != null) {
            setModel();
        }
    }

    /**
     * Returns process definition version.
     *
     * @return the process definition version.
     */
    public String getVersion()
    {
        return m_version;
    }

    /**
     * Sets the process definition version.
     *
     * @param version the process definition version.
     */
    public void setVersion(String version)
    {
        this.m_version = version;
        if (null == m_model && m_processId != null) {
            setModel();
        }
    }

    /**
     * Gets the approval flow creation time.
     *
     * @return creation time.
     */
    public Date getCreationTime()
    {
        return newDate(m_creationTime);
    }

    /**
     * Sets the process creation time.
     *
     * @param creationTime process instance creation time
     */
    public void setCreationTime(Timestamp creationTime)
    {
        this.m_creationTime = creationTime;
    }

    /**
     * Gets the id of the engine that started the process.
     *
     * @return the engine id.
     */
    public String getEngineId()
    {
        return m_engineId;
    }

    /**
     * Sets the id of the engine that started the process.
     *
     * @param engine the engine id.
     */
    public void setEngineId(String engine)
    {
        this.m_engineId = engine;
    }

    /**
     * Gets the distinguishing name of the user that this approval flow pertains
     * to.
     *
     * @return distinguishing name of approval flow user.
     */
    public String getRecipient()
    {
        return m_recipient;
    }

    /**
     * Sets recipient DN.
     *
     * @param recipient the recipient.
     */
    public void setRecipient(String recipient)
    {
        this.m_recipient = recipient;
    }

    /**
     * Gets the approval flow initiator distinguishing name.
     *
     * @return initiator distinguishing name.
     */
    public String getInitiator()
    {
        return m_initiator;
    }

    /**
     * Sets the approval flow initiator distinguishing name.
     *
     * @param initiator distinguishing name.
     */
    public void setInitiator(String initiator)
    {
        this.m_initiator = initiator;
    }

    /**
     * Sets the correlationId.
     *
     * @param correlationId the GUID.
     */
    public void setCorrelationId(String correlationId)
    {
        this.m_correlationId = correlationId;
    }

    /**
     * Gets the distinguishing name of the proxy that initiated the flow on
     * behalf of the initiator.
     *
     * @return proxy distinguishing name.
     */
    public String getProxy()
    {
        return m_proxy;
    }

    /**
     * Sets the distinguishing name of the proxy that initiated the flow on
     * behalf of the initiator.
     *
     * @param proxy distinguishing name.
     */
    public void setProxy(String proxy)
    {
        this.m_proxy = proxy;
    }

    /**
     * Returns the current execution status of the procss.
     *
     * @return the current state
     * @see ProcessConstants#RUNNING
     * @see ProcessConstants#STOPPED
     * @see ProcessConstants#TERMINATED
     * @see ProcessConstants#COMPLETED
     */
    public int getProcessStatus()
    {
        return m_processStatus;
    }

    /**
     * Sets the execution status of the process.
     *
     * @param status the new status
     */
    public void setProcessStatus(int status)
    {
        this.m_processStatus = status;
    }

    /**
     * Returns the current approval status of the procss.
     *
     * @return the current approval state
     * @see ProcessConstants#PROCESSING
     * @see ProcessConstants#DENIED
     * @see ProcessConstants#APPROVED
     * @see ProcessConstants#RETRACT
     * @see ProcessConstants#ERROR
     */
    public int getApprovalStatus()
    {
        return m_approvalStatus;
    }

    /**
     * Sets the execution approval status of the process.
     *
     * @param approvalStatus the process approval status
     * @see ProcessConstants#PROCESSING
     * @see ProcessConstants#DENIED
     * @see ProcessConstants#APPROVED
     * @see ProcessConstants#RETRACT
     * @see ProcessConstants#ERROR
     */
    public void setApprovalStatus(int approvalStatus)
    {
        this.m_approvalStatus = approvalStatus;
    }

    /**
     * Sets the process completion time.
     *
     * @param ts completion timestamp
     */
    public void setCompletionTime(Timestamp ts)
    {
        this.m_completionTime = newDate(ts);
    }

    /**
     * Get the process completion time.
     *
     * @return the process completion date
     */
    public Date getCompletionTime()
    {
        return newDate(m_completionTime);
    }

    /**
     * Sets the process provision resource type.
     *
     * @param type resource type for the process request
     */
    public void setResourceType(String type)
    {
        this.m_resourceType = type;
    }

    /**
     * Sets the process suggested view restrictions.
     *
     * @param view restriction value
     * @see ProcessConstants#RESTRICTED
     * @see ProcessConstants#NONRESTRICTED
     */
    public void setRestrictView(int view)
    {
        this.m_restrictView = view;
    }

    /**
     * Sets the process definition id.
     *
     * @param id process definition id
     */
    public void setProcessId(String id)
    {
        this.m_processId = id;
    }

    /**
     * Returns the process definition id.
     *
     * @return the id of the process definition.
     */
    public String getProcessId()
    {
        return m_processId;
    }

    /**
     * Gets the provisioning resource type.
     *
     * @return resource type or null if provisioning activity not executed.
     */
    public String getResourceType()
    {
        return m_resourceType;
    }

    /**
     * Returns the suggested view restriction
     *
     * @return the suggested view restriction.
     * @see ProcessConstants#RESTRICTED
     * @see ProcessConstants#NONRESTRICTED
     */
    public int getRestrictView()
    {
        return m_restrictView;
    }

    /**
     * Returns the localized approval status of the process.
     *
     * @param locale desired locale
     * @return the localized approval status string.
     */
    public String getApprovalStatus(Locale locale)
    {
        return LocalizationHelper.getFormattedString(WorkflowRsrc.BUNDLE_ID, ProcessConstants.APPROVAL_STATUS[getApprovalStatus()], locale);
    }

    /**
     * Returns the localized execution status of the process.
     *
     * @param locale this desired locale
     * @return the localized process status string.
     */
    public String getProcessStatus(Locale locale)
    {
        return LocalizationHelper.getFormattedString(WorkflowRsrc.BUNDLE_ID, ProcessConstants.PROCESS_STATUS[getProcessStatus()], locale);
    }

    /**
     * Returns the process flow model.
     *
     * @return the process flow model.
     */
    public ProcessFlowModel getModel()
    {
        return m_model;
    }


    /**
     * Returns the process scripts.
     *
     * @return the list of global process scripts.
     */
    public List<ImportScript> getScripts()
    {
        RequestContext context = getRequestContext();
        List<ImportScript> script =  (List<ImportScript>) (context.getObjects() == null ?
                                                           null : context.getObjects().get(RequestContext.IMPORTEDSCRIPTS));
        return script == null ? new ArrayList<ImportScript>() : script;
    }

    /**
     * Returns the correlationId.
     *
     * @return the correlationId.
     */
    public String getCorrelationId()
    {
        return m_correlationId;
    }

    /**
     * Returns the engine instance.
     *
     * @return the engine instance.
     */
    public EngineImpl getEngine()
    {
        if (null == m_engine) {
            m_engine = (EngineImpl) EngineImpl.getEngine();
        }
        return m_engine;
    }

    /**
     * Returns true if this process' engine id matches the id of currently
     * running engine.
     *
     * @return true if the process is bound to this engine.
     * @throws ProcessException on persistence error
     */
    public boolean isBoundToEngine()
            throws ProcessException
    {
        boolean bound = false;
        if (getEngine().getId().equals(getEngineId())) {
            // Engine ids match in memory. Need to validate persisted state.
            // Process may have been orphaned or reclaimed by another engine
            try {
                bound = ProcessDAO.isBoundToEngine(getEngineId(), getRequestId());
            } catch (PersistenceException ex) {
                throw new ProcessException(ex, WorkflowRsrc.PROCESS_ERROR_PERSISTING_PROCESS_STATE,
                        getRequestId(), getId());
            }
            if (!bound) {
                // Database state shows that this process is no longer bound
                // to this engine. If this process instance is in the active
                // map remove it and cancel it's timers.
                if (!(getEngine().getActiveProcess(getRequestId()) == this)) {
                    cancelTimedActivities();
                    getEngine().removeProcess(getRequestId());
                }
            }
        }
        return bound;
    }

    /**
     * Validates this process' instance matches the instance of currently
     * running engine.
     *
     * @throws ProcessException if process's instance does not match active map instance
     */
    public void validateActiveInstance()
            throws ProcessException
    {
        if (getEngine().getActiveProcess(getRequestId()) == this) {
            return;
        }
        throw new ProcessException(WorkflowRsrc.PROCESS_NOT_VALID_STATE, getRequestId(), getId());
    }

    /**
     * Returns true if the process is RUNNING.
     *
     * @return true if the process is RUNNING.
     */
    public boolean isRunning()
    {
        return ProcessConstants.RUNNING == getProcessStatus();
    }

    /**
     * Returns true if the process is STOPPED.
     *
     * @return true if the process is STOPPED.
     */
    public boolean isStopped()
    {
        return ProcessConstants.STOPPED == getProcessStatus();
    }

    /**
     * Returns true if the process is TERMINATED.
     *
     * @return true if the process is TERMINATED.
     */
    public boolean isTerminated()
    {
        return ProcessConstants.TERMINATED == getProcessStatus();
    }

    /**
     * Returns true if the process is COMPLETED.
     *
     * @return true if the process is COMPLETED.
     */
    public boolean isCompleted()
    {
        return ProcessConstants.COMPLETED == getProcessStatus();
    }

    /**
     * Return status of all currently executing activities (one per branch).
     * Initially, the list has one item, which is the PROCESSING string. When
     * the process is executing a user-facing or Web service activity, the list
     * will contain the name of the activity. Upon final approval or refusal the
     * list has one item, which is APPROVED or DENIED.
     *
     * @return A list with process status information.
     */
    public List<String> getActivityStatus()
    {
        List<String> list = new ArrayList<String>();
        list.add(ProcessConstants.APPROVAL_STATUS[getApprovalStatus()].toString());
        synchronized (m_activeNodes) {
            for (ActivityNode act : m_activeNodes.values()) {
                list.add(act.getActivityId());
            }
        }
        return list;
    }

    /**
     * Return status of all currently executing activities (one per branch).
     * Initially, the list has one item, which is the PROCESSING string. When
     * the process is executing a user-facing or Web service activity, the list
     * will contain the name of the activity. Upon final approval or refusal the
     * list has one item, which is APPROVED or DENIED.
     *
     * @param locale the desired locale
     * @return A list with process status information.
     */
    public List<String> getActivityStatus(Locale locale)
    {
        List<String> list = new ArrayList<String>();
        list.add(getApprovalStatus(locale));
        synchronized (m_activeNodes) {
            for (ActivityNode act : m_activeNodes.values()) {
                list.add(act.getActivityName(locale));
            }
        }
        return list;
    }

    /**
     * Convenience method for getting work entry objects pertaining to this
     * process.
     *
     * @return List of <code>IWorkEntry</code> objects.
     * @throws ProcessException if there is an error getting comments
     */
    public List<IWorkEntry> getWorkEntries()
            throws ProcessException
    {
        try {
            return WorkTaskDAO.getbyRequestId(getRequestId());
        } catch (PersistenceException se) {
            LOGGER.debug("getWorkEntries failed", se);
            throw new ProcessPersistenceException(WorkflowRsrc.PROCESS_ERROR_LOADING_WORK, se,
                    getRequestId(), getId());
        }
    }

    /**
     * Start process execution.
     *
     * @param dataItems the initial set of dtaItems from the Provisioning Request
     * @throws ProcessException if there is an error starting the process
     */
    public void start(Map<String, IDataItem> dataItems)
            throws ProcessException
    {
        boolean isStandardEdition = false;
        try {
            isStandardEdition = EditionHelper.isStandardEdition();
        } catch (IDMAuthorizationException e) {
            // Do Nothing
        }
        if (isStandardEdition) {
            Object[] params = nauditLicenseComplianceWorkflowStarted(
                    getInitiator(), getRecipient(), getCorrelationId());
            LOGGER.info(LicenseComplianceRsrc.BUNDLE_ID,
                    LicenseComplianceRsrc.AE_FEATURE_USED, params);
        }
    
        try {
            StartActivity startNode = null;
            if (null != m_model) {
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("start() requestId=" + getRequestId() + ", type=" + getId());
                }
                Session session = null;
                boolean autoCommit = true;
                try {
                    session = HibernateUtil.getCurrentSession();
                    autoCommit = HibernateUtil.begin(session);
                    setProcessStatus(ProcessConstants.RUNNING);
                    ProcessDAO.saveOrUpdate(this);
                    startNode = new StartActivity(this, m_model.getStartActivity());
                    // create the request instance workitem document
                    Document xmlDoc = createWorkItem();
                    WorkFlowData workItem;
                    IDataItemEvaluator evaluator;
                    // apply the dataitem targets to the document
                    try {
                        DataItemImpl di = new DataItemImpl(ProcessConstants.IDM_COMPLETED_APPROVAL_STATUS, m_model
                                .getDefaultCompletedApprovalStatus(), "string");
                        di.setTarget(RequestContext.FLOWDATATARGET + ProcessConstants.IDM_COMPLETED_APPROVAL_STATUS);
                        dataItems.put(ProcessConstants.IDM_COMPLETED_APPROVAL_STATUS, di);
                        workItem = new WorkFlowData(getRequestId(), xmlDoc);
                        WorkFlowDataDAO.create(workItem);

                        //Must not include the registering of the NrfRequest, NrfResourceRequest, and Attestation object just yet.
                        //Need to create the flowdata document first

                        evaluator = DataItemEvaluator.getInstance(getRequestContext(), false);
                        evaluator.evaluate(dataItems, DataItemEvaluator.TARGET);
                    } catch (DataItemException ex) {
                        throw new ProcessException(ex, WorkflowRsrc.DATAITEM_EVALUATION_FAILURE,
                                getRequestId(), getId());
                    } catch (Exception sqle) {
                        throw new ProcessException(sqle, WorkflowRsrc.DATAITEM_EVALUATION_FAILURE,
                                getRequestId(), getId());
                    }
                    workItem = (WorkFlowData) evaluator.getRegisteredObject(RequestContext.FLOWDATA);
                    WorkFlowDataDAO.update(workItem);
                    startNode.persistStatus(ActivityNode.ARRIVING);
                    HibernateUtil.commit(session, autoCommit);
                    startActivity(startNode, true);
                } catch (ProcessException pe) {
                    throw pe;
                } catch (TransformerException te) {
                    throw new ProcessException(te, WorkflowRsrc.PROCESS_ERROR_PERSISTING_PROCESS_STATE,
                            getRequestId(), getId());
                } catch (PersistenceException ex) {
                    throw new ProcessException(ex, WorkflowRsrc.PROCESS_ERROR_PERSISTING_PROCESS_STATE,
                            getRequestId(), getId());
                } finally {
                    try {
                        if (HibernateUtil.isActiveTransaction()) {
                            HibernateUtil.rollback(session, autoCommit);
                        }
                    } catch (PersistenceException he) {
                        LOGGER.error(WorkflowRsrc.BUNDLE_ID, WorkflowRsrc.PROCESS_ERROR_PERSISTING_PROCESS_STATE,
                                getRequestId(), getId(), he);
                    } finally {
                        HibernateUtil.closeSession(session, autoCommit);
                    }
                }
            } else {
                throw new ProcessException(WorkflowRsrc.PROCESS_MODEL_INVALID,
                        getRequestId(), getId(), getVersion());
            }
        } finally {
            // Null out thread local evaluator so that the next user of this
            // thread is guaranteed a new evaluator.
            DataItemEvaluator.getInstance().reset();
        }
    }

    private Object[] nauditLicenseComplianceWorkflowStarted(String initiator,
            String recipient, String correlationId) 
    {
        // set the originator and recipient
        Object[] params = NauditHelper.getParamsArray(initiator, recipient,
                NauditHelper.LDAP_NOTATION, NauditHelper.LDAP_NOTATION);
        // set text1 and text3
        NauditHelper.setTextParams(params, correlationId, null,
                LocalizationHelper.getString(LicenseComplianceRsrc.BUNDLE_ID,
                        LicenseComplianceRsrc.WORKFLOW_STARTED,
                        LocaleInfo.getApplicationDefaultLocale()));
        
        //set the global correlation id
        if (null != correlationId) {
            NauditHelper.setGlobalCorrelationId(params, correlationId);
        }
        
        return params;
    }

    /**
     * Restart the process.
     *
     * @throws ProcessException if there is an error restarting the process
     */
    public void restart()
            throws ProcessException
    {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("restart() processid=" + getRequestId() + ", process id = " + m_model.getId());
        }

        if (isStopped()) {
            persistProcessStatus(ProcessConstants.RUNNING);
        } else if (isTerminated()) {
            resumeTermination();
            LOGGER.debug("Resumed termination and cleanup of process, requestId: " + getRequestId());

            return;
        } else if (isCompleted()) {
            cleanup();
            LOGGER.debug("Resumed completion and cleanup of process, requestId: " + getRequestId());
        }
        loadActivities();
        boolean isStarted = false;
        synchronized (m_activeNodes) {
            for (ActivityNode activity : m_activeNodes.values()) {
                if (activity instanceof UserActivity && (activity.isRunning() || activity.isPending() || activity.isDeparting())) {
                    continue;
                }

                if (!isStarted && !activity.isIdle()) {
                    isStarted = true;
                }
                if (!activity.isArriving()) {
                    // roll back the status and restart the activity
                    rollbackActivityStatus(activity);
                }
                startActivity(activity, true);
            }
        }
        try {
            // load any persisted activity timers
            EngineImpl.getEngine().loadActivityTimers(getRequestId());
        } catch (Exception ex) {
            throw new ProcessException(ex, WorkflowRsrc.PROCESS_RETRIEVE_ACTIVITY_TIMER_PROBLEM,
                    getRequestId(), getId());
        }
    }

    /**
     * Stop process execution. Process waits until all running non user
     * activities have completed before removing itself.
     */
    public synchronized void stop()
    {
        setProcessStatus(ProcessConstants.STOPPED);
    }

    /**
     * CompleteApprovalStatus the process. This allows the completed process
     * approval status to be set prior to finish processing so scripting objects
     * can reference it from the finish activity.
     *
     * @throws ProcessException if mapping fails
     */
    public void completeApprovalStatus()
            throws ProcessException
    {
        /*
         * if (getApprovalStatus() != ProcessConstants.APPROVED) {
         * setApprovalStatus(ProcessConstants.DENIED); }
         */
        setApprovalStatus(mapApprovalStatus());
    }

    /**
     * Complete the process.
     *
     * @param processStatus completion status for process instance
     * @throws ProcessException if there is an error completing the process
     */
    public void complete(int processStatus)
            throws ProcessException
    {
        // determine if Approved, if not set Approval Status to Denied
        int approvalStatus = mapApprovalStatus();
        /*
         * int approvalStatus = getApprovalStatus(); if (approvalStatus !=
         * ProcessConstants.APPROVED) { approvalStatus =
         * ProcessConstants.DENIED; }
         */
        finish(processStatus, approvalStatus);
    }

    private int mapApprovalStatus()
            throws ProcessException
    {
        try {
            String afstatus = getEvaluator().evaluateToString(
                    RequestContext.XPATHSCRIPT + ".get('" + ProcessConstants.IDM_COMPLETED_APPROVAL_STATUS + "')");

            if (afstatus.equalsIgnoreCase("approved")) {
                return ProcessConstants.APPROVED;
            } else if (afstatus.equalsIgnoreCase("denied")) {
                return ProcessConstants.DENIED;
            } else {
                return ProcessConstants.NOT_SUPPORTED;
            }
        } catch (DataItemException ex) {
            throw new ProcessException(ex, WorkflowRsrc.DATAITEM_EVALUATION_FAILURE,
                    getRequestId(), getId());
        }
    }

    /**
     * Finish the process.
     *
     * @param processStatus  completion status for process instance
     * @param approvalStatus approval status for process instance
     * @throws ProcessException if there is an error completing the process
     */
    public synchronized void finish(int processStatus, int approvalStatus)
            throws ProcessException
    {
        try {
            if (getCompletionTime() == null) {
                String requestId = getRequestId();
                try {
                    // remove process instance from map
                    getEngine().removeProcess(requestId);
                    setApprovalStatus(approvalStatus);
                    setProcessStatus(processStatus);
                    ProcessDAO.saveOrUpdate(this);
                } catch (PersistenceException ex) {
                    throw new ProcessException(ex, WorkflowRsrc.PROCESS_ERROR_PERSISTING_PROCESS_STATE,
                            requestId, getId());
                }

                //Update the Attestation System if attestation type
                if (getModel().getProcessType().equals(IProvisioningRequest.PROCESS_TYPE_ATTESTATION)) {
                    updateAttestationRequestStatus();
                }

                // cancel any timed activities for this process
                cancelTimedActivities();
                // cleanup related tables outside transaction
                // if this fails the tables are cleaned up at engine startup
                try {
                    cleanup();
                } catch (ProcessException ex) {
                    LOGGER.warn(WorkflowRsrc.BUNDLE_ID, WorkflowRsrc.PROCESS_ERROR_PERSISTING_PROCESS_STATE,
                            requestId, getId(), ex);
                }
            }
        } finally {
            // clear the evaluator!!!
            DataItemEvaluator.getInstance().reset();
        }
    }

    private void persistProcessStatus(int processStatus)
            throws ProcessException
    {
        setProcessStatus(processStatus);
        try {
            ProcessDAO.saveOrUpdate(this);
        } catch (PersistenceException ex) {
            throw new ProcessException(ex, WorkflowRsrc.PROCESS_ERROR_PERSISTING_PROCESS_STATE,
                    getRequestId(), getId());
        }
    }

    private void updateAttestationRequestStatus()
    {
        LOGGER.trace("updateAttestationRequestStatus - Started");
        if (getModel().getProcessType().equals(IProvisioningRequest.PROCESS_TYPE_ATTESTATION)) {
            LOGGER.trace("updateAttestationRequestStatus - Attestation Type Found");

            if (getCorrelationId() != null && !getCorrelationId().trim().equals("")) {
                LOGGER.trace("updateAttestationRequestStatus - correlation ID = [" + getCorrelationId() + "]");
                ProcessInfoQueryImpl query = new ProcessInfoQueryImpl();
                EbiQueryExpression queryExpr = query.whereCorrelationId(getCorrelationId());
                EbiQueryExpression processStatusExpr = query.whereProcessStatus(ProcessConstants.RUNNING);
                queryExpr.andExpression(processStatusExpr);
                query.setWhere(queryExpr);

                try {
                    List<IProcessInfo> processInfoList = ProcessDAO.getProcessInfosByQuery(query);

                    if (processInfoList == null || processInfoList.isEmpty()) {
                        Date today = new Date();
                        LOGGER.trace("updateAttestationRequestStatus - updating to completed with completion time = [" + today + "]");
                        AttestationRequest attestation = AttestationRequestDAO.findById(getCorrelationId());
                        attestation.setStatus(AttestationRequestStatus.COMPLETED.value());
                        attestation.setCompletedDate(today);
                        AttestationRequestDAO.update(attestation);
                    }
                } catch (PersistenceException error) {
                    AttestationException exception = new AttestationException(AttestationRsrc.BUNDLE_ID,
                            AttestationRsrc.ERR_SETTING_REQUEST_STATUS,
                            getCorrelationId(),
                            error);
                    LOGGER.error(exception);
                }
            }
        }
        LOGGER.trace("updateAttestationRequestStatus - Done");
    }

    private Document createWorkItem()
            throws ProcessException
    {
        try {
            DocumentBuilderFactory lFactory = DocumentBuilderFactory.newInstance();
            lFactory.setNamespaceAware(true);
            DocumentBuilder builder = lFactory.newDocumentBuilder();
            ByteArrayInputStream stream = new ByteArrayInputStream("<flow-data></flow-data>".getBytes("UTF-8"));
            return builder.parse(stream);
        } catch (Exception ex) {
            throw new ProcessException(ex, WorkflowRsrc.PROCESS_ERROR_CREATING_PROCESS_STATE,
                    getRequestId(), getId());
        }
    }

    /**
     * Activities executing at the time of this method's invocation are going to
     * finish and the branch execution is not going to forwarded to the
     * subsequent activities. This method returns before the currently executing
     * activities are finished. The process is marked as terminated and can not
     * be restarted afterwards.
     *
     * @param ctx     the user context
     * @param state   termination state (error, retraction)
     * @param comment the comment
     * @throws ProcessException thrown if there is an error terminating process instance.
     */
    public void terminate(ISecurityContext ctx, int state, String comment)
            throws ProcessException
    {
        if (ctx != null) {
            //Check authorization for termination for retractions only state = 3 (RETRACT)
            if (state == ProcessConstants.RETRACT) {
                try {
                    IRuntimeAuthorizationManager authMgr = EngineImpl.getEngine().getAuthorizationFactory().getRuntimeAuthorizationManagerInstance();

                    if (!authMgr.checkAccess(RuntimeAuthType.WF_PROCESS_INSTANCE,
                            RuntimeOperationType.RETRACT,
                            this,
                            ctx)) {
                        throw new ProcessException(WorkflowRsrc.ENTITY_NOT_AUTHORIZED);
                    }
                } catch (IDMAuthorizationException error) {
                    LOGGER.error(error);
                    throw new ProcessException(error, WorkflowRsrc.ENTITY_NOT_AUTHORIZED);
                }
            }
            //Done authorization check

            // add comment
            if (comment != null) {
                try {
                    CommentDAO.create(getRequestId(), NO_ACTIVITYID, ctx.getPrimaryPrincipal(), IComment.USER, comment);
                } catch (PersistenceException ex) {
                    LOGGER.debug("terminate failed", ex);
                    throw new ProcessException(ex, WorkflowRsrc.PROCESS_ERROR_TERMINATING_PROCESS,
                            getRequestId(), getId());
                }
            }
            ISecurityContext proxy = ctx.getParent();
            logAction(ctx.getPrimaryPrincipal(), (proxy == null ? null : proxy.getPrimaryPrincipal()), ActivityNode.PROXY,
                    AFLogEventAction.WORKFLOW_RETRACTED);
            terminate(state);
        } else {
            throw new ProcessException(WorkflowRsrc.NULL_SECURITY_CONTEXT, getRequestId(), getId());
        }
    }

    /**
     * Writes an activity action to the log.
     *
     * @param userID        user who performed the action
     * @param secondaryUser delegatee or proxy user ID. Not required for all events.
     * @param secondaryType delegatee or proxy type (Delegatee, Proxy). Not required for all
     *                      events. *
     * @param logEvent      event to log
     */
    private void logAction(String userID, String secondaryUser, int secondaryType, AFLogEventAction logEvent)
    {
        try {
            Object[] params = NauditHelper.getParamsArray(userID, getRecipient(), NauditHelper.LDAP_NOTATION, NauditHelper.LDAP_NOTATION);

            NauditHelper.setSubTargetParams(params, getProcessId(), null, getRequestId());
            if (secondaryUser != null) {
                NauditHelper.setTextParam(params, secondaryUser);
                // Set the data field to secondary user type (Delagatee,
                // Proxy, Other)
                String secondaryUserType = ActivityNode.SECONDARY_USER_TYPES.get(secondaryType);
                NauditHelper.setDataParams(params, secondaryUserType);
            }
            
            // set global correlation id
            NauditHelper.setGlobalCorrelationId(params, getCorrelationId());
            
            LOGGER.info(WorkflowRsrc.BUNDLE_ID, logEvent.getMsgKey(), params);

            CommentDAO.createSystemComment(getRequestId(), NO_ACTIVITYID, logEvent.toString(), userID, null);

        } catch (PersistenceException ex) {
            LOGGER.error(WorkflowRsrc.BUNDLE_ID, WorkflowRsrc.ADD_COMMENT_ERROR, ex);
        }
    }

    /**
     * Activities executing at the time of this method's invocation are going to
     * finish and the branch execution is not going to forwarded to the
     * subsequent activities. This method returns before the currently executing
     * activities are finished. The process is marked as terminated and can not
     * be restarted afterwards.
     *
     * @param approvalStatus termination state (error, retraction)
     * @throws ProcessException thrown if there is an error terminating process instance.
     */
    public synchronized void terminate(int approvalStatus)
            throws ProcessException
    {
        // If the process is running on the engine that received the termination
        // request and the process is not active go ahead and do a full
        // termination
        if (isBoundToEngine() && !isActive()) {
            finish(ProcessConstants.TERMINATED, approvalStatus);
        } else {
            // Process is 1) not bound to this engine or 2) is still active.
            // Persist the completion status but do not cleanup state or
            // set completion time.
            // If 1) the process is not bound to this engine a timer on the
            // correct engine will pickup the process and terminate it if
            // is marked terminated in the db and it's completion time is null
            // If 2) the process is still active. The active thread will
            // resume termination.
            try {
                setApprovalStatus(approvalStatus);
                setProcessStatus(ProcessConstants.TERMINATED);
                ProcessDAO.saveOrUpdate(this);
            } catch (PersistenceException ex) {
                throw new ProcessException(ex, WorkflowRsrc.PROCESS_ERROR_PERSISTING_PROCESS_STATE,
                        getRequestId(), getId());
            }
        }
    }

    /**
     * Forward execution from the current activity to the next activity.
     *
     * @param current the current activity
     * @param next    the next activity
     * @param elem    the object that will be persisted in the same transaction as the
     *                activity state transition
     * @throws ProcessException if an error occurs while forwarding to the next activity.
     */
    public void forward(ActivityNode current, ActivityNode next, Object elem)
            throws ProcessException
    {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("forward() current=" + current + ", next=" + next);
        }
        Session session = null;
        boolean autoCommit = true;
        boolean merge = next instanceof MergeActivity;
        try {
            // Only call notify depart if process is bound to this engine.
            if (current.isRunning() || current.isPending()) {
                try {
                    session = HibernateUtil.getCurrentSession();
                    autoCommit = HibernateUtil.begin(session);
                    if (isBoundToEngine()) {
                        // The status should not be persisted to DEPARTING
                        // if this is
                        // a pending activity. If it is and a crash or
                        // connectivity issue
                        // occurs below the pending state would be lost and
                        // the process
                        // would not recover correctly on restart.
                        if (current.isRunning()) {
                            // Synchronized to ensure isActive does not return false when activity status is changed to departing
                            synchronized (m_activeNodes) {
                                current.persistStatus(ActivityNode.DEPARTING);
                            }
                        }
                    }
                    HibernateUtil.commit(session, autoCommit);
                } catch (ProcessException ex) {
                    throw new ActivityPersistenceException(WorkflowRsrc.ACTIVITY_FORWARDING_ERROR, ex, current.getActivityId(), next.getActivityId());
                } catch (PersistenceException ex) {
                    throw new ActivityPersistenceException(WorkflowRsrc.ACTIVITY_FORWARDING_ERROR, ex,
                            current.getActivityId(), next.getActivityId());
                } finally {
                    if (HibernateUtil.isActiveTransaction()) {
                        try {
                            HibernateUtil.rollback(session, autoCommit);
                        } catch (PersistenceException re) {
                            LOGGER.debug(re);
                        }
                    }
                    HibernateUtil.closeSession(session, autoCommit);
                }
                current.notifyDepart();
            }
            // update next, current, workentry, and workitem in same
            // transaction
            session = HibernateUtil.getCurrentSession();
            autoCommit = HibernateUtil.begin(session);
            if (!current.isPending()) {
                // persist workitem
                boolean updated = false;
                for (int i = 0; i < WORKITEM_MAX_RETRY && !updated; i++) {
                    updated = persistWorkFlowData(current);

                    if (!updated) {
                        DataItemEvaluator.getInstance().reset();
                        // Need to rollback and restart transaction in order
                        // to retry persisting the workflow data. Hibernate
                        // requires this as the session is left in a
                        // broken state
                        if (HibernateUtil.isActiveTransaction()) {
                            HibernateUtil.rollback(session, autoCommit);
                        }
                        session = HibernateUtil.getCurrentSession();
                        autoCommit = HibernateUtil.begin(session);
                    }
                }
                if (!updated) {
                    // throw concurrency error
                    throw new ProcessException(WorkflowRsrc.PROCESS_ERROR_UPDATING_FLOWDATA,
                            getRequestId(), getId(), current.getActivityId());
                }
            }
            if (elem != null && !current.canComplete(elem)) {
                HibernateUtil.commit(session, autoCommit);
                return;
            }
            if (current.canForward()) {
                current.persistStatus(ActivityNode.IDLE);
                m_activeNodes.remove(current.getId());
            } else {
                HibernateUtil.commit(session, autoCommit);
                return;
            }
            if (merge) {
                m_mergeSemaphore.acquire();
            }
            if (next.canStart()) {
                // Synchronized to ensure isActive does not return false when activity status is changed to arriving
                synchronized (m_activeNodes) {
                    next.persistStatus(ActivityNode.ARRIVING);
                }
            } else {
                HibernateUtil.commit(session, autoCommit);
                current.logAction(AFLogEventAction.WORKFLOW_FORWARDED);
                return;
            }
            if (next instanceof ProvisionActivity) {
                // set the category
                setResourceType(((ProvisionActivity) next).getCategory());
            }
            HibernateUtil.commit(session, autoCommit);
        } catch (Exception ex) {
            current.logAction(AFLogEventAction.WORKFLOW_ERROR);
            try {
                if (HibernateUtil.isActiveTransaction()) {
                    HibernateUtil.rollback(session, autoCommit);
                }
            } catch (PersistenceException he) {
                LOGGER.error(WorkflowRsrc.BUNDLE_ID, WorkflowRsrc.ACTIVITY_FORWARDING_ERROR,
                        current.getActivityId(), next.getActivityId(), he);
            } finally {
                HibernateUtil.closeSession(session, autoCommit);
            }
            // Wrap PersistenceException as an ActivityPersistenceException.
            // This will be rethrown and handled
            // in startActivity and RunnableActivity.
            if (ex instanceof PersistenceException) {
                throw new ActivityPersistenceException(WorkflowRsrc.ACTIVITY_FORWARDING_ERROR, ex,
                        current.getActivityId(), next.getActivityId());
            } else if (ex instanceof ProcessException) {
                // Rethrow ProcessException. May include
                // DataItemPersistenceException or
                // ProcessPersistenceException
                throw (ProcessException) ex;
            } else {
                throw new ProcessException(ex, WorkflowRsrc.ACTIVITY_FORWARDING_ERROR,
                        current.getActivityId(), next.getActivityId());
            }
        } finally {
            if (merge) {
                m_mergeSemaphore.release();
            }
        }
        // Check to see if the process is still running
        if (isRunning()) {
            boolean isNewThread = false;
            // If the current activity is a user activity ALWAYS start the next
            // activity in a new thread regardless of its type.
            if (current instanceof UserActivity) {
                isNewThread = true;
            }
            current.logAction(AFLogEventAction.WORKFLOW_FORWARDED);
            startActivity(next, isNewThread);
        } else if (isTerminated()) {
            resumeTermination();
        }
    }

    /**
     * Handles retry processing for persistence exceptions.
     *
     * @param ape the ActivityPersistenceException
     * @param act the ActivityNode that will be retried
     */
    public void handleActivityPersistenceException(ActivityPersistenceException ape, ActivityNode act)
    {
        if (isRunning()) {
            int maxRetry = getEngine().getMaximumRetryCount();
            if (maxRetry == -1 || (act.getFailureCount() < maxRetry)) {
                act.incrementFailureCount();
                getEngine().addActivityToRetryQueue(act);
                LOGGER.error(WorkflowRsrc.BUNDLE_ID, WorkflowRsrc.ERROR_PERSIST_ACTIVITY_STATE,
                        getRequestId(), getId(), act.getActivityId(), ape);
            } else {
                LOGGER.error(WorkflowRsrc.BUNDLE_ID, WorkflowRsrc.ERROR_PERSIST_ACTIVITY_STATE2,
                        getRequestId(), getId(), act.getActivityId(), ape);
            }
        }
    }

    /**
     * Restarts an user activity that was marked pending by another machine in
     * the cluster.
     *
     * @param actInfo the activity status info
     * @throws ProcessException on error restarting pending activity
     */
    public void restartPendingActivity(ActivityStatusInfo actInfo)
            throws ProcessException
    {
        String actId = actInfo.getActivityId();
        ActivityNode act = createActivity(m_model.getActivity(actId));
        if (null != act) {
            m_activeNodes.put(actId, act);
            act.setActivityStatusInfo(actInfo);
            /*ActivityNode next =*/
            act.getNextActivity();
            forward(act, act.getNextActivity(), null);
        }

    }

    /**
     * Roll back activity status updates
     *
     * @param act the activity node
     * @throws ActivityException            on error updating activity
     * @throws ActivityPersistenceException on error rolling back
     *         
     */
    public void rollbackActivityStatus(ActivityNode act)
            throws ActivityException, ActivityPersistenceException
    {
        // roll back the status
        Session session = null;
        boolean autoCommit = false;
        try {
            session = HibernateUtil.getCurrentSession();
            autoCommit = HibernateUtil.begin(session);
            act.setStatus(ActivityNode.IDLE);
            act.persistStatus(ActivityNode.ARRIVING);
            if (autoCommit) {
                HibernateUtil.commit(session, autoCommit);
            }
        } catch (PersistenceException ex) {
            throw new ActivityPersistenceException(WorkflowRsrc.ACTIVITY_PERSIST_ERROR, ex, act.getActivityId());
        } finally {
            try {
                if (autoCommit && HibernateUtil.isActiveTransaction()) {
                    HibernateUtil.rollback(session, autoCommit);
                }
            } catch (PersistenceException he) {
                LOGGER.error(WorkflowRsrc.BUNDLE_ID, WorkflowRsrc.ACTIVITY_PERSIST_ERROR, act.getActivityId(), he);
            } finally {
                HibernateUtil.closeSession(session, autoCommit);
            }
        }
    }

    /**
     * Start an activity that was marked pending by another machine in the
     * cluster.
     *
     * @param activity  the activity node
     * @param newThread true if new thread required to start activity
     * @throws ProcessException on error starting activity
     */
    public void startActivity(ActivityNode activity, boolean newThread)
            throws ProcessException
    {
        try {
            if (newThread) {
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("startActivity() activity=" + activity + ", newThread=" + newThread + ", pooled = true");
                }
                // Execute activity in a new thread
                getEngine().getExecutor().execute(new RunnableActivity(activity));
            } else {
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("startActivity() activity=" + activity + ", newThread=" + newThread + ", pooled = false");
                }
                // Execute activity in the current thread
                activity.notifyArrive();
            }
        } catch (ActivityPersistenceException ex) {
            // Retry activity
            handleActivityPersistenceException(ex, activity);
        }
    }

    /**
     * Restarts a failed activity.
     *
     * @param act failed activity
     */
    public void restartFailedActivity(ActivityNode act)
    {
        Session session = null;
        boolean autoCommit = true;
        try {
            session = HibernateUtil.getCurrentSession();
            autoCommit = HibernateUtil.begin(session);
            if (isBoundToEngine()) {
                String actId = act.getActivityId();
                ActivityStatusInfo actInfo = AfActivityDAO.findById(new AfActivityId(getRequestId(), actId));
                if (null != actInfo) {
                    act.setActivityStatusInfo(actInfo);
                } else {
                    LOGGER.error(WorkflowRsrc.BUNDLE_ID, WorkflowRsrc.ERROR_RESTARTING_ACTIVITY,
                            getRequestId(), getId(), act.getActivityId());
                    try {
                        terminate(ProcessConstants.ERROR);
                    } catch (ProcessException pe) {
                        // log error
                        LOGGER.error(WorkflowRsrc.BUNDLE_ID, WorkflowRsrc.ERROR_TERMINATING_PROCESS,
                                getRequestId(), getId(), pe);
                    }
                }
                rollbackActivityStatus(act);
                m_activeNodes.put(actId, act);
            } else {
                // no longer bound to this engine, don't restart
                return;
            }
            HibernateUtil.commit(session, autoCommit);
            getEngine().getExecutor().execute(new RunnableActivity(act));
        } catch (ActivityPersistenceException ex) {
            handleActivityPersistenceException(ex, act);
        } catch (ActivityException ex) {
            LOGGER.error(WorkflowRsrc.BUNDLE_ID, WorkflowRsrc.ERROR_RESTARTING_ACTIVITY,
                    getRequestId(), getId(), act.getActivityId(), ex);
            try {
                terminate(ProcessConstants.ERROR);
            } catch (ProcessException pe) {
                LOGGER.error(WorkflowRsrc.BUNDLE_ID, WorkflowRsrc.ERROR_TERMINATING_PROCESS,
                        getRequestId(), getId(), pe);
            }
        } catch (ProcessException ex) {
            handleActivityPersistenceException(new ActivityPersistenceException(WorkflowRsrc.ACTIVITY_PERSIST_ERROR, ex, act.getActivityId()), act);
        } catch (PersistenceException ex) {
            handleActivityPersistenceException(new ActivityPersistenceException(WorkflowRsrc.ACTIVITY_PERSIST_ERROR, ex, act.getActivityId()), act);
        } finally {
            try {
                if (HibernateUtil.isActiveTransaction()) {
                    HibernateUtil.rollback(session, autoCommit);
                }
            } catch (PersistenceException he) {
                handleActivityPersistenceException(new ActivityPersistenceException(WorkflowRsrc.ACTIVITY_PERSIST_ERROR, he,
                        act.getActivityId()), act);
            } finally {
                HibernateUtil.closeSession(session, autoCommit);
            }
        }
        LOGGER.info(WorkflowRsrc.BUNDLE_ID, WorkflowRsrc.RETRYING_ACTIVITY,
                getRequestId(), getId(), act.getActivityId());
    }

    /**
     * Returns the specified activity.
     *
     * @param id id of the activity
     * @return the activity; null if it does not exist
     * @throws ActivityException if activity cannot be found or created, or if the process has
     *                           been terminated.
     */
    public synchronized ActivityNode getActivity(String id)
            throws ActivityException
    {
        ActivityNode act = m_activeNodes.get(id);
        if (isTerminated()) {
            throw new ActivityException(WorkflowRsrc.PROCESS_TERMINATED_ERROR, getRequestId(), getId());
        } else {
            if (null == act && null != id) {
                synchronized (m_activeNodes) {
                    act = loadActivity(id);
                    m_activeNodes.put(id, act);
                }
            }
        }
        return act;
    }

    /**
     * Removes the specified activity from the active nodes map.
     *
     * @param id the activity id
     */
    public void removeActivity(String id)
    {
        m_activeNodes.remove(id);
    }

    private ActivityNode createActivity(ActivityBean bean)
            throws ActivityException
    {
        ActivityNode act = null;

        try {
            if (bean instanceof UserActivityBean) {
                act = new UserActivity(this, (UserActivityBean) bean);
            } else if (bean instanceof ProvisionActivityBean) {
                String provisionActivityClassName = EboConfig.getInstance().
                getProperty("WorkflowService/ProvisionActivityName", "com.novell.soa.af.impl.activity.ProvisionActivity");
                act = (ActivityNode) Class.forName(provisionActivityClassName, true, Thread.currentThread().getContextClassLoader()).newInstance();
                act.initialize(this, bean);
                //act = new ProvisionActivity(this, (ProvisionActivityBean) bean);
            } else if (bean instanceof StartActivityBean) {
                act = new StartActivity(this, ((StartActivityBean) bean));
            } else if (bean instanceof FinishActivityBean) {
                act = new FinishActivity(this, (FinishActivityBean) bean);
            } else if (bean instanceof BranchActivityBean) {
                act = new BranchActivity(this, (BranchActivityBean) bean);
            } else if (bean instanceof ConditionActivityBean) {
                act = new ConditionActivity(this, (ConditionActivityBean) bean);
            } else if (bean instanceof LogActivityBean) {
                act = new LogActivity(this, (LogActivityBean) bean);
            } else if (bean instanceof MappingActivityBean) {
                act = new MappingActivity(this, (MappingActivityBean) bean);
            } else if (bean instanceof MergeActivityBean) {
                act = new MergeActivity(this, (MergeActivityBean) bean);
            } else if (bean instanceof IntegrationActivityBean) {
                act = new IntegrationActivity(this, (IntegrationActivityBean) bean);
            } else if (bean instanceof NotificationActivityBean) {
                act = new NotificationActivity(this, (NotificationActivityBean) bean);
            } else if (bean instanceof RoleBindingActivityBean) {
                String roleBindingActivityClassName = EboConfig.getInstance().
                getProperty("WorkflowService/RoleBindingActivityName", "com.novell.soa.af.impl.activity.RoleBindingActivity");
                act = (ActivityNode) Class.forName(roleBindingActivityClassName, true, Thread.currentThread().getContextClassLoader()).newInstance();
                act.initialize(this, bean);
                //act = new RoleBindingActivity(this, (RoleBindingActivityBean) bean);
            } else if (bean instanceof ResourceStatusBindingActivityBean) {
                String resourceStatusBindingActivityClassName = EboConfig.getInstance().
                getProperty("WorkflowService/ResourceStatusBindingActivityName", "com.novell.soa.af.impl.activity.ResourceStatusBindingActivity");
                act =
                    (ActivityNode) Class.forName(
                        resourceStatusBindingActivityClassName, true, Thread.currentThread().getContextClassLoader()).newInstance();
                act.initialize(this, bean);
                //act = new ResourceStatusBindingActivity(this, (ResourceStatusBindingActivityBean) bean);
            } else if (bean instanceof StartCorrelatedFlowActivityBean) {
                act = new StartCorrelatedFlowActivity(this, (StartCorrelatedFlowActivityBean) bean);
            } else if (bean instanceof RoleRequestActivityBean) {
                String roleRequestActivityClassName = EboConfig.getInstance().
                getProperty("WorkflowService/RoleRequestActivityName", "com.novell.soa.af.impl.activity.RoleRequestActivity");
                act = (ActivityNode) Class.forName(roleRequestActivityClassName, true, Thread.currentThread().getContextClassLoader()).newInstance();
                act.initialize(this, bean);
                //act = new RoleRequestActivity(this, (RoleRequestActivityBean) bean);
            } else if (bean instanceof ResourceRequestActivityBean) {
                String resourceRequestActivityClassName = EboConfig.getInstance().
                getProperty("WorkflowService/ResourceRequestActivityName", "com.novell.soa.af.impl.activity.ResourceRequestActivity");
                act = (ActivityNode) Class.forName(resourceRequestActivityClassName, true, Thread.currentThread().getContextClassLoader()).newInstance();
                act.initialize(this, bean);
                //act = new RoleRequestActivity(this, (RoleRequestActivityBean) bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ActivityException(
                WorkflowRsrc.ACTIVITY_ERROR_LOADING_ACTIVITY, e, bean.getId(), getRequestId());
        }

        return act;
    }

    void loadActivities()
            throws ProcessException
    {
        synchronized (m_activeNodes) {
            try {
                ActivityStatusInfo activityQBE = new ActivityStatusInfo();
                activityQBE.setRequestId(getRequestId());
                List<ActivityStatusInfo> actList = AfActivityDAO.findByExample(activityQBE);
                for (ActivityStatusInfo actInfo : actList) {
                    String actId = actInfo.getActivityId();
                    ActivityNode act = createActivity(m_model.getActivity(actId));
                    if (null != act) {
                        act.setActivityStatusInfo(actInfo);
                    }
                    m_activeNodes.put(actId, act);
                }
            } catch (PersistenceException se) {
                LOGGER.debug("loadActivities failed", se);
                throw new ProcessPersistenceException(WorkflowRsrc.PROCESS_ERROR_LOADING_ACTIVITIES, se,
                        getRequestId(), getId());
            }
        }
    }

    private ActivityNode loadActivity(String id)
            throws ActivityException
    {
        ActivityNode act = null;
        try {
            AfActivityId activityId = new AfActivityId(getRequestId(), id);
            ActivityStatusInfo actInfo = AfActivityDAO.findById(activityId);
            act = createActivity(m_model.getActivity(id));
            if (null != act && null != actInfo) {
                act.setActivityStatusInfo(actInfo);
            }
        } catch (PersistenceException ex) {
            LOGGER.warn(WorkflowRsrc.BUNDLE_ID, WorkflowRsrc.GETWORKENTRIES_FAILED, ex);
            throw new ActivityPersistenceException(WorkflowRsrc.ACTIVITY_ERROR_LOADING_ACTIVITY, ex, id, getRequestId());
        }
        return act;
    }

    /**
     * Returns true if the process is active; false otherwise.
     *
     * @return true if non-user activites are arriving, running, or departing
     *         and user activities are arriving or departing.
     */
    public boolean isActive()
    {
        boolean isActive = false;
        synchronized (m_activeNodes) {
            Object[] activities = m_activeNodes.values().toArray();
            for (int i = 0; i < activities.length; i++) {
                ActivityNode act = (ActivityNode) activities[i];
                if (act.isActive()) {
                    isActive = true;
                    break;
                }
            }
        }
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Process " + getRequestId() + " isActive() = " + isActive);
        }
        return isActive;
    }

    /**
     * This method is used to resume termination of processes. There are three
     * possible ways for a process to end up in a partially terminated state: 1)
     * The terminate method was called on an engine in a cluster that the
     * process is not bound to. A timer task on the correct engine will call
     * this method. 2) There were running activities when terminate was called.
     * When the activities complete they will call this method. 3) The
     * termination did not fully complete. On engine startup, the process
     * restart thread will call this method.
     *
     * @throws ProcessException on error terminating process
     */
    public synchronized void resumeTermination()
            throws ProcessException
    {
        String requestId = getRequestId();
        try {
            // If the completion time has been set there's nothing to do.
            if (getCompletionTime() == null) {
                // set the in memory state to TERMINATED
                // currently executing activites should stop executing
                setProcessStatus(ProcessConstants.TERMINATED);
                // remove process instance from memory
                getEngine().removeProcess(requestId);
                // If current activites are done executing cleanup the state
                // If not the forward mechanism should call this method before
                // starting another activity
                if (!isActive()) {
                    cleanup();
                }
            }
        } catch (ProcessException ex) {
            throw new ProcessException(ex, WorkflowRsrc.PROCESS_ERROR_PERSISTING_PROCESS_STATE,
                    requestId, getId());
        }
    }

    private void cleanup()
            throws ProcessException
    {
        try {
            // cleanup related tables outside transaction
            WorkTaskDAO.deleteForRequestId(m_requestId);
            QuorumDAO.deleteForRequestId(m_requestId);
            AfActivityDAO.deleteByRequestId(m_requestId);
            AfBranchDAO.deleteByRequestId(m_requestId);
            //Moved to expired process timer
            //WorkFlowDataDAO.deleteByRequestId(requestId);
            AfActivityTimerTasksDAO.deleteByRequestId(m_requestId);
            // Once completion time is set and persisted the process
            // cleanup will never be tried again
            setCompletionTime(new Timestamp(System.currentTimeMillis()));
            ProcessDAO.saveOrUpdate(this);
        } catch (PersistenceException ex) {
            throw new ProcessException(ex, WorkflowRsrc.PROCESS_ERROR_PERSISTING_PROCESS_STATE,
                    m_requestId, getId());
        }
    }

    /**
     * Sets the entitlement result.
     *
     * @param correlationId     -
     *                          correlation ID associated with the entitlement
     * @param entitlementState  -
     *                          entitlement state (Granted, Revoked)
     * @param entitlementStatus -
     *                          entitlement status (Success, Warning, Error, Fatal)
     * @param message           -
     *                          message associated with the entitlement result
     * @return DN - target user DN for provisioning result
     * @throws ProcessException -
     *                          thrown when record can not be stored
     */
    final String setResult(String correlationId, int entitlementState, int entitlementStatus, String message)
            throws ProcessException
    {
        try {
            String provisioningDN = null;
            // Must find the first provissioning record in the table to get
            // appropriate info to create the
            // new provisioning status record with the same correlation ID.
            // If record not found then skip inserting new record
            IProvisioningStatus originalProvStatus = null;
            List<IProvisioningStatus> results = ProvisioningStatusDAO.getProvisioningStatusByCorrelationId(correlationId,
                    IEntitlement.ENTITLEMENT_SUBMITTED);
            if (results != null && !results.isEmpty()) {
                originalProvStatus = results.get(0);
                ProvisioningStatusId statusRecordId = new ProvisioningStatusId(originalProvStatus.getRequestId(), originalProvStatus.getActivityId(),
                        UUID.generate().toString());
                ProvisioningStatus newProvStatus = new ProvisioningStatus(statusRecordId, originalProvStatus.getRecipient(), originalProvStatus
                        .getCorrelationId(), originalProvStatus.getProvisioningTime(), new Date(System.currentTimeMillis()), entitlementState,
                        entitlementStatus, message);
                ProvisioningStatusDAO.saveOrUpdate(newProvStatus);
                provisioningDN = originalProvStatus.getRecipient();
            }
            // generate user comment
            if (isGenerateCommentsEnabled()) {
                String targetDN = provisioningDN == null ? m_recipient : provisioningDN;
                String recipientName;
                IIDXUtil idx = EngineImpl.getEngine().getIDXUtil();
                if (idx.isGroup(provisioningDN)) {
                    recipientName = idx.resolveGroupName(targetDN);
                } else {
                    recipientName = idx.resolveUserName(targetDN);
                }
                AFCommentId msgId;
                if (entitlementStatus == IEntitlement.ENTITLEMENT_SUCCESS || entitlementStatus == IEntitlement.ENTITLEMENT_WARNING) {
                    if (entitlementState == IEntitlement.ENTITLEMENT_GRANTED) {
                        msgId = AFCommentId.PROVISION_TASK_RESULT_SUCCESS_GRANT;
                    } else if (entitlementState == IEntitlement.ENTITLEMENT_REVOKED) {
                        msgId = AFCommentId.PROVISION_TASK_RESULT_SUCCESS_REVOKE;
                    } else {
                        msgId = AFCommentId.PROVISION_TASK_RESULT_SUCCESS_NA;
                    }
                } else if (entitlementStatus == IEntitlement.ENTITLEMENT_SUBMITTED) {
                    if (entitlementState == IEntitlement.ENTITLEMENT_GRANTED) {
                        msgId = AFCommentId.PROVISION_TASK_RESULT_SUBMITTED_GRANT;
                    } else if (entitlementState == IEntitlement.ENTITLEMENT_REVOKED) {
                        msgId = AFCommentId.PROVISION_TASK_RESULT_SUBMITTED_REVOKE;
                    } else {
                        msgId = AFCommentId.PROVISION_TASK_RESULT_SUBMITTED_NA;
                    }
                } else {
                    if (entitlementState == IEntitlement.ENTITLEMENT_GRANTED) {
                        msgId = AFCommentId.PROVISION_TASK_RESULT_FAILURE_GRANT;
                    } else if (entitlementState == IEntitlement.ENTITLEMENT_REVOKED) {
                        msgId = AFCommentId.PROVISION_TASK_RESULT_FAILURE_REVOKE;
                    } else {
                        msgId = AFCommentId.PROVISION_TASK_RESULT_FAILURE_NA;
                    }
                }
                CommentDAO.createUserComment(getRequestId(), originalProvStatus.getActivityId(), msgId, recipientName);
            }
            return provisioningDN == null ? m_recipient : provisioningDN;
        } catch (PersistenceException ex) {
            throw new ProcessException(ex, WorkflowRsrc.PROCESS_ERROR_PERSISTING_PROCESS_STATE,
                    m_requestId, getId());
        }
    }

    /**
     * Return comments for the specified activity.
     *
     * @param activityId id of the activity
     * @return the list of <code>IComment</code> objects
     * @throws ProcessException if there is an error getting comments
     */
    public final List<IComment> getCommentsByActivity(String activityId)
            throws ProcessException
    {
        try {
            return CommentDAO.getCommentsByActivityForRequest(getRequestId(), activityId, NO_ROW_LIMIT);
        } catch (PersistenceException ex) {
            LOGGER.debug("getComments failed", ex);
            throw new ProcessPersistenceException(WorkflowRsrc.PROCESS_ERROR_LOADING_COMMENTS, ex,
                    getRequestId(), getProcessId());
        }
    }

    /**
     * Return comments made by the specified user.
     *
     * @param user the user
     * @return the list of <code>IComment</code> objects
     * @throws ProcessException if there is an error getting comments
     */
    public final List<IComment> getCommentsByUser(String user)
            throws ProcessException
    {
        try {
            return CommentDAO.getCommentsByAuthorForRequest(getRequestId(), user, NO_ROW_LIMIT);
        } catch (PersistenceException ex) {
            LOGGER.debug("getComments failed", ex);
            throw new ProcessPersistenceException(WorkflowRsrc.PROCESS_ERROR_LOADING_COMMENTS, ex,
                    getRequestId(), getProcessId());
        }
    }

    /**
     * Return comments by comment type.
     *
     * @param type the type of comment: USER | SYSTEM
     * @return the list of <code>IComment</code> objects
     * @throws ProcessException if there is an error getting comments
     */
    public final List<IComment> getCommentsByType(String type)
            throws ProcessException
    {
        try {
            return CommentDAO.getCommentsByTypeForRequest(getRequestId(), type, NO_ROW_LIMIT);
        } catch (PersistenceException ex) {
            LOGGER.debug("getComments failed", ex);
            throw new ProcessPersistenceException(WorkflowRsrc.PROCESS_ERROR_LOADING_COMMENTS, ex,
                    getRequestId(), getProcessId());
        }
    }

    /**
     * Return comments based on creation time for this request instance.
     *
     * @param creationTime the time the comment was created
     * @param op           the where clause operator. see <code>ICommentQuery</code> for
     *                     possible values of this parameter.
     * @return the list of <code>IComment</code> objects
     * @throws ProcessException if there is an error getting comments
     * @see ICommentQuery
     */
    public final List<IComment> getCommentsByCreationTime(long creationTime, int op)
            throws ProcessException
    {
        try {
            return CommentDAO.getCommentsByTimestampForRequest(getRequestId(), creationTime, op, NO_ROW_LIMIT);
        } catch (PersistenceException ex) {
            LOGGER.debug("getComments failed", ex);
            throw new ProcessPersistenceException(WorkflowRsrc.PROCESS_ERROR_LOADING_COMMENTS, ex,
                    getRequestId(), getProcessId());
        }
    }

    /**
     * Queries the comments and returns a list of <code>IComment</code>
     * objects that satisfy the query.
     *
     * @param query      the <code>ICommentQuery</code>
     * @param maxRecords the maximum number of records to retrieve
     * @return the list of <code>IComment</code> objects
     * @throws ProcessException if there is an error getting comments
     */
    public final List<IComment> getComments(ICommentQuery query, int maxRecords)
            throws ProcessException
    {
        try {
            return CommentDAO.getCommentsByQueryForRequest(query, getRequestId());
        } catch (PersistenceException ex) {
            LOGGER.debug("getComments failed", ex);
            throw new ProcessPersistenceException(WorkflowRsrc.PROCESS_ERROR_LOADING_COMMENTS, ex,
                    getRequestId(), getProcessId());
        }
    }

    /**
     * Create an object to query comments.
     *
     * @return an instance of the <code>ICommentQuery</code> interface
     */
    public ICommentQuery createCommentQuery()
    {
        return new CommentQueryImpl();
    }

    /**
     * cancel all timed activities used when a process completees or is
     * terminated
     */
    private void cancelTimedActivities()
    {
        synchronized (m_activeNodes) {
            try {
                for (ActivityNode act : m_activeNodes.values()) {
                    if (act instanceof ITimedActivity) {
                        getEngine().getActivityTimer().cancel((ITimedActivity) act, -1);
                    }
                }
            } catch (ActivityTimerException e) {
                LOGGER.warn(e);
            }
        }
    }

    /**
     * Get the row count for this request's provisioning statuses
     *
     * @return number of rows
     */
    public final long getProvisioningStatusRowCount()
    {
        try {
            return ProvisioningStatusDAO.getRowCount(getRequestId());
        } catch (PersistenceException e) {
            // if an exception occurs, log it and return 0
            LOGGER.debug(e);
            return 0;
        }
    }

    /**
     * Get an <code>IProvisioningStatusQuery</code>
     *
     * @param maxRows maximum # of rows in result set
     * @return List of <code>IProvisioningStatus</code> objects
     */
    public final List<IProvisioningStatus> getProvisioningStatus(int maxRows)
    {
        IProvisioningStatusQuery query = new ProvisioningStatusQueryImpl();
        query.setWhere(query.whereRequestId(getRequestId()));
        // TODO change query order per UI group's requirement
        query.orderByProvisioningTime(true);
        return getProvisioningStatus(query, maxRows);
    }

    /**
     * Performs the Provisioning Status Query returning the results in a
     * <code>List</code> of <code>IProvisioningStatus</code>
     *
     * @param q       -
     *                the <code>IProvisioningStatusQuery</code>
     * @param maxrows =
     *                maximum number of rows to return
     * @return List of <code>IProvisioningStatus</code> objects
     */
    public final List<IProvisioningStatus> getProvisioningStatus(IProvisioningStatusQuery q, int maxrows)
    {
        List<IProvisioningStatus> results;
        try {
            results = ProvisioningStatusDAO.getProvisioningStatus(q, maxrows);
        } catch (PersistenceException e) {
            // if an exception occurs, log it and return an empty list
            LOGGER.debug(e);
            results = new ArrayList<IProvisioningStatus>();
        }
        return results;
    }

    /**
     * This is a convenience method that returns the rows in a predefined sort
     * order. The ui group will let us know what order they want the rows in.
     *
     * @return List of <code>IProvisioningStatus</code> objects
     */
    public IProvisioningStatusQuery createProvisioningStatusQuery()
    {
        return new ProvisioningStatusQueryImpl();
    }

    private Date newDate(Date date)
    {
        if (date != null) {
            return new Date(date.getTime());
        } else {
            return null;
        }
    }
}

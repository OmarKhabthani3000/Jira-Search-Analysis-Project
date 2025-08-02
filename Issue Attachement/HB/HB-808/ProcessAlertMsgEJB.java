package mil.navy.med.nmcsd.rules.ejb;

import java.util.ArrayList;
import java.util.List;
import java.rmi.RemoteException;

import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import mil.navy.med.nmcsd.logging.Logger;
import mil.navy.med.nmcsd.logging.LogManager;

/*
import mil.navy.med.nmcsd.dbaccess.DBAccessFactory;
import mil.navy.med.nmcsd.dbaccess.Database;
import mil.navy.med.nmcsd.dbaccess.Query;
import mil.navy.med.nmcsd.dbaccess.QueryResults;
import mil.navy.med.nmcsd.dbaccess.DBAccessException;
*/

import java.io.File;
import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.Query;
import net.sf.hibernate.HibernateException;

import mil.navy.med.nmcsd.icdb.model.pophealth.Alert;

/**
 * Each message is a new alert to process.
 */
public class ProcessAlertMsgEJB
        implements MessageDrivenBean, MessageListener
{
    /** Context. */
    private transient MessageDrivenContext mdc = null;

    /** Logger. */
    private static Logger logger
        = LogManager.getInstance().getLogger(ProcessAlertMsgEJB.class);

    /** Hibernate SessionFactory. */
    private static SessionFactory sf = null;

    /** Insert operation. */
    public static final int OP_INSERT = 1;

    /** Update operation. */
    public static final int OP_UPDATE = 2;

    /**
     * Default constructor.
     */
    public ProcessAlertMsgEJB()
    {
    }

    /**
     * Implement MessageListener.
     */
    public void onMessage(Message inMessage)
    {
        ObjectMessage msg = null;
        Alert alert;

        logger.debug("Received message.");

        try
        {
            if (inMessage instanceof ObjectMessage)
            {
                msg = (ObjectMessage) inMessage;
                alert = (Alert) msg.getObject();

                //Add alert to the database
                persistAlert(alert);
            }
            else
            {
                logger.error("Message of wrong type: " 
                    + inMessage.getClass().getName());
            }
        }
        catch (JMSException jmse)
        {
            logger.error("Messaging error.", jmse);
            //mdc.setRollbackOnly();
        }
        catch (ClassCastException cce)
        {
            logger.error("Wrong object in message.", cce);
        }
        catch (Throwable te)
        {
            logger.error("Error processing message.", te);
        }

    }

    //Implement MessageDrivenBean
    public void setMessageDrivenContext(MessageDrivenContext mdc)
    {
        this.mdc = mdc;
    }

    //Needed by the container
    public void ejbCreate()
    {
    }

    //Implement MessageDrivenBean
    public void ejbRemove()
    {
    }

    /**
     * Add alert to the database.  Overwrite current alert if one exits.
     */
/*
    private void persistCastorAlert(Alert alert)
    {
        Database db = null;
        int operation = 0;

        try
        {
            db = DBAccessFactory.getDatabase("pophealth");

            db.begin();

            //Get current alert
            Query query = db.getQuery("SELECT a FROM "
                + "mil.navy.med.nmcsd.icdb.model.pophealth.Alert a "
                + "WHERE a.patID = $1 " //patient ID
                + "AND a.diseaseID = $2 "  //specify diesase type
                + "AND a.status = $3 " //specify status
                + "AND a.type = $4 ");  //specify alert type
            query.bind(alert.getPatID());
            query.bind(alert.getDiseaseID());
            query.bind(alert.getStatus());
            query.bind(alert.getType());

            QueryResults results = query.execute();

            if (results.hasMore())
            {
                //Update existing alert
                Alert a = (Alert) results.next();
                a.setLevel(alert.getLevel());
                a.setDescription(alert.getDescription());
                a.setDate(alert.getDate());

                operation = OP_UPDATE;
            }
            else
            {
                //Add new alert
                db.create(alert);

                operation = OP_INSERT;
            }

            db.commit();
        }
        catch (DBAccessException dbae)
        {
            logger.error("Error persisting alert for patient: "
                + alert.getPatID(), dbae);
        }
        finally
        {
            if (db != null)
            {
                try
                {
                    db.close();
                }
                catch (DBAccessException dbae)
                {
                    logger.error("Error closing alert for patient: "
                        + alert.getPatID(), dbae);
                }
            }
        }

        switch (operation)
        {
            case OP_INSERT:
                logger.debug("A new alert was added to the database.");
                break;
            case OP_UPDATE:
                logger.debug("An existing alert was updated in the database.");
                break;
        }

    } //persistAlert()
*/

    private void persistAlert(Alert alert)
    {
        Session sess = null;
        Transaction tx = null;
        int operation = 0;

        try
        {
            if (sf == null)
            {
                sf = new Configuration()
                    .configure(new File("hib_pophealth.xml"))
                    .buildSessionFactory();
            }
            sess = sf.openSession();
            tx = sess.beginTransaction();
    
            //Get current alert
            Query query = sess.createQuery(
                "from mil.navy.med.nmcsd.icdb.model.pophealth.Alert alert "
                + "where alert.patID = :patID " //patient ID
                + "and alert.diseaseID = :diseaseID "  //specify diesase type
                + "and alert.status = :status " //specify status
                + "and alert.type = :type ");  //specify alert type
            /*
            Query query = sess.getNamedQuery("pophealth.Alert.by.patID.diseaseID.status.type");
            */
            query.setInteger("patID", alert.getPatID());
            query.setInteger("diseaseID", alert.getDiseaseID());
            query.setString("status", alert.getStatus());
            query.setInteger("type", alert.getType());

            List results = query.list();

            if (!results.isEmpty())
            {
                //Update existing alert
                Alert a = (Alert) results.get(0);
                a.setLevel(alert.getLevel());
                a.setDescription(alert.getDescription());
                a.setDate(alert.getDate());

                operation = OP_UPDATE;
            }
            else
            {
                //Add new alert
                sess.save(alert);

                operation = OP_INSERT;
            }

            tx.commit();
        }
        catch (HibernateException he)
        {
            logger.error("Error persisting alert for patient: "
                + alert.getPatID(), he);
            if (tx != null)
            {
                try
                {
                    tx.rollback();
                }
                catch (HibernateException he2)
                {
                    logger.error("Error rolling back alert for patient: "
                        + alert.getPatID(), he2);
                }
            }
        }
        finally
        {
            if (sess != null)
            {
                try
                {
                    sess.close();
                }
                catch (HibernateException he)
                {
                    logger.error("Error closing alert for patient: "
                        + alert.getPatID(), he);
                }
            }
        }

        switch (operation)
        {
            case OP_INSERT:
                logger.debug("A new alert was added to the database.");
                break;
            case OP_UPDATE:
                logger.debug("An existing alert was updated in the database.");
                break;
        }

    }

} //ProcessAlertMsgEJB

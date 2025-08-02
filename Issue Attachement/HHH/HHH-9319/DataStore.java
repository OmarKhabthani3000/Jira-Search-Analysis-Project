

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Session;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.*;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;


@Stateless
@Local(IDataStore.class)
public class DataStore implements IDataStore,Serializable
{
    private static final Logger LOGGER = Logger.getLogger(DataStore.class.getName());


    @PersistenceContext(unitName ="DAL-connector-persistence")
    private EntityManager manager;

    /** must be a multiple of 3 ! */
    public static final int CHUNKSIZE = 500001;



    /**
     * Encodes b64 and persists the content of the specified file
     * in LOBDATA table
     *
     * @param path	 absolute path of the file
     * @param filename file name
     * @return LobDataEntity instance
     * @throws java.io.FileNotFoundException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<Long> persistLob(String path, String filename) throws FileNotFoundException
    {
        LobDataEntity l = null;
        long wrote = 0;
        long start,end = 0l;
        byte[] b64DataBuf;
        List<Long> result = new ArrayList<Long>(3);

        if (LOGGER.isInfoEnabled())
        {
            LOGGER.info("Content to be transferred: " + path + File.separator + filename);
        }

        long recsize;
        FileInputStream in = null;


        try
        {
            recsize = new File(path + File.separator + filename).length();
            l = new LobDataEntity();
            //binary format
            in = new FileInputStream(path + File.separator + filename);

            int maxNoOfBytesToRead = (int) (CHUNKSIZE < (recsize) ? CHUNKSIZE : (recsize / 3) * 3);

            byte[] buffer = new byte[maxNoOfBytesToRead];


            while (wrote < (recsize * 4 / 3))
            {
                int readBytes = in.read(buffer);
                if (wrote == 0)
                {
                    Session session = (Session) manager.getDelegate();
                    Blob newContent = Hibernate.getLobCreator(session).createBlob(new byte[]{});
                    l.setBlobData(newContent);
                    manager.persist(l);
                }

                b64DataBuf = Base64.encode(buffer, readBytes);                
                l.getBlobData().setBytes(wrote + 1, b64DataBuf);

                wrote += b64DataBuf.length;
            }
            l.setSize(wrote);
            l.setEffectiveSize(recsize);
        }
        catch (Exception ex)
        {
            //If file is not found, throw exception to connecotor, so to handle this exception
            if (ex instanceof FileNotFoundException)
            {
                throw new FileNotFoundException("File was not found!");
            }
            LOGGER.warn("Exception occurred while writing blob for update  " + path + File.separator + filename +
                    ". Details:", ex);
        }
        finally
        {
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                    if (LOGGER.isInfoEnabled())
                    {
                        LOGGER.info(
                                "Cannot close file stream for " + path + File.separator + filename + ". Details:" + e);
                    }
                }
            }
        }

        if (l != null)
	{
	    result.add(l.getLobId());
	    result.add(l.getEffectiveSize());
	    result.add(l.getSize());
	}

        return result;

    }
}

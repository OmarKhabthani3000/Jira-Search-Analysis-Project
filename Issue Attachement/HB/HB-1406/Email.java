/*
 * $Id$
 */

/**
 * @author Oliver Becker, Bella Kendel, Thomas Langfeld
 * @version $Rev$ $Date$
 */
public class Email {

    private int id;
    
    private String address;
    
    
    /**
     * @return Returns the address.
     */
    public String getAddress() {
        return address;
    }
    /**
     * @param address The address to set.
     */
    public void setAddress(String address) {
        this.address = address;
    }
    /**
     * @return Returns the id.
     */
    public int getId() {
        return id;
    }
    /**
     * @param id The id to set.
     */
    public void setId(int id) {
        this.id = id;
    }
}

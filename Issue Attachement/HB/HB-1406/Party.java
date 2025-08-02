/*
 * $Id$
 */

/**
 * @author Oliver Becker, Bella Kendel, Thomas Langfeld
 * @version $Rev$ $Date$
 */
public class Party {

    private int id;
    
    private Email email;
    
    
    /**
     * @return Returns the email.
     */
    public Email getEmail() {
        return email;
    }
    /**
     * @param email The email to set.
     */
    public void setEmail(Email email) {
        this.email = email;
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

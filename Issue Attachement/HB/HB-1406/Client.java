/*
 * $Id$
 */

/**
 * @author Oliver Becker, Bella Kendel, Thomas Langfeld
 * @version $Rev$ $Date$
 */
public class Client {

    private int id;
    
    private Person person;
    
    
    
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
    /**
     * @return Returns the person.
     */
    public Person getPerson() {
        return person;
    }
    /**
     * @param person The person to set.
     */
    public void setPerson(Person person) {
        this.person = person;
    }
}

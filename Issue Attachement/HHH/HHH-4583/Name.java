/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package foo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Name
 *
 * Encapsulating Class
 *
 * @author alan.oleary
 */
@Embeddable
public class Name implements Serializable {
    private static final long serialVersionUID = 8381969086665589013L;

	@Column(name = "FIRST_NAME", nullable = false)
    public String firstName;

	@Column(name = "LAST_NAME", nullable = false)
    public String lastName;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------
}
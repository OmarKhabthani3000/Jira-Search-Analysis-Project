package org.hibernate.test.annotations.cid;

import javax.persistence.Basic;
import javax.persistence.Embeddable;

/**
 * @author Emmanuel Bernard
 */
@Embeddable
public class Product {
	@Basic
    public String name;
}

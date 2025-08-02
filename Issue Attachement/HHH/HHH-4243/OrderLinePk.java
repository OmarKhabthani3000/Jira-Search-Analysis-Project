package org.hibernate.test.annotations.cid;


import javax.persistence.Embedded;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import java.io.Serializable;

/**
 * @author Emmanuel Bernard
 */
public class OrderLinePk implements Serializable {
	@ManyToOne
    @JoinColumn(name = "foo", nullable = false)
    public Order order;
	@Embedded
    public Product product;    
}

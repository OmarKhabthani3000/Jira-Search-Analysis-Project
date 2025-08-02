package korhner.hibernatebug;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;

/**
 * @author Steve Ebersole
 */
@Entity
public class Customer {
	@Id
	public Long id;

	
	@OneToMany( fetch = FetchType.LAZY, mappedBy = "customer" )
	@OrderColumn(name="index")
	public List<Order> orders = new ArrayList<Order>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

}
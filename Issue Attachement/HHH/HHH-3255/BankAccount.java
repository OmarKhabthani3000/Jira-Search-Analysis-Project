package gov.utrafe.lab.lazy;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

@Entity
@Table(name="TEST_BANK_ACCOUNT")
public class BankAccount {

	private Long id;
	
	private Agency agency;
	
	private String number;
	
	private static final Map<String, String> order = new HashMap<String, String>();
	
	@Transient
	public static String getOrder(String value){
		return order.get(value);
	}
	
	@Id
	@TableGenerator(name = "bankAccountGenerator", 
					table = "TABLE_SEQUENCE_GENERATOR", 
					pkColumnName = "GEN_KEY", 
					valueColumnName = "GEN_VALUE", 
					pkColumnValue = "bankaccount_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.TABLE, 
			generator = "bankAccountGenerator")
	@Column(name = "ID")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="AGENCY_ID", nullable=false)
	public Agency getAgency() {
		return agency;
	}

	public void setAgency(Agency agency) {
		this.agency = agency;
	}

	@Column(name="NUMBER")
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
	
	
	
}

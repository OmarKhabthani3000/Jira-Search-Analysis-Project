package gov.utrafe.lab.lazy;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name="TEST_AGENCY")
public class Agency {

	private Long id;
	
	private String name;
	
	private Set<BankAccount> bankAccounts = new HashSet<BankAccount>();
	
	@Id
	@TableGenerator(name = "agencyGenerator", 
			table = "TABLE_SEQUENCE_GENERATOR", 
			pkColumnName = "GEN_KEY", 
			valueColumnName = "GEN_VALUE", 
			pkColumnValue = "AGENCY_id", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.TABLE, generator="agencyGenerator")
	public Long getId(){
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@OneToMany(fetch =FetchType.LAZY, mappedBy="agency", 
			cascade=CascadeType.PERSIST)	
	public Set<BankAccount> getBankAccounts(){
		return bankAccounts;
	}

	public void setBankAccounts(Set<BankAccount> bankAccounts){
		this.bankAccounts = bankAccounts;
	}
	
	public void addBankAccounts(BankAccount bankAccount){
		this.bankAccounts.add(bankAccount);
		bankAccount.setAgency(this);
		
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}

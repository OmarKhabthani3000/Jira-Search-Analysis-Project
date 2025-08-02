package com.amin.gigaspaces.common.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Store;
import org.jboss.envers.Versioned;
import org.springframework.util.Assert;
import org.springframework.util.comparator.InvertibleComparator;

import com.amin.gigaspaces.common.util.Constants;
import com.amin.gigaspaces.common.util.NoteComparator;


@Entity
@org.hibernate.annotations.Entity(
        selectBeforeUpdate = true,
        dynamicInsert = true, dynamicUpdate = true)
@Table(name="T_CONTACT")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("Contact")
@DiscriminatorColumn(name="contactType",discriminatorType=javax.persistence.DiscriminatorType.STRING)
@Versioned
@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
public class Contact implements Serializable {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(Contact.class);

    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="C_CONTACT_ID")
    @Type(type="java.lang.Long")
    @DocumentId
    private Long id;

    @Column(name="C_EMAIL")
    @Field(index=Index.TOKENIZED, store=Store.YES)
    private String email;

    @Column(name="C_CREATEDON")
    @Type(type="java.util.Date")
    private Date createdOn;

    @Column(name="C_LASTUPDATEDON")
    @Type(type="java.util.Date")
    private Date lastUpdatedOn;

    @OneToMany(mappedBy="contact",targetEntity=com.amin.gigaspaces.common.domain.Address.class, cascade = { CascadeType.ALL}, fetch=FetchType.EAGER)
    @IndexedEmbedded
    @JoinColumn(name="C_CONTACT_ID")
    @Type(type="java.util.Set")
    @Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
    private Set<Address> addresses;

    @OneToMany(mappedBy="contact", targetEntity=com.amin.gigaspaces.common.domain.Phone.class,cascade = { CascadeType.ALL}, fetch=FetchType.EAGER)
    @IndexedEmbedded
    @JoinColumn(name="C_CONTACT_ID")
    @Type(type="java.util.Set")
    @Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
    private Set<Phone> phoneNumbers;

    @OneToMany(mappedBy="contact", targetEntity=com.amin.gigaspaces.common.domain.Note.class,cascade = { CascadeType.ALL}, fetch=FetchType.EAGER)
    @IndexedEmbedded
    @JoinColumn(name="C_CONTACT_ID")
    @Type(type="java.util.Set")
    @Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
    private Set<Note> contactNotes;

    @Column(name="C_NOTES")
    @Field(index=Index.TOKENIZED, store=Store.YES)
    private String notes;

    @Column(name="C_TRASHED")
    private boolean trashed;

    public Contact() {
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public boolean isTrashed() {
		return trashed;
	}

	public void setTrashed(boolean trashed) {
		this.trashed = trashed;
	}

	public String getEmail() {
        if (null == this.email || "".equals(this.email)) {
            return "N/A";
        }
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public Date getCreatedOn() {
        return createdOn;
    }
    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }
    public Date getLastUpdatedOn() {
        return lastUpdatedOn;
    }
    public void setLastUpdatedOn(Date lastUpdatedOn) {
        this.lastUpdatedOn = lastUpdatedOn;
    }
    public Set<Address> getAddresses() {
        return addresses;
    }
    public void setAddresses(Set<Address> addresses) {
        this.addresses = addresses;
    }
    public Set<Phone> getPhoneNumbers() {
        return phoneNumbers;
    }
    public void setPhoneNumbers(Set<Phone> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }


    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void addAddressToContact(Address address) {
        if (address == null) {
            throw new IllegalArgumentException("Address cannot be null");
        }
        if (addresses == null) {
            addresses = new HashSet<Address>();
        }
        address.setContact(this);
        addresses.add(address);
    }


    public void addPhoneToContact(Phone phone) {
        if (phone == null) {
            throw new IllegalArgumentException("Phone cannot be null");
        }
        if (phoneNumbers == null) {
            phoneNumbers = new HashSet<Phone>();
        }
        phone.setContact(this);
        phoneNumbers.add(phone);
    }


    public void removePhoneFromContact(Phone phone) {
        if (phone == null) {
            throw new IllegalArgumentException("Phone cannot be null");
        }
        if (this.phoneNumbers.contains(phone)) {
        	this.phoneNumbers.remove(phone);
        }

    }

    public void removeAddressFromContact(Address address) {
        if (address == null) {
            throw new IllegalArgumentException("Address cannot be null");
        }
        if (this.addresses.contains(address)) {
           this.addresses.remove(address);
        }
    }



    @SuppressWarnings("unchecked")
	protected List<Phone> filterPhoneNumbersByType(final String phoneType) {
    	Assert.notNull(phoneType, "Phone type cannot be null");
    	Assert.hasText(phoneType, "Phone type cannot be empty");
    	return (List<Phone>)CollectionUtils.select(this.phoneNumbers, new Predicate() {
    		public boolean evaluate(Object object) {
    			Phone phone = (Phone)object;
    			return phoneType.equals(phone.getType());
    		}
    	});
    }


    @SuppressWarnings("unchecked")
	protected List<Address> showActiveAddresses() {
    	return (List<Address>) CollectionUtils.select(this.addresses,new Predicate() {
    		public boolean evaluate(Object object) {
    			Address address = (Address)object;
    			return address.isActive();
    		}
    	});
    }

    @SuppressWarnings("unchecked")
	protected List<Address> showInactiveAddresses() {
    	return (List<Address>) CollectionUtils.select(this.addresses, new Predicate() {
    		public boolean evaluate(Object object) {
    			Address address = (Address)object;
    			return !address.isActive();
    		}
    	});
    }

	protected void displayPhonesAndAddresses(StringBuffer buf) {
		buf.append(Constants.NEW_LINE);
		buf.append("Phone Detail(s):" + Constants.NEW_LINE);
		if (null != this.getPhoneNumbers() && 0 != this.getPhoneNumbers().size()) {
			for (Phone phone:  this.getPhoneNumbers()) {
				buf.append(phone);
			}
		}
		buf.append(Constants.NEW_LINE);
		buf.append("Address Details:" + Constants.NEW_LINE );
		if (null != this.getAddresses() && 0 != this.getAddresses().size()) {
			for (Address address: this.getAddresses()) {
				buf.append(address);
			}
		}
	}


	public String getContactType() {
		return "";
	}


	public Set<Note> getContactNotes() {
		return contactNotes;
	}

	public void setContactNotes(Set<Note> contactNotes) {
		this.contactNotes = contactNotes;
	}

	@SuppressWarnings("unchecked")
	public List<Note> getNoteHistory(boolean asc) {
		if (this.contactNotes != null && this.contactNotes.size() != 0) {
			List<Note> notes = new ArrayList<Note>();
			for (Note note: this.contactNotes) {
				notes.add(note);
			}
			if (asc) {
				Collections.sort(notes, new InvertibleComparator(new NoteComparator(),true));
			} else {
				Collections.sort(notes, new NoteComparator());
			}
			return notes;
		}
		return null;
	}

	public void addNoteToContact(Note note) {
		if (this.contactNotes == null) {
			this.contactNotes = new HashSet<Note>();
		}
		note.setContact(this);
		this.contactNotes.add(note);
	}


	public void removeNoteFromContact(Note note) {
		if (this.contactNotes != null) {
			this.contactNotes.remove(note);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((createdOn == null) ? 0 : createdOn.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (trashed ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Contact other = (Contact) obj;
		if (createdOn == null) {
			if (other.createdOn != null)
				return false;
		} else if (!createdOn.equals(other.createdOn))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (trashed != other.trashed)
			return false;
		return true;
	}

}

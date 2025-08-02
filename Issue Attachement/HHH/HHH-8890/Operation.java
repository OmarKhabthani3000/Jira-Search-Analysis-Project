package hjb.jpa;

import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.IncompleteArgumentException;

import com.boxbe.pub.email.EmailAddress;
import com.socrates.webscore.acn.AECGenKey;
import com.socrates.webscore.beans.SessionBean;


/**
 * @author HJB
 *
 * The persistent class for the operation database table.
 * 
 */
@Entity
@Table(name="operation")
@NamedNativeQueries(value={
	@NamedNativeQuery(
		name="Operation.mailList",
		query="SELECT DISTINCT o.* FROM webscore.operation AS o INNER JOIN webscore.protocol AS p ON p.operation_id = o.id INNER JOIN webscore.protocol_schedule AS ps ON ps.protocol_id = p.id WHERE o.unsubscribe = false AND p.cancelled = false AND ps.submitted = false AND ps.expired = false AND LOCALTIMESTAMP >= ( ps.schedule_date - INTERVAL '1' day * ps.schedule_window ) AND ( ( ps.mailed is null AND LOCALTIMESTAMP < ( ps.schedule_date - INTERVAL '1' day * ( ( ps.schedule_window / 2 ) + ( ps.schedule_window % 2 ) ) ) ) or ( ps.reminder is null AND LOCALTIMESTAMP < ps.schedule_date ) )",
		resultClass=Operation.class
	),
	// Same thing but check for a valid license as well
	@NamedNativeQuery(
		name="Operation.NativeMailListWithValidLicense",
		query="SELECT o.* FROM webscore.operation AS o INNER JOIN webscore.protocol AS p ON p.operation_id = o.id INNER JOIN webscore.protocol_schedule AS ps ON ps.protocol_id = p.id WHERE o.unsubscribe = false AND o.surgery_id IN (SELECT surgery_id FROM webscore.license WHERE revoked = false AND CURRENT_DATE BETWEEN start_date AND expiry_date GROUP BY surgery_id) AND p.cancelled = false AND ps.submitted = false AND ps.expired = false AND LOCALTIMESTAMP >= ( ps.schedule_date - INTERVAL '1' day * ps.schedule_window ) AND ( ( ps.mailed is null AND LOCALTIMESTAMP < ( ps.schedule_date - INTERVAL '1' day * ( ( ps.schedule_window / 2 ) + ( ps.schedule_window % 2 ) ) ) ) or ( ps.reminder is null AND LOCALTIMESTAMP < ps.schedule_date ) )",
		resultClass=Operation.class
	)
})
@NamedQueries(value={
	@NamedQuery(
//		This one select all submitted and not cancelled
		name="Operation.collectProtocols",query="SELECT DISTINCT p FROM Operation o INNER JOIN o.protocols p INNER JOIN p.protocolSchedules AS ps WHERE o.id = :operationId AND ps.submitted = true AND ps.collected = false AND p.cancelled = false"
	),
	@NamedQuery(
			name="Operation.mailListWithValidLicense",
			query="SELECT DISTINCT o "
				+ "FROM Operation o " // Operations for
				+ "INNER JOIN o.protocols p "
				+ "INNER JOIN p.protocolSchedules ps "
				+ "WHERE EXISTS ("
					+ "SELECT 'found' "
					+ "FROM License l " // a license
					+ "WHERE l.surgery = o.surgery " // for the current surgery
					+ "AND l.revoked = FALSE " // which is unrevoked
					+ "AND CURRENT_DATE BETWEEN l.startDate AND l.expiryDate" // and current
				+ ") "
				+ "AND p.cancelled = FALSE " // protocols not cancelled
				+ "AND ps.submitted = FALSE AND ps.expired = FALSE " // schedules not submitted and not expired
				+ "AND CURRENT_TIMESTAMP <= ps.scheduleDate " // not after schedule date
				+ "AND ( "
					+ "( "
						+ "ps.mailed IS NULL " // the schedule isn't mailed
						+ "AND CURRENT_TIMESTAMP >= ps.mailingDate " // after mailing date
						+ "AND CURRENT_TIMESTAMP < ps.reminderDate " // before reminder date
					+ ") OR ( "
						+ "ps.reminder IS NULL " // the schedule isn't reminded
						+ "AND CURRENT_TIMESTAMP >= ps.reminderDate " // after the reminder date
					+ ") "
				+ ")"),
	@NamedQuery(name="Operation.findAllOperations",query="SELECT o FROM Operation o"),
	@NamedQuery(name="Operation.findByMailId",query="SELECT o FROM Operation o WHERE o.mailIdHigh = :mailIdHigh AND o.mailIdLow = :mailIdLow"),
	@NamedQuery(name="Operation.findByEncodedEmail",query="SELECT o FROM Operation o WHERE o.email = :email"),
})
public class Operation extends SuperEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long mailIdHigh;
	private Long mailIdLow;
	private String operationName;
	private String patientName;
	private String email;
	private Boolean unsubscribe;
	private String bounceReason;
	private Boolean subscriptionNotification;
	private Batch batch;
	private Calendar date;
	private String pass;
	private Surgery surgery;
	private Set<Protocol> protocols;

    public Operation() {
    }


	@Id
	@SequenceGenerator(name="OPERATION_ID_GENERATOR", sequenceName="OPERATION_ID_SEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="OPERATION_ID_GENERATOR")
	@Column(unique=true, nullable=false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	/**
	 * @return the mailIdHigh
	 */
	@Column(name="mail_id_high", nullable=false)
	public Long getMailIdHigh() {
		return mailIdHigh;
	}

	/**
	 * @param mailIdHigh the mailIdHigh to set
	 */
	public void setMailIdHigh(Long mailIdHigh) {
		this.mailIdHigh = mailIdHigh;
	}


	/**
	 * @return the mailIdLow
	 */
	@Column(name="mail_id_low", nullable=false)
	public Long getMailIdLow() {
		return mailIdLow;
	}

	/**
	 * @param mailIdLow the mailIdLow to set
	 */
	public void setMailIdLow(Long mailIdLow) {
		this.mailIdLow = mailIdLow;
	}


	@Transient
	public UUID getMailUuid() {
		if (getMailIdHigh() == null || getMailIdLow() == null) {
			return null;
		}
		return new UUID(getMailIdHigh(), getMailIdLow());
	}

	@Transient
	public void setMailUuid(UUID uuid) throws IOException {
		setMailIdHigh(uuid.getMostSignificantBits());
		setMailIdLow(uuid.getLeastSignificantBits());
	}

	@Column(name="operation_name", length=510, nullable=false)
	public String getOperationName() {
		return this.operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	@Transient
	public String getDecodedOperationName() {
		String decoded = "";
		try {
			decoded = AECGenKey.decryptBase64String(getOperationName());
		} catch (Exception e) {}
		return decoded;
	}

	@Transient
	public void setEncodedOperationName(String operationName) {
		setOperationName(AECGenKey.base64encodeEncryptObject(operationName));
	}


	@Column(name="patient_name", length=510, nullable=false)
	public String getPatientName() {
		return this.patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	@Transient
	public String getDecodedPatientName() {
		String decoded = "";
		try {
			decoded = AECGenKey.decryptBase64String(getPatientName());
		} catch (Exception e) {}
		return decoded;
	}

	@Transient
	public void setEncodedPatientName(String patientName) {
		setPatientName(AECGenKey.base64encodeEncryptObject(patientName));
	}


	@Column(name="email", length=510, nullable=true)
	public String getEmail() {
		return this.email;
	}

	@Transient
	public static boolean isValidOperationEmailAddress(String emailAddress) { // Tests for RFC2822 compliance
		if (emailAddress == null || "".equals(emailAddress) || EmailAddress.isValidAddressList(emailAddress)) {
			return true;
		}
		return false;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Transient
	public String getDecodedEmail() {
		String decoded = "";
		try {
			decoded = AECGenKey.decryptBase64String(getEmail());
		} catch (Exception e) {}
		return decoded;
	}

	@Transient
	public void setEncodedEmail(String email) {
		if (email == null)
			email = "";
		setEmail(AECGenKey.base64encodeEncryptObject(email));
	}


	@Column(name="pass", length=44, nullable=false)
	public String getPass() {
		return this.pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	@Transient
	public String getDecodedPass() {
		return AECGenKey.decryptBase64String(getPass());
	}

	@Transient
	public void setEncodedPass(String pass) {
		setPass(AECGenKey.base64encodeEncryptObject(pass));
	}


	@Column(name="date", nullable=false)
	@Temporal(TemporalType.DATE)
	public Calendar getDate() {
		return this.date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}


	@Column(name="unsubscribe", nullable=false)
	public Boolean isUnsubscribe() {
		return this.unsubscribe;
	}

	public void setUnsubscribe(Boolean unsubscribe) {
		this.unsubscribe = unsubscribe;
	}


	@Column(name="bounce_reason", length=2000, nullable=true)
	public String getBounceReason() {
		return this.bounceReason;
	}

	public void setBounceReason(final String bounceReason) {
		this.bounceReason = bounceReason;
	}


	@Column(name="subscription_notification", nullable=false)
	public Boolean getSubscriptionNotification() {
		return this.subscriptionNotification;
	}

	public void setSubscriptionNotification(Boolean subscriptionNotification) {
		this.subscriptionNotification = subscriptionNotification;
	}


	//bi-directional many-to-one association to Batch
    @ManyToOne(cascade={CascadeType.PERSIST,CascadeType.DETACH,CascadeType.MERGE,CascadeType.REFRESH}) // No cascade remove
	@JoinColumn(name="subscription_batch_id", nullable=true)
	public Batch getBatch() {
		return this.batch;
	}

	public void setBatch(Batch batch) {
		this.batch = batch;
	}
	
	//bi-directional many-to-one association to Surgery
    @ManyToOne
	@JoinColumn(name="surgery_id", nullable=false)
	public Surgery getSurgery() {
		return this.surgery;
	}

	public void setSurgery(Surgery surgery) {
		this.surgery = surgery;
	}
	

	//bi-directional many-to-one association to Protocol
	@OneToMany(mappedBy="operation", cascade = {CascadeType.ALL})
	public Set<Protocol> getProtocols() {
		return this.protocols;
	}

	public void setProtocols(Set<Protocol> protocols) {
		this.protocols = protocols;
	}

	@Transient
	public static Operation getByUuidString(final String uuidString, final SessionBean sb) {
		UUID uuid = null;
		try {
			uuid = UUID.fromString(uuidString);
		} catch (IllegalArgumentException e) {
			throw new IncompleteArgumentException("uuidString is not a valid UUID string");
		}
		return getByUuid(uuid, sb);
	}

	@Transient
	public static Operation getByUuid(final UUID uuid, final SessionBean sb) {
		TypedQuery<Operation> qry = sb.getEm().createNamedQuery("Operation.findByMailId", Operation.class);
		qry.setParameter("mailIdHigh", uuid.getMostSignificantBits());
		qry.setParameter("mailIdLow", uuid.getLeastSignificantBits());
		Operation operation = qry.getSingleResult();
		return operation;
	}
}
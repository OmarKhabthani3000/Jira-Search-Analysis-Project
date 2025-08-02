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

import com.socrates.webscore.beans.SessionBean;


/**
 * @author Hugh Bragg
 *
 * The persistent class for the protocol_schedule database table.
 * 
 */
@Entity
@Table(name="protocol_schedule")
@NamedNativeQueries(value={
	@NamedNativeQuery(
			name="ProtocolSchedule.mailList",
			query="SELECT ps.* FROM webscore.protocol AS p INNER JOIN webscore.protocol_schedule AS ps ON ps.protocol_id = p.id WHERE p.operation_id = ? AND p.cancelled = false AND ps.submitted = false AND ps.expired = false AND ps.collected = false AND ( LOCALTIMESTAMP >= ( ps.schedule_date - INTERVAL '1' day * ps.schedule_window ) AND ( ( ps.mailed is null AND LOCALTIMESTAMP < ( ps.schedule_date - INTERVAL '1' day * ( ( ps.schedule_window / 2 ) + ( ps.schedule_window % 2 ) ) ) ) OR ( ps.reminder is null AND LOCALTIMESTAMP < ps.schedule_date ) ) OR ( LOCALTIMESTAMP > ps.schedule_date ) )",
			resultClass=ProtocolSchedule.class
	)
})
@NamedQueries(value={
		@NamedQuery(name="ProtocolSchedule.operationMailList",
				query="SELECT ps "
					+ "FROM ProtocolSchedule ps "
					+ "INNER JOIN ps.protocol p "
					+ "WHERE p.operation.id = :operationId "
					+ "AND p.cancelled = false "
					+ "AND ps.submitted = false "
					+ "AND ps.expired = false "
					+ "AND ps.collected = false "
					+ "AND CURRENT_TIMESTAMP <= ps.scheduleDate "
					+ "AND "
						+ "( "
							+ "( CURRENT_TIMESTAMP >= ps.mailingDate "
							+ "AND ps.mailed is null "
							+ "AND CURRENT_TIMESTAMP < ps.reminderDate ) "
						+ "OR "
							+ "( CURRENT_TIMESTAMP >= ps.reminderDate "
							+ "AND ps.reminder is null )"
						+ " )"),
		@NamedQuery(name="ProtocolSchedule.findByMailId",query="SELECT ps FROM ProtocolSchedule ps WHERE ps.mailIdHigh = :mailIdHigh AND ps.mailIdLow = :mailIdLow"),
		@NamedQuery(name="ProtocolSchedule.findAllProtocolSchedules",query="SELECT ps FROM ProtocolSchedule ps"),
		@NamedQuery(name="ProtocolSchedule.collectResults",query="SELECT r FROM ProtocolSchedule ps INNER JOIN ps.results r WHERE ps.id = :protocolScheduleId AND ps.submitted = true AND ps.collected = false"),
})
public class ProtocolSchedule extends SuperEntity implements Serializable {
	private static final long serialVersionUID = 2L;
	private Long mailIdHigh;
	private Long mailIdLow;
	private Calendar scheduleDate;
	private Calendar mailingDate;
	private Calendar reminderDate;
	private Boolean submitted;
	private Boolean expired;
	private Boolean collected;
	private Calendar mailed;
	private Integer emailAttempts;
	private Calendar reminder;
	private Integer reminderAttempts;
	private Protocol protocol;
	private ScoreVersion scoreVersion;
	private Set<Result> results;
	private Batch batch;

    public ProtocolSchedule() {}


	@Id
	@SequenceGenerator(name="PROTOCOL_SCHEDULE_ID_GENERATOR", sequenceName="PROTOCOL_SCHEDULE_ID_SEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="PROTOCOL_SCHEDULE_ID_GENERATOR")
	@Column(unique=true, nullable=false)
	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
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
	public void setMailIdHigh(final Long mailIdHigh) {
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
	public void setMailIdLow(final Long mailIdLow) {
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
	public void setMailUuid(final UUID uuid) throws IOException {
		setMailIdHigh(uuid.getMostSignificantBits());
		setMailIdLow(uuid.getLeastSignificantBits());
	}

	
	@Column(name="schedule_date", nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getScheduleDate() {
		return this.scheduleDate;
	}

	public void setScheduleDate(final Calendar scheduleDate) {
		this.scheduleDate = scheduleDate;
	}


	@Column(name="mailing_date", nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getMailingDate() {
		return this.mailingDate;
	}

	public void setMailingDate(final Calendar mailingDate) {
		this.mailingDate = mailingDate;
	}


	@Column(name="reminder_date", nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getReminderDate() {
		return this.reminderDate;
	}

	public void setReminderDate(final Calendar reminderDate) {
		this.reminderDate = reminderDate;
	}


	@Column(name="mailed", nullable=true)
	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getMailed() {
		return this.mailed;
	}

	public void setMailed(final Calendar mailed) {
		this.mailed = mailed;
	}


	@Column(name="email_attempts", nullable=false)
	public Integer getEmailAttempts() {
		return this.emailAttempts;
	}

	public void setEmailAttempts(final Integer emailAttempts) {
		this.emailAttempts = emailAttempts;
	}


	@Column(name="reminder", nullable=true)
	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getReminder() {
		return this.reminder;
	}

	public void setReminder(final Calendar reminder) {
		this.reminder = reminder;
	}


	@Column(name="reminder_attempts", nullable=false)
	public Integer getReminderAttempts() {
		return this.reminderAttempts;
	}

	public void setReminderAttempts(final Integer reminderAttempts) {
		this.reminderAttempts = reminderAttempts;
	}


	@Column(name="submitted", nullable=false)
	public Boolean isSubmitted() {
		return submitted;
	}

	public void setSubmitted(final Boolean submitted) {
		this.submitted = submitted;
	}


	@Column(name="expired", nullable=false)
	public Boolean isExpired() {
		return expired;
	}

	public void setExpired(final Boolean expired) {
		this.expired = expired;
	}


	@Column(name="collected", nullable=false)
	public Boolean isCollected() {
		return collected;
	}

	public void setCollected(final Boolean collected) {
		this.collected = collected;
	}


	//bi-directional many-to-one association to Protocol
    @ManyToOne
	@JoinColumn(name="protocol_id", nullable=false)
	public Protocol getProtocol() {
		return this.protocol;
	}

	public void setProtocol(final Protocol protocol) {
		this.protocol = protocol;
	}
	

	//bi-directional many-to-one association to ScoreVersion
    @ManyToOne
	@JoinColumn(name="score_version_id", nullable=false)
	public ScoreVersion getScoreVersion() {
		return this.scoreVersion;
	}

	public void setScoreVersion(final ScoreVersion scoreVersion) {
		this.scoreVersion = scoreVersion;
	}
	
	@Transient
	public Score getScore() {
		return this.getScoreVersion().getScore();
	}

	//bi-directional one-to-many association to Result
	@OneToMany(mappedBy="protocolSchedule", cascade = {CascadeType.ALL})
	public Set<Result> getResults() {
		return this.results;
	}

	public void setResults(final Set<Result> results) {
		this.results = results;
	}
	
	//bi-directional many-to-one association to Batch
    @ManyToOne(cascade={CascadeType.PERSIST,CascadeType.DETACH,CascadeType.MERGE,CascadeType.REFRESH}) // No cascade remove
	@JoinColumn(name="batch_id", nullable=true)
	public Batch getBatch() {
		return this.batch;
	}

	public void setBatch(final Batch batch) {
		this.batch = batch;
	}

	@Transient
	public static ProtocolSchedule getByUuidString(final String uuidString, final SessionBean sb) {
		UUID uuid = null;
		try {
			uuid = UUID.fromString(uuidString);
		} catch (IllegalArgumentException e) {
			throw new IncompleteArgumentException("uuidString is not a valid UUID string");
		}
		return getByUuid(uuid, sb);
	}

	@Transient
	public static ProtocolSchedule getByUuid(final UUID uuid, final SessionBean sb) {
		TypedQuery<ProtocolSchedule> qry = sb.getEm().createNamedQuery("ProtocolSchedule.findByMailId", ProtocolSchedule.class);
		qry.setParameter("mailIdHigh", uuid.getMostSignificantBits());
		qry.setParameter("mailIdLow", uuid.getLeastSignificantBits());
		ProtocolSchedule protocolSchedule = qry.getSingleResult();
		return protocolSchedule;
	}
}
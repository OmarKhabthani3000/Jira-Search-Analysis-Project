package de.hska;

import static javax.persistence.GenerationType.AUTO;

import javax.persistence.Entity;

import javax.persistence.Table;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Column;


@Entity
@Table(name="kunde")
public class Kunde implements java.io.Serializable {
	private static final long serialVersionUID = -9145991012326136804L;
	static final Long KEINE_ID = Long.valueOf(-1);
	static final int ERSTE_VERSION = 0;


	@Id
	@GeneratedValue(strategy=AUTO, generator="kunde_sequence_name")
	@SequenceGenerator(name="kunde_sequence_name", sequenceName="kunde_k_id_seq", allocationSize=1)
	@Column(name="k_id", nullable=false)
	protected Long id = KEINE_ID;

	@Column(length=32, nullable=false)
	protected String name = "";

	@OneToOne(mappedBy="kunde")
	protected Betreuer betreuer;

	public Kunde() {
		super();
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String nachname) {
		this.name = nachname;
	}

	public Betreuer getBetreuer() {
		return betreuer;
	}

	public void setBetreuer(Betreuer betreuer) {
		this.betreuer = betreuer;
	}

	@Override
	public String toString() {
		return "{id=" + id + ", name=" + name + '}';
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (other instanceof Kunde == false) return false;

		final Kunde k = (Kunde) other;
		return id.equals(k.id) && name.equals(k.name);
	}

	@Override
	public int hashCode() {
		return 37 ^ id.intValue();          // Bit-weise XOR
	}
}
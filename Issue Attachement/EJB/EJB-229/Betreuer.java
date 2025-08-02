package de.hska;

import static javax.persistence.GenerationType.AUTO;
import static de.hska.Kunde.KEINE_ID;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="betreuer")
public class Betreuer implements Serializable {
	private static final long serialVersionUID = -4090693388150752068L;

	@Id
	@GeneratedValue(strategy=AUTO, generator="betreuer_sequence_name")
	@SequenceGenerator(name="betreuer_sequence_name", sequenceName="betreuer_b_id_seq", allocationSize=1)
	@Column(name="b_id", nullable=false)
	private Long id = KEINE_ID;

	@Column(length=32, nullable=false)
	protected String name = "";

	@OneToOne
	@JoinColumn(name="kunde_fk")
	private Kunde kunde = null;

	public Betreuer() {
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

	public void setName(String name) {
		this.name = name;
	}

	public Kunde getKunde() {
		return kunde;
	}
	public void setKunde(Kunde kunde) {
		this.kunde = kunde;
	}

	@Override
	public String toString() {
		return "{id=" + id + ", name=" + name + '}';
	}
	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (other instanceof Betreuer == false) return false;

		final Betreuer b = (Betreuer) other;
		return id.equals(b.id);
	}
	
	@Override
	public int hashCode() {
		return 37 ^ id.intValue();          // Bit-weise XOR
	}
}

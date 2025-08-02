import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

@Entity
@Table(name = "TB_REQUERIMENTO")
@SequenceGenerator(name = "SQ_NAME", sequenceName = "SQ_REQUERIMENTO", initialValue = 1, allocationSize = 1)
@DynamicUpdate
@DynamicInsert
public class Requerimento extends MyBean {
	private static final long serialVersionUID = 1L;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "ID_PESSOA")
	private Pessoa requerente = new Pessoa();

	@Column(name = "NR_PROTOCOLO")
	private String nrProtocolo;

	@Column(name = "VERSAO", insertable = false, updatable = false)
	@Version
	@Generated(GenerationTime.ALWAYS)
	private Long versao;

	public Pessoa getRequerente() {
		return requerente;
	}

	public void setRequerente(Pessoa requerente) {
		this.requerente = requerente;
	}

	public String getNrProtocolo() {
		return nrProtocolo;
	}

	public void setNrProtocolo(String nrProtocolo) {
		this.nrProtocolo = nrProtocolo;
	}

	public Long getVersao() {
		return versao;
	}
}

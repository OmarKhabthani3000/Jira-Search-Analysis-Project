import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.OptimisticLock;

@Entity
@Table(name = "TB_PESSOA")
@SequenceGenerator(name = "SQ_NAME", sequenceName = "SQ_PESSOA", initialValue = 1, allocationSize = 1)
@DynamicUpdate
@DynamicInsert
public class Pessoa extends MyBean {
	private static final long serialVersionUID = 1L;

	@Column(name = "NOME")
	private String nome;

	@Column(name = "DT_NASCIMENTO")
	private Date dataNascimento;

	@Column(name = "VERSAO", insertable = false, updatable = false)
	@Version
	@Generated(GenerationTime.ALWAYS)
	private Long versao;

	// I want Pessoa to be versioned if records are added to/removed from PessoaNomeAnterior
	@OptimisticLock(excluded = false)
	@OneToMany(mappedBy = "pessoa", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PessoaNomeAnterior> pessoaNomesAnteriores = new ArrayList<PessoaNomeAnterior>();

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public Long getVersao() {
		return versao;
	}

	public List<NomeAnteriorPessoa> getPessoaNomesAnteriores() {
		return pessoaNomesAnteriores;
	}

	public void setPessoaNomesAnteriores(List<NomeAnteriorPessoa> pessoaNomesAnteriores) {
		this.pessoaNomesAnteriores = pessoaNomesAnteriores;
	}
}

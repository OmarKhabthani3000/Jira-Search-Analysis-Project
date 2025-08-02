import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "TB_PESSOA_NOME_ANTERIOR")
@SequenceGenerator(name = "SQ_NAME", sequenceName = "SQ_PESSOA_NOME_ANTERIOR", initialValue = 1, allocationSize = 1)
public class PessoaNomeAnterior extends MyBean {
	private static final long serialVersionUID = 1L;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "ID_PESSOA")
	private Pessoa pessoa = new Pessoa();

	@Column(name = "NOME")
	private String nome;

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
}

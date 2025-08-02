package pt.gedi.siag.persistence;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;


/** 
 *         Classe gerada pelo Gerador automático, nao alterar!!!
 *         @author GEDI Team
 *     
*/
public class ASUtilizador implements 
		BasePojo
	,Serializable {

    /** identifier field */
    private Integer chvP;

    /** nullable persistent field */
    private String defaultModAvancado;

    /** nullable persistent field */
    private Integer grupoFiltro;

    /** persistent field */
    private Date criacaoTS;

    /** persistent field */
    private Integer criacaoUtilizador;

    /** nullable persistent field */
    private Date UAltTS;

    /** nullable persistent field */
    private Integer UAltUtilizador;

    /** nullable persistent field */
    private Integer chvEPSUnidadeUtilizadora;

    /** nullable persistent field */
    private Integer chvEPSExercicio;

    /** persistent field */
    private String nomeUtilizador;

    /** nullable persistent field */
    private String codAcesso;

    /** nullable persistent field */
    private Integer chvEASGrupo;

    /** nullable persistent field */
    private Integer numMaquinas;

    /** nullable persistent field */
    private Integer numAcessos;

    /** nullable persistent field */
    private Date ultimoAcesso;

    /** nullable persistent field */
    private Date tempoUtilizacao;

    /** persistent field */
    private Integer multiJanela;

    /** persistent field */
    private Integer fechoJanela;

    /** persistent field */
    private Integer fechoMenu;

    /** persistent field */
    private Integer tipoDocExistencia;

    /** nullable persistent field */
    private Integer maxRegistos;

    /** nullable persistent field */
    private Integer visualizacao;

    /** nullable persistent field */
    private String defaultTabAuxiliar;

    /** nullable persistent field */
    private pt.gedi.siag.persistence.PSUnidadeUtilizadora relMO_ASUtilizador_PSUnidadeUtilizadora;

    /** nullable persistent field */
    private pt.gedi.siag.persistence.PSExercicio relMO_ASUtilizador_PSExercicio;

    /** nullable persistent field */
    private pt.gedi.siag.persistence.ASGrupo relMO_ASUtilizador_ASGrupo;

    /** persistent field */
    private Set relOM_ASUtilizador_ASUtilizadorPrivilegio;

    /** persistent field */
    private Set relOM_ASUtilizador_ASUtilizadorGrupo;

    /** persistent field */
    private Set relOM_ASUtilizador_SIListas;

    /** persistent field */
    private Set relOM_ASUtilizador_SIPesquisa;

    /** persistent field */
    private Set relOM_ASUtilizador_SIOrdenacao;

    /** persistent field */
    private Set relOM_ASUtilizador_PSDocumentoNota;

    /** full constructor */
    public ASUtilizador(String defaultModAvancado, Integer grupoFiltro, Date criacaoTS, Integer criacaoUtilizador, Date UAltTS, Integer UAltUtilizador, Integer chvEPSUnidadeUtilizadora, Integer chvEPSExercicio, String nomeUtilizador, String codAcesso, Integer chvEASGrupo, Integer numMaquinas, Integer numAcessos, Date ultimoAcesso, Date tempoUtilizacao, Integer multiJanela, Integer fechoJanela, Integer fechoMenu, Integer tipoDocExistencia, Integer maxRegistos, Integer visualizacao, String defaultTabAuxiliar, pt.gedi.siag.persistence.PSUnidadeUtilizadora relMO_ASUtilizador_PSUnidadeUtilizadora, pt.gedi.siag.persistence.PSExercicio relMO_ASUtilizador_PSExercicio, pt.gedi.siag.persistence.ASGrupo relMO_ASUtilizador_ASGrupo, Set relOM_ASUtilizador_ASUtilizadorPrivilegio, Set relOM_ASUtilizador_ASUtilizadorGrupo, Set relOM_ASUtilizador_SIListas, Set relOM_ASUtilizador_SIPesquisa, Set relOM_ASUtilizador_SIOrdenacao, Set relOM_ASUtilizador_PSDocumentoNota) {
        this.defaultModAvancado = defaultModAvancado;
        this.grupoFiltro = grupoFiltro;
        this.criacaoTS = criacaoTS;
        this.criacaoUtilizador = criacaoUtilizador;
        this.UAltTS = UAltTS;
        this.UAltUtilizador = UAltUtilizador;
        this.chvEPSUnidadeUtilizadora = chvEPSUnidadeUtilizadora;
        this.chvEPSExercicio = chvEPSExercicio;
        this.nomeUtilizador = nomeUtilizador;
        this.codAcesso = codAcesso;
        this.chvEASGrupo = chvEASGrupo;
        this.numMaquinas = numMaquinas;
        this.numAcessos = numAcessos;
        this.ultimoAcesso = ultimoAcesso;
        this.tempoUtilizacao = tempoUtilizacao;
        this.multiJanela = multiJanela;
        this.fechoJanela = fechoJanela;
        this.fechoMenu = fechoMenu;
        this.tipoDocExistencia = tipoDocExistencia;
        this.maxRegistos = maxRegistos;
        this.visualizacao = visualizacao;
        this.defaultTabAuxiliar = defaultTabAuxiliar;
        this.relMO_ASUtilizador_PSUnidadeUtilizadora = relMO_ASUtilizador_PSUnidadeUtilizadora;
        this.relMO_ASUtilizador_PSExercicio = relMO_ASUtilizador_PSExercicio;
        this.relMO_ASUtilizador_ASGrupo = relMO_ASUtilizador_ASGrupo;
        this.relOM_ASUtilizador_ASUtilizadorPrivilegio = relOM_ASUtilizador_ASUtilizadorPrivilegio;
        this.relOM_ASUtilizador_ASUtilizadorGrupo = relOM_ASUtilizador_ASUtilizadorGrupo;
        this.relOM_ASUtilizador_SIListas = relOM_ASUtilizador_SIListas;
        this.relOM_ASUtilizador_SIPesquisa = relOM_ASUtilizador_SIPesquisa;
        this.relOM_ASUtilizador_SIOrdenacao = relOM_ASUtilizador_SIOrdenacao;
        this.relOM_ASUtilizador_PSDocumentoNota = relOM_ASUtilizador_PSDocumentoNota;
    }

    /** default constructor */
    public ASUtilizador() {
    }

    /** minimal constructor */
    public ASUtilizador(Date criacaoTS, Integer criacaoUtilizador, String nomeUtilizador, Integer multiJanela, Integer fechoJanela, Integer fechoMenu, Integer tipoDocExistencia, Set relOM_ASUtilizador_ASUtilizadorPrivilegio, Set relOM_ASUtilizador_ASUtilizadorGrupo, Set relOM_ASUtilizador_SIListas, Set relOM_ASUtilizador_SIPesquisa, Set relOM_ASUtilizador_SIOrdenacao, Set relOM_ASUtilizador_PSDocumentoNota) {
        this.criacaoTS = criacaoTS;
        this.criacaoUtilizador = criacaoUtilizador;
        this.nomeUtilizador = nomeUtilizador;
        this.multiJanela = multiJanela;
        this.fechoJanela = fechoJanela;
        this.fechoMenu = fechoMenu;
        this.tipoDocExistencia = tipoDocExistencia;
        this.relOM_ASUtilizador_ASUtilizadorPrivilegio = relOM_ASUtilizador_ASUtilizadorPrivilegio;
        this.relOM_ASUtilizador_ASUtilizadorGrupo = relOM_ASUtilizador_ASUtilizadorGrupo;
        this.relOM_ASUtilizador_SIListas = relOM_ASUtilizador_SIListas;
        this.relOM_ASUtilizador_SIPesquisa = relOM_ASUtilizador_SIPesquisa;
        this.relOM_ASUtilizador_SIOrdenacao = relOM_ASUtilizador_SIOrdenacao;
        this.relOM_ASUtilizador_PSDocumentoNota = relOM_ASUtilizador_PSDocumentoNota;
    }

    public Integer getChvP() {
        return this.chvP;
    }

    public void setChvP(Integer chvP) {
        this.chvP = chvP;
    }

    public String getDefaultModAvancado() {
        return this.defaultModAvancado;
    }

    public void setDefaultModAvancado(String defaultModAvancado) {
        this.defaultModAvancado = defaultModAvancado;
    }

    public Integer getGrupoFiltro() {
        return this.grupoFiltro;
    }

    public void setGrupoFiltro(Integer grupoFiltro) {
        this.grupoFiltro = grupoFiltro;
    }

    public Date getCriacaoTS() {
        return this.criacaoTS;
    }

    public void setCriacaoTS(Date criacaoTS) {
        this.criacaoTS = criacaoTS;
    }

    public Integer getCriacaoUtilizador() {
        return this.criacaoUtilizador;
    }

    public void setCriacaoUtilizador(Integer criacaoUtilizador) {
        this.criacaoUtilizador = criacaoUtilizador;
    }

    public Date getUAltTS() {
        return this.UAltTS;
    }

    public void setUAltTS(Date UAltTS) {
        this.UAltTS = UAltTS;
    }

    public Integer getUAltUtilizador() {
        return this.UAltUtilizador;
    }

    public void setUAltUtilizador(Integer UAltUtilizador) {
        this.UAltUtilizador = UAltUtilizador;
    }

    public Integer getChvEPSUnidadeUtilizadora() {
        return this.chvEPSUnidadeUtilizadora;
    }

    public void setChvEPSUnidadeUtilizadora(Integer chvEPSUnidadeUtilizadora) {
        this.chvEPSUnidadeUtilizadora = chvEPSUnidadeUtilizadora;
    }

    public Integer getChvEPSExercicio() {
        return this.chvEPSExercicio;
    }

    public void setChvEPSExercicio(Integer chvEPSExercicio) {
        this.chvEPSExercicio = chvEPSExercicio;
    }

    public String getNomeUtilizador() {
        return this.nomeUtilizador;
    }

    public void setNomeUtilizador(String nomeUtilizador) {
        this.nomeUtilizador = nomeUtilizador;
    }

    public String getCodAcesso() {
        return this.codAcesso;
    }

    public void setCodAcesso(String codAcesso) {
        this.codAcesso = codAcesso;
    }

    public Integer getChvEASGrupo() {
        return this.chvEASGrupo;
    }

    public void setChvEASGrupo(Integer chvEASGrupo) {
        this.chvEASGrupo = chvEASGrupo;
    }

    public Integer getNumMaquinas() {
        return this.numMaquinas;
    }

    public void setNumMaquinas(Integer numMaquinas) {
        this.numMaquinas = numMaquinas;
    }

    public Integer getNumAcessos() {
        return this.numAcessos;
    }

    public void setNumAcessos(Integer numAcessos) {
        this.numAcessos = numAcessos;
    }

    public Date getUltimoAcesso() {
        return this.ultimoAcesso;
    }

    public void setUltimoAcesso(Date ultimoAcesso) {
        this.ultimoAcesso = ultimoAcesso;
    }

    public Date getTempoUtilizacao() {
        return this.tempoUtilizacao;
    }

    public void setTempoUtilizacao(Date tempoUtilizacao) {
        this.tempoUtilizacao = tempoUtilizacao;
    }

    public Integer getMultiJanela() {
        return this.multiJanela;
    }

    public void setMultiJanela(Integer multiJanela) {
        this.multiJanela = multiJanela;
    }

    public Integer getFechoJanela() {
        return this.fechoJanela;
    }

    public void setFechoJanela(Integer fechoJanela) {
        this.fechoJanela = fechoJanela;
    }

    public Integer getFechoMenu() {
        return this.fechoMenu;
    }

    public void setFechoMenu(Integer fechoMenu) {
        this.fechoMenu = fechoMenu;
    }

    public Integer getTipoDocExistencia() {
        return this.tipoDocExistencia;
    }

    public void setTipoDocExistencia(Integer tipoDocExistencia) {
        this.tipoDocExistencia = tipoDocExistencia;
    }

    public Integer getMaxRegistos() {
        return this.maxRegistos;
    }

    public void setMaxRegistos(Integer maxRegistos) {
        this.maxRegistos = maxRegistos;
    }

    public Integer getVisualizacao() {
        return this.visualizacao;
    }

    public void setVisualizacao(Integer visualizacao) {
        this.visualizacao = visualizacao;
    }

    public String getDefaultTabAuxiliar() {
        return this.defaultTabAuxiliar;
    }

    public void setDefaultTabAuxiliar(String defaultTabAuxiliar) {
        this.defaultTabAuxiliar = defaultTabAuxiliar;
    }

    public pt.gedi.siag.persistence.PSUnidadeUtilizadora getRelMO_ASUtilizador_PSUnidadeUtilizadora() {
        return this.relMO_ASUtilizador_PSUnidadeUtilizadora;
    }

    public void setRelMO_ASUtilizador_PSUnidadeUtilizadora(pt.gedi.siag.persistence.PSUnidadeUtilizadora relMO_ASUtilizador_PSUnidadeUtilizadora) {
        this.relMO_ASUtilizador_PSUnidadeUtilizadora = relMO_ASUtilizador_PSUnidadeUtilizadora;
    }

    public pt.gedi.siag.persistence.PSExercicio getRelMO_ASUtilizador_PSExercicio() {
        return this.relMO_ASUtilizador_PSExercicio;
    }

    public void setRelMO_ASUtilizador_PSExercicio(pt.gedi.siag.persistence.PSExercicio relMO_ASUtilizador_PSExercicio) {
        this.relMO_ASUtilizador_PSExercicio = relMO_ASUtilizador_PSExercicio;
    }

    public pt.gedi.siag.persistence.ASGrupo getRelMO_ASUtilizador_ASGrupo() {
        return this.relMO_ASUtilizador_ASGrupo;
    }

    public void setRelMO_ASUtilizador_ASGrupo(pt.gedi.siag.persistence.ASGrupo relMO_ASUtilizador_ASGrupo) {
        this.relMO_ASUtilizador_ASGrupo = relMO_ASUtilizador_ASGrupo;
    }

    public Set getRelOM_ASUtilizador_ASUtilizadorPrivilegio() {
        return this.relOM_ASUtilizador_ASUtilizadorPrivilegio;
    }

    public void setRelOM_ASUtilizador_ASUtilizadorPrivilegio(Set relOM_ASUtilizador_ASUtilizadorPrivilegio) {
        this.relOM_ASUtilizador_ASUtilizadorPrivilegio = relOM_ASUtilizador_ASUtilizadorPrivilegio;
    }

    public Set getRelOM_ASUtilizador_ASUtilizadorGrupo() {
        return this.relOM_ASUtilizador_ASUtilizadorGrupo;
    }

    public void setRelOM_ASUtilizador_ASUtilizadorGrupo(Set relOM_ASUtilizador_ASUtilizadorGrupo) {
        this.relOM_ASUtilizador_ASUtilizadorGrupo = relOM_ASUtilizador_ASUtilizadorGrupo;
    }

    public Set getRelOM_ASUtilizador_SIListas() {
        return this.relOM_ASUtilizador_SIListas;
    }

    public void setRelOM_ASUtilizador_SIListas(Set relOM_ASUtilizador_SIListas) {
        this.relOM_ASUtilizador_SIListas = relOM_ASUtilizador_SIListas;
    }

    public Set getRelOM_ASUtilizador_SIPesquisa() {
        return this.relOM_ASUtilizador_SIPesquisa;
    }

    public void setRelOM_ASUtilizador_SIPesquisa(Set relOM_ASUtilizador_SIPesquisa) {
        this.relOM_ASUtilizador_SIPesquisa = relOM_ASUtilizador_SIPesquisa;
    }

    public Set getRelOM_ASUtilizador_SIOrdenacao() {
        return this.relOM_ASUtilizador_SIOrdenacao;
    }

    public void setRelOM_ASUtilizador_SIOrdenacao(Set relOM_ASUtilizador_SIOrdenacao) {
        this.relOM_ASUtilizador_SIOrdenacao = relOM_ASUtilizador_SIOrdenacao;
    }

    public Set getRelOM_ASUtilizador_PSDocumentoNota() {
        return this.relOM_ASUtilizador_PSDocumentoNota;
    }

    public void setRelOM_ASUtilizador_PSDocumentoNota(Set relOM_ASUtilizador_PSDocumentoNota) {
        this.relOM_ASUtilizador_PSDocumentoNota = relOM_ASUtilizador_PSDocumentoNota;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("chvP", getChvP())
            .toString();
    }

}

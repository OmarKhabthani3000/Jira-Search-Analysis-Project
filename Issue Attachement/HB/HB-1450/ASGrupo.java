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
public class ASGrupo implements 
		BasePojo
	,Serializable {

    /** identifier field */
    private Integer chvP;

    /** persistent field */
    private String designacao;

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

    /** persistent field */
    private Set relOM_ASGrupo_ASUtilizador;

    /** persistent field */
    private Set relOM_ASGrupo_ASGrupoPrivilegio;

    /** persistent field */
    private Set relOM_ASGrupo_ASUtilizadorGrupo;

    /** persistent field */
    private Set relOM_ASGrupo_PSDocumentoNota;

    /** full constructor */
    public ASGrupo(String designacao, Integer grupoFiltro, Date criacaoTS, Integer criacaoUtilizador, Date UAltTS, Integer UAltUtilizador, Set relOM_ASGrupo_ASUtilizador, Set relOM_ASGrupo_ASGrupoPrivilegio, Set relOM_ASGrupo_ASUtilizadorGrupo, Set relOM_ASGrupo_PSDocumentoNota) {
        this.designacao = designacao;
        this.grupoFiltro = grupoFiltro;
        this.criacaoTS = criacaoTS;
        this.criacaoUtilizador = criacaoUtilizador;
        this.UAltTS = UAltTS;
        this.UAltUtilizador = UAltUtilizador;
        this.relOM_ASGrupo_ASUtilizador = relOM_ASGrupo_ASUtilizador;
        this.relOM_ASGrupo_ASGrupoPrivilegio = relOM_ASGrupo_ASGrupoPrivilegio;
        this.relOM_ASGrupo_ASUtilizadorGrupo = relOM_ASGrupo_ASUtilizadorGrupo;
        this.relOM_ASGrupo_PSDocumentoNota = relOM_ASGrupo_PSDocumentoNota;
    }

    /** default constructor */
    public ASGrupo() {
    }

    /** minimal constructor */
    public ASGrupo(String designacao, Date criacaoTS, Integer criacaoUtilizador, Set relOM_ASGrupo_ASUtilizador, Set relOM_ASGrupo_ASGrupoPrivilegio, Set relOM_ASGrupo_ASUtilizadorGrupo, Set relOM_ASGrupo_PSDocumentoNota) {
        this.designacao = designacao;
        this.criacaoTS = criacaoTS;
        this.criacaoUtilizador = criacaoUtilizador;
        this.relOM_ASGrupo_ASUtilizador = relOM_ASGrupo_ASUtilizador;
        this.relOM_ASGrupo_ASGrupoPrivilegio = relOM_ASGrupo_ASGrupoPrivilegio;
        this.relOM_ASGrupo_ASUtilizadorGrupo = relOM_ASGrupo_ASUtilizadorGrupo;
        this.relOM_ASGrupo_PSDocumentoNota = relOM_ASGrupo_PSDocumentoNota;
    }

    public Integer getChvP() {
        return this.chvP;
    }

    public void setChvP(Integer chvP) {
        this.chvP = chvP;
    }

    public String getDesignacao() {
        return this.designacao;
    }

    public void setDesignacao(String designacao) {
        this.designacao = designacao;
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

    public Set getRelOM_ASGrupo_ASUtilizador() {
        return this.relOM_ASGrupo_ASUtilizador;
    }

    public void setRelOM_ASGrupo_ASUtilizador(Set relOM_ASGrupo_ASUtilizador) {
        this.relOM_ASGrupo_ASUtilizador = relOM_ASGrupo_ASUtilizador;
    }

    public Set getRelOM_ASGrupo_ASGrupoPrivilegio() {
        return this.relOM_ASGrupo_ASGrupoPrivilegio;
    }

    public void setRelOM_ASGrupo_ASGrupoPrivilegio(Set relOM_ASGrupo_ASGrupoPrivilegio) {
        this.relOM_ASGrupo_ASGrupoPrivilegio = relOM_ASGrupo_ASGrupoPrivilegio;
    }

    public Set getRelOM_ASGrupo_ASUtilizadorGrupo() {
        return this.relOM_ASGrupo_ASUtilizadorGrupo;
    }

    public void setRelOM_ASGrupo_ASUtilizadorGrupo(Set relOM_ASGrupo_ASUtilizadorGrupo) {
        this.relOM_ASGrupo_ASUtilizadorGrupo = relOM_ASGrupo_ASUtilizadorGrupo;
    }

    public Set getRelOM_ASGrupo_PSDocumentoNota() {
        return this.relOM_ASGrupo_PSDocumentoNota;
    }

    public void setRelOM_ASGrupo_PSDocumentoNota(Set relOM_ASGrupo_PSDocumentoNota) {
        this.relOM_ASGrupo_PSDocumentoNota = relOM_ASGrupo_PSDocumentoNota;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("chvP", getChvP())
            .toString();
    }

}

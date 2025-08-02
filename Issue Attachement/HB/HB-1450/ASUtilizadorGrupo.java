package pt.gedi.siag.persistence;

import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang.builder.ToStringBuilder;


/** 
 *         Classe gerada pelo Gerador automático, nao alterar!!!
 *         @author GEDI Team
 *     
*/
public class ASUtilizadorGrupo implements 
		BasePojo
	,Serializable {

    /** identifier field */
    private Integer chvP;

    /** nullable persistent field */
    private Integer chvEASGrupo;

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
    private Integer chvEASUtilizador;

    /** nullable persistent field */
    private pt.gedi.siag.persistence.ASGrupo relMO_ASUtilizadorGrupo_ASGrupo;

    /** nullable persistent field */
    private pt.gedi.siag.persistence.ASUtilizador relMO_ASUtilizadorGrupo_ASUtilizador;

    /** full constructor */
    public ASUtilizadorGrupo(Integer chvEASGrupo, Integer grupoFiltro, Date criacaoTS, Integer criacaoUtilizador, Date UAltTS, Integer UAltUtilizador, Integer chvEASUtilizador, pt.gedi.siag.persistence.ASGrupo relMO_ASUtilizadorGrupo_ASGrupo, pt.gedi.siag.persistence.ASUtilizador relMO_ASUtilizadorGrupo_ASUtilizador) {
        this.chvEASGrupo = chvEASGrupo;
        this.grupoFiltro = grupoFiltro;
        this.criacaoTS = criacaoTS;
        this.criacaoUtilizador = criacaoUtilizador;
        this.UAltTS = UAltTS;
        this.UAltUtilizador = UAltUtilizador;
        this.chvEASUtilizador = chvEASUtilizador;
        this.relMO_ASUtilizadorGrupo_ASGrupo = relMO_ASUtilizadorGrupo_ASGrupo;
        this.relMO_ASUtilizadorGrupo_ASUtilizador = relMO_ASUtilizadorGrupo_ASUtilizador;
    }

    /** default constructor */
    public ASUtilizadorGrupo() {
    }

    /** minimal constructor */
    public ASUtilizadorGrupo(Date criacaoTS, Integer criacaoUtilizador) {
        this.criacaoTS = criacaoTS;
        this.criacaoUtilizador = criacaoUtilizador;
    }

    public Integer getChvP() {
        return this.chvP;
    }

    public void setChvP(Integer chvP) {
        this.chvP = chvP;
    }

    public Integer getChvEASGrupo() {
        return this.chvEASGrupo;
    }

    public void setChvEASGrupo(Integer chvEASGrupo) {
        this.chvEASGrupo = chvEASGrupo;
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

    public Integer getChvEASUtilizador() {
        return this.chvEASUtilizador;
    }

    public void setChvEASUtilizador(Integer chvEASUtilizador) {
        this.chvEASUtilizador = chvEASUtilizador;
    }

    public pt.gedi.siag.persistence.ASGrupo getRelMO_ASUtilizadorGrupo_ASGrupo() {
        return this.relMO_ASUtilizadorGrupo_ASGrupo;
    }

    public void setRelMO_ASUtilizadorGrupo_ASGrupo(pt.gedi.siag.persistence.ASGrupo relMO_ASUtilizadorGrupo_ASGrupo) {
        this.relMO_ASUtilizadorGrupo_ASGrupo = relMO_ASUtilizadorGrupo_ASGrupo;
    }

    public pt.gedi.siag.persistence.ASUtilizador getRelMO_ASUtilizadorGrupo_ASUtilizador() {
        return this.relMO_ASUtilizadorGrupo_ASUtilizador;
    }

    public void setRelMO_ASUtilizadorGrupo_ASUtilizador(pt.gedi.siag.persistence.ASUtilizador relMO_ASUtilizadorGrupo_ASUtilizador) {
        this.relMO_ASUtilizadorGrupo_ASUtilizador = relMO_ASUtilizadorGrupo_ASUtilizador;
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("chvP", getChvP())
            .toString();
    }

}

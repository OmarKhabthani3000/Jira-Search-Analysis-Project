/*
 * Created on 1/Fev/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package pt.gedi.siag.persistence;

import java.util.Date;

/**
 * @author Nuno Cardoso
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface BasePojo {

   
    
    //public abstract boolean validate();

//    /** identifier field */
//    protected Integer chvP;
//    /** nullable persistent field */
//    protected Date criacaoTs;
//    /** persistent field */
//    protected int criacaoUtilizador;
//    /** nullable persistent field */
//    protected Date ualtTs;
//    /** nullable persistent field */
//    protected Integer ualtUtilizador;

    public Integer getChvP();

    public void setChvP(Integer chvP) ;

    public Date getCriacaoTS();

    public void setCriacaoTS(Date criacaoTs);

    public Integer getCriacaoUtilizador();

    public void setCriacaoUtilizador(Integer criacaoUtilizador);

    public Date getUAltTS();

    public void setUAltTS(Date ualtTs);

    public Integer getUAltUtilizador();

    public void setUAltUtilizador(Integer ualtUtilizador);
}

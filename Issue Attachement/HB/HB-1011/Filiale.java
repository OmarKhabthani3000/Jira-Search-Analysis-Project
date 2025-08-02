package de.jdufner;

/*
create table mg_filialaktiv (
  fnr smallint not null primary key,
  ort varchar(50),
  aktiv_kz smallint,
  upprog varchar(50) not null,
  uptimestamp timestamp not null
)

insert into mg_filialaktiv values (1, 'Karlsruhe', 1, 'UNKNOWN', '2004-06-05 00:00:00')
 */
public class Filiale implements java.io.Serializable {

  
  // attributeDeclaration
  
    /**
     * 
     */
          private int filialnummer;
      
    /**
     * 
     */
          private java.lang.String ort;
      
    /**
     * 
     */
          private boolean aktiv;
      
    /**
     * 
     */
          private java.util.Date uptimestamp;
      
    /**
     * 
     */
          private java.lang.String upprog;
        
  
  // associationDeclaration
                  
        /**
         * 
         */
        private java.util.Set vorschlag;
            
  
  // attributeMethods
  
    /**
     * @return The attribute filialnummer
     */
          public int getFilialnummer() {
          return this.filialnummer;
    }

    /**
     * @param filialnummer The attribute filialnummer
     */
          public void setFilialnummer(int filialnummer) {
          this.filialnummer = filialnummer;
    }
  
    /**
     * @return The attribute ort
     */
          public java.lang.String getOrt() {
          return this.ort;
    }

    /**
     * @param ort The attribute ort
     */
          public void setOrt(java.lang.String ort) {
          this.ort = ort;
    }
  
    /**
     * @return The attribute aktiv
     */
          public boolean getAktiv() {
          return this.aktiv;
    }

    /**
     * @param aktiv The attribute aktiv
     */
          public void setAktiv(boolean aktiv) {
          this.aktiv = aktiv;
    }
  
    /**
     * @return The attribute uptimestamp
     */
          public java.util.Date getUptimestamp() {
          return this.uptimestamp;
    }

    /**
     * @param uptimestamp The attribute uptimestamp
     */
          public void setUptimestamp(java.util.Date uptimestamp) {
          this.uptimestamp = uptimestamp;
    }
  
    /**
     * @return The attribute upprog
     */
          public java.lang.String getUpprog() {
          return this.upprog;
    }

    /**
     * @param upprog The attribute upprog
     */
          public void setUpprog(java.lang.String upprog) {
          this.upprog = upprog;
    }
    
  
  // associationMethods
                  
        /**
         *
         */
        public java.util.Set getVorschlag() {
          return this.vorschlag;
        }
    
        /**
         *
         */
        public void setVorschlag(java.util.Set vorschlag) {
          this.vorschlag = vorschlag;
        }
            
  /**
   * @see java.lang.Object#equals(Object other)
   */
  public boolean equals(Object other) {
    if (this == other) {
    return true;
  }
    if (other == null) {
      return false;
    }
    if (this.getClass() == other.getClass()) {
      Filiale that = (Filiale) other;
                      
          if (this.getFilialnummer() != that.getFilialnummer()) return false;
                      
          if (this.getOrt() != null && that.getOrt() != null) {
            if (!this.getOrt().equals(that.getOrt())) return false;
          } else {
            if (this.getOrt() != null || that.getOrt() != null) {
              return false;
            }
          }
                              
          if (this.getAktiv() != that.getAktiv()) return false;
                      
          if (this.getUptimestamp() != null && that.getUptimestamp() != null) {
            if (!this.getUptimestamp().equals(that.getUptimestamp())) return false;
          } else {
            if (this.getUptimestamp() != null || that.getUptimestamp() != null) {
              return false;
            }
          }
                      
          if (this.getUpprog() != null && that.getUpprog() != null) {
            if (!this.getUpprog().equals(that.getUpprog())) return false;
          } else {
            if (this.getUpprog() != null || that.getUpprog() != null) {
              return false;
            }
          }
                                                   // todo
                                                       // todo
                        
      return true;
    }
    return false;
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  public int hashCode() {
      int hashCode = 0;
      try {
                
        hashCode += new java.lang.Integer(this.getFilialnummer()).hashCode();
                      
        if(this.getOrt() != null) {
          hashCode += this.getOrt().hashCode();
        }
                      
        hashCode += new java.lang.Boolean(this.getAktiv()).hashCode();
                      
        if(this.getUptimestamp() != null) {
          hashCode += this.getUptimestamp().hashCode();
        }
                      
        if(this.getUpprog() != null) {
          hashCode += this.getUpprog().hashCode();
        }
      } catch (Exception e) {
        System.out.println("EXCEPTION!!!");
      }
      return hashCode;
  }

  /**
   * @see java.lang.Object#toString()
   */
  public String toString() {
    StringBuffer sb = new StringBuffer(super.toString());
          sb.append(" filialnummer=");
      sb.append(getFilialnummer());
          sb.append(" ort=");
      sb.append(getOrt());
          sb.append(" aktiv=");
      sb.append(getAktiv());
          sb.append(" uptimestamp=");
      sb.append(getUptimestamp());
          sb.append(" upprog=");
      sb.append(getUpprog());
                                    return sb.toString();
  }
  
}


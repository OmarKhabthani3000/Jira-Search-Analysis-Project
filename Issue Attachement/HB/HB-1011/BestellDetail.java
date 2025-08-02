package de.jdufner;

/*
create table MG_BestellDetails (
  fnr smallint not null,
  bestelldatum date not null,
  muenzwert smallint not null,
  anzahl smallint,
  upprog varchar(50) not null,
  uptimestamp timestamp not null
)

insert into mg_bestelldetails values (1, '2004-06-05', 1, 10, 'UNKNOWN', '2004-06-05 00:00:00');
insert into mg_bestelldetails values (1, '2004-06-05', 2, 10, 'UNKNOWN', '2004-06-05 00:00:00');
insert into mg_bestelldetails values (1, '2004-06-05', 5, 10, 'UNKNOWN', '2004-06-05 00:00:00');
 */
public
class BestellDetail
implements java.io.Serializable {

  
  // attributeDeclaration
  
    /**
     * 
     */
          private int muenzwert;
      
    /**
     * <p>
     *  The value to multiply with the number of wreaths (Gebinde). The
     *  number of wreath comes for each Muenzwert from the configuration.
     * </p>
     */
          private int anzahl;
      
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
        private de.jdufner.BestellKopf bestellKopf;
            
  
  // attributeMethods
  
    /**
     * @return The attribute muenzwert
     */
          public int getMuenzwert() {
          return this.muenzwert;
    }

    /**
     * @param muenzwert The attribute muenzwert
     */
          public void setMuenzwert(int muenzwert) {
          this.muenzwert = muenzwert;
    }
  
    /**
     * @return The attribute anzahl
     */
          public int getAnzahl() {
          return this.anzahl;
    }

    /**
     * @param anzahl The attribute anzahl
     */
          public void setAnzahl(int anzahl) {
          this.anzahl = anzahl;
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
        public de.jdufner.BestellKopf getBestellKopf() {
          return this.bestellKopf;
        }
    
        /**
         *
         */
        public void setBestellKopf(de.jdufner.BestellKopf bestellKopf) {
          this.bestellKopf = bestellKopf;
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
      BestellDetail that = (BestellDetail) other;
                      
          if (this.getMuenzwert() != that.getMuenzwert()) return false;
                              
          if (this.getAnzahl() != that.getAnzahl()) return false;
                      
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
                                                
            if (!this.getBestellKopf().equals(that.getBestellKopf())) return false;
                        
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
        hashCode += new java.lang.Integer(this.getMuenzwert()).hashCode();
                      
        hashCode += new java.lang.Integer(this.getAnzahl()).hashCode();
                      
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
          sb.append(" muenzwert=");
      sb.append(getMuenzwert());
          sb.append(" anzahl=");
      sb.append(getAnzahl());
          sb.append(" uptimestamp=");
      sb.append(getUptimestamp());
          sb.append(" upprog=");
      sb.append(getUpprog());
                                    return sb.toString();
  }
  
}

